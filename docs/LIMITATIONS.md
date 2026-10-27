# Limitaciones técnicas

Esta es la parte más importante del proyecto: distinguir lo que Android permite
realmente de lo que solo funciona en apps del sistema. SplitEasy se ha diseñado
alrededor de esa frontera.

## 1. Colocar dos aplicaciones arbitrarias en pantalla dividida no es posible con APIs públicas

Android no ofrece a una aplicación normal una API para decir «pon A y B en
pantalla dividida». Las vías que sí existen son internas, están ocultas o
dependen de privilegios:

### `ActivityOptions.setLaunchWindowingMode()`

En AOSP (`core/java/android/app/ActivityOptions.java`, rama principal a
2026-10-27) el método está marcado:

```java
/**
 * Sets the windowing mode the activity should launch into.
 * @hide
 */
@TestApi
public void setLaunchWindowingMode(int windowingMode) { ... }
```

No aparece en la referencia pública de Android. No es invocable desde el SDK,
no se puede usar con reflexión de forma fiable (restricciones de APIs ocultas)
y no serviría aunque se invocara: el sistema comprueba privilegios de gestión de
tareas. SplitEasy **no** lo usa.

### `ActivityOptions.setLaunchBounds()`

Es público (`ActivityOptions.java`), pero solo aplica límites en modo freeform o
Picture-in-Picture:

- `ActivityTaskSupervisor.canUseActivityOptionsLaunchBounds()` exige
  `mSupportsFreeformWindowManagement` o PiP.
- `TaskLaunchParamsModifier` cambia a `WINDOWING_MODE_FREEFORM` solo si la
  política de freeform está disponible.

Por eso SplitEasy usa esta vía únicamente cuando el dispositivo declara
`FEATURE_FREEFORM_WINDOW_MANAGEMENT`. Incluso entonces:

- El sistema puede ignorar los límites.
- El fabricante puede recolocar o apilar las ventanas.
- La app destino puede ser no redimensionable y abrirse a pantalla completa.
- El segundo lanzamiento puede quedar bloqueado por las restricciones de
  actividad en segundo plano.

La app pide confirmación al usuario en lugar de dar por hecho el resultado.

### `FLAG_ACTIVITY_LAUNCH_ADJACENT`

La bandera es pública, pero no crea la pareja A+B por sí sola:

- `ActivityStarter` la ignora si falta `FLAG_ACTIVITY_NEW_TASK` o si no hay una
  actividad de origen (`mSourceRecord`).
- `TaskDisplayArea.getLaunchRootTask()` solo la dirige a una tarea adyacente
  creada por el sistema (`mLaunchAdjacentFlagRootTask` / tareas creadas por un
  organizador), no a una pareja de apps elegida por el usuario.

Cuando SplitEasy lanza B con esta bandera, el origen es la propia actividad de
SplitEasy, no A. En el mejor caso podría dejar SplitEasy junto a B; no A junto a
B. Por eso esta bandera no es la solución y solo se ofrece como prueba
experimental, desactivada por omisión, con un aviso explícito.

### Lo que sí es público y se usa

- `Intent` explícito a la actividad de lanzador.
- `startActivity` / `startActivities` con `FLAG_ACTIVITY_NEW_TASK`.
- `ActivityOptions.setLaunchBounds` en dispositivos freeform.
- `Activity.onMultiWindowModeChanged` para saber si la propia app entró en
  multiventana.
- `ShortcutManagerCompat` para accesos directos.
- `PackageManager.hasSystemFeature(FEATURE_FREEFORM_WINDOW_MANAGEMENT)`.

## 2. No hay una API pública de «¿este dispositivo admite pantalla dividida?»

`PackageManager` no expone una característica de pantalla dividida. SplitEasy
no puede consultar algo como `FEATURE_SPLIT_SCREEN`. Por eso usa:

- `FEATURE_FREEFORM_WINDOW_MANAGEMENT` para saber si puede intentar ventanas.
- `ActivityManager.isLowRamDevice` como señal negativa, no como prueba.
- `onMultiWindowModeChanged` como evidencia real de que la app entró en
  multiventana.
- La confirmación del usuario tras la guía.

El nivel «Pantalla dividida compatible» solo aparece después de una evidencia
real. La versión de Android no se usa como prueba.

## 3. No se puede saber de antemano si una app concreta es redimensionable

`ActivityInfo.resizeMode` y `supportsPictureInPicture()` son `@hide`. No hay un
equivalente público fiable. Algunas apps declaran `resizeableActivity="false"` y
el sistema no ofrece «Pantalla dividida» para ellas.

Consecuencia: SplitEasy no puede clasificar la lista de apps en «compatible» e
«incompatible». Avisa de la posibilidad, sugiere probar con la otra app y no
presenta un fallo de la pareja como error de SplitEasy.

