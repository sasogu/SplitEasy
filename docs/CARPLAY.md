# CarPlay y cajas T-Box

Las cajas **Carlinkit CPC200-Tbox / T-Box S** y similares ejecutan Android y
proyectan su pantalla sobre la pantalla del coche mediante CarPlay o Android
Auto. No son televisiones Android y su launcher de coche suele carecer de
**Recientes** y de la opción nativa de pantalla dividida. Por eso SplitEasy no
puede guiarte por el flujo normal de Recientes en estos equipos.

La alternativa es usar **ventanas flotantes de Android**. Android las trae desde
hace años, pero muchos firmwares de coche las ocultan. Se pueden habilitar desde
Opciones de desarrollador y, tras reiniciar, SplitEasy puede colocar dos
aplicaciones en dos ventanas independientes.

## Requisitos

- Android 10 o posterior. Si SplitEasy 0.1.x se ha instalado, la caja ya cumple
  este requisito (la app usa `minSdk 29`).
- Opciones de desarrollador accesibles.
- Las opciones **Forzar actividades redimensionables** y **Habilitar ventanas
  flotantes** activadas.
- Un reinicio después de activarlas: Android lee estas opciones al arrancar.

## Activar las ventanas flotantes

### Desde la caja

1. Ajustes → Acerca de.
2. Toca 7 veces **Número de compilación**.
3. Vuelve y entra en **Opciones de desarrollador** (puede estar en Sistema o en
   Opciones avanzadas).
4. Activa **Forzar actividades redimensionables**.
5. Activa **Habilitar ventanas flotantes** (el nombre puede variar; si no
   aparece, usa ADB).
6. Reinicia la caja.

### Desde un ordenador con ADB

Con la caja conectada por USB y la **depuración USB** activada:

```bash
adb devices
adb shell settings put global enable_freeform_support 1
adb shell settings put global force_resizable_activities 1
adb reboot
```

Después del reinicio:

```bash
adb shell settings get global enable_freeform_support
adb shell settings get global force_resizable_activities
```

Ambos deben devolver `1`. En SplitEasy, Ayuda → **Cajas CarPlay / T-Box** muestra
un diagnóstico con el modelo, la API, la función del sistema, la opción de
desarrollador, la opción de forzar redimensionables y el modo CarPlay.

## Usar SplitEasy en la caja

1. Abre SplitEasy.
2. Ayuda → **Cajas CarPlay / T-Box** → activa **Modo CarPlay** si no se activó
   solo. El modo se activa automáticamente si el modelo parece una caja
   Carlinkit/T-Box o si detecta las opciones de desarrollador.
3. Elige la aplicación A y la B.
4. Pulsa **Abrir las dos**.
5. Pulsa **Abrir en dos ventanas**.

En una pantalla horizontal, SplitEasy coloca una ventana a la izquierda y otra a
la derecha. Cada ventana conserva su barra de título de Android para moverla,
maximizarla o cerrarla. No se necesita Recientes.

## Cómo funciona

- SplitEasy lee `enable_freeform_support` y `force_resizable_activities` de
  `Settings.Global`, sin permisos.
- Si están activas, considera que el dispositivo admite ventanas flotantes
  aunque no declare `FEATURE_FREEFORM_WINDOW_MANAGEMENT`.
- Lanza cada app con una tarea nueva, límites de pantalla y el modo freeform.
- Para forzar el modo freeform añade al `Bundle` de `ActivityOptions` la clave
  serializada `android.activity.windowingMode` con valor `5`. Es la misma clave
  que usa internamente `ActivityOptions.setLaunchWindowingMode()`; no se usa
  reflexión ni se piden permisos. Es un protocolo interno que puede cambiar en
  el futuro; por eso el modo es experimental.
- Los límites se calculan con márgenes y el hueco de las barras del sistema para
  que las ventanas no queden cortadas.

## Limitaciones y avisos

- **No es una función oficial del fabricante**: si el firmware ignora o bloquea
  freeform, SplitEasy no puede hacer nada. Ninguna app instalable puede forzar
  dos apps lado a lado en ese caso.
- Hace falta **reiniciar** después de activar las opciones.
- Algunas apps usan `singleTask` o se resisten a abrirse en una tarea nueva;
  pueden aparecer a pantalla completa o reutilizar una tarea antigua.
- Las ventanas tienen barras de título del sistema. En una pantalla táctil de
  coche se pueden mover y cerrar, pero no son tan limpias como la pantalla
  dividida nativa.
- No hay confirmación fiable de que las dos apps se hayan abierto: el sistema no
  expone el modo de ventana de otras apps. La app conserva el estado y permite
  reintentar.
- El modo CarPlay se probó en emuladores **Android 15** (Calendar + Camera) y
  **Android 10** (Calendar + Clock) con las dos opciones de desarrollador y
  reinicio: se abrieron dos ventanas flotantes, en horizontal y en vertical.
  **No se ha probado aún en una T-Box física.** El resultado depende del firmware de la caja.
- En Android 9 las versiones 0.1.x no se instalan (`minSdk 29`). Si tu T-Box
  original tiene Android 9, no es compatible con esta compilación.

## Si no funciona

1. Comprueba el diagnóstico de la app: las dos opciones deben estar en **Sí**.
2. Reinicia la caja después de activarlas.
3. Prueba con dos apps sencillas (Ajustes + Reloj).
4. Si solo se abre una, vuelve a SplitEasy y pulsa **Reintentar apertura**.
5. Si nada funciona, conecta la caja por USB, activa depuración USB y revisa:

```bash
adb devices
adb shell getprop ro.build.version.release
adb shell settings get global enable_freeform_support
adb shell settings get global force_resizable_activities
```

Si el fabricante ha deshabilitado estas claves o el sistema no admite
multiventana, no hay una vía pública desde una aplicación.