## 4. Restricciones de lanzamiento en segundo plano

Desde Android 10, una app en segundo plano no puede iniciar actividades
arbitrarias. Android concede una ventana de gracia breve tras una actividad
reciente y algunos fabricantes la modifican; las versiones más nuevas tienen
indicadores de seguridad que pueden desactivarla.

En el intento freeform se lanza A y, unos milisegundos después, B. Si el sistema
bloquea el segundo lanzamiento, el usuario verá solo una app. Por eso:

- La app pregunta qué ha pasado en lugar de afirmar que funcionó.
- La guía manual es el camino principal en dispositivos sin freeform.
- No se usa un servicio en primer plano, una burbuja ni el permiso de overlay
  para saltarse la restricción: añadirían complejidad y permisos sin garantía.

## 5. Diferencias entre fabricantes

Samsung, Xiaomi, Motorola, Google y el Android de AOSP comparten la idea de
Recientes, pero cambian nombres de menú, iconos y en algunos casos el gesto.
SplitEasy muestra rutas orientativas y una alternativa general. No inventa rutas
de Ajustes que no pueda verificar. Cuando el fabricante no está identificado, se
usa la guía genérica.

Tampoco se puede afirmar que una ruta siga existiendo en la próxima versión de
la capa del fabricante. La app está pensada para actualizarse con facilidad: los
textos están en recursos y el detector de fabricante es una función pura.

## 6. Detección de gestos o tres botones

No hay una API pública que diga «el usuario usa gestos». SplitEasy estima el
modo por los insets de la barra de navegación:

- ~24-32 dp suele corresponder a gestos.
- 40-48 dp suele corresponder a tres botones.
- 0 px puede ser gestos con el indicador oculto o un modo desconocido.

Es una sugerencia, no una certeza. El usuario puede corregirla y la elección se
guarda. Esto solo afecta a las instrucciones, no a la compatibilidad.

## 7. Accesos directos

`ShortcutManagerCompat.requestPinShortcut` depende de que el lanzador soporte la
API de shortcuts anclados y de que el usuario acepte el diálogo del sistema. Si
no se puede, la app lo comunica. En los dispositivos sin apertura automática, el
acceso directo abre la guía de la pareja, no una pareja ya dividida.

## 8. Visibilidad de paquetes

SplitEasy declara en el manifiesto:

```xml
<queries>
    <intent>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent>
</queries>
```

No usa `QUERY_ALL_PACKAGES`. Consecuencias:

- Solo aparecen apps con actividad de lanzador.
- Las apps de un perfil de trabajo no se incluyen.
- Una app sin actividad de lanzador no se puede seleccionar.
- Algunas apps del sistema con lanzador pueden aparecer; es intencionado.

## 9. Privacidad y almacenamiento

- Sin permiso de Internet, sin red, sin analítica.
- Sin permisos de usuario; AndroidX añade un permiso interno de firma para
  receptores dinámicos, que no se muestra al usuario.
- Favoritos en `SharedPreferences`; sin copia de seguridad en la nube ni
  transferencia entre dispositivos.
- No se lee el contenido de otras aplicaciones.

## 10. Límites de la confirmación del usuario

La apertura automática se confirma con una pregunta. Es una decisión de diseño
honesta: el sistema no expone el modo de ventana de otras apps. Si el usuario
responde mal, la app puede guardar una evidencia incorrecta; por eso la
detección se puede restablecer y el estado de pantalla dividida también se puede
confirmar por la vía del sistema (`onMultiWindowModeChanged`) cuando la propia
app se coloca en multiventana.

## 11. Qué requeriría privilegios del sistema

Para abrir A+B de forma garantizada harían falta, según la versión:

- `MANAGE_ACTIVITY_TASKS` o firma de plataforma para controlar tareas.
- APIs internas como `setLaunchWindowingMode`, `setLaunchRootTask` o
  `TaskFragment` organizados por el sistema.
- Posiblemente acceso a `ActivityTaskManager` y permisos de organizador.

Ese camino queda fuera del alcance de una app instalable desde una tienda o un
APK y no se implementa. SplitEasy prefiere ser pequeña, estable y honesta antes
que depender de trucos que se rompen con cada versión.

## 12. Resumen de lo que el usuario debe esperar

- En un teléfono normal: elegir pareja, abrir la guía, hacer 3-4 pasos del
  sistema y confirmar. Las siguientes veces, el favorito o el acceso directo
  ahorran la selección.
- En un dispositivo con ventanas flotantes: intento automático y confirmación.
- En un dispositivo sin multiventana: la app no promete lo imposible y lo dice.
