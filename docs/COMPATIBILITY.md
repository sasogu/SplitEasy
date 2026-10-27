# Matriz de compatibilidad

Fecha de la última revisión: 2026-10-27. Versión de SplitEasy: 0.1.1.

## Cómo leer esta matriz

- **Probado (A)**: comprobado en el emulador o dispositivo indicado durante el
  desarrollo de esta versión.
- **AOSP**: comportamiento del Android abierto y del flujo estándar de
  Recientes. Puede variar en capas de fabricante.
- **Orientativo**: ruta publicada por el fabricante o de uso común, no
  verificada en todos los modelos. Las rutas cambian entre versiones.
- **No**: no disponible o no automatizable con APIs públicas.

SplitEasy nunca usa la versión de Android como prueba de compatibilidad. La
pantalla dividida puede estar deshabilitada en un Android moderno o existir en
un dispositivo antiguo con una capa de fabricante.

## Por versión de Android

| Versión | API | Pantalla dividida manual | Apertura automática de A+B | Notas |
| --- | --- | --- | --- | --- |
| Android 10 | 29 | Normalmente sí (AOSP; menú verificado A) | Ventanas flotantes con opciones de desarrollo (probado A) | Probado el flujo de guía y dos ventanas freeform con Calendar+Clock; Camera no era redimensionable. |
| Android 11 | 30 | Normalmente sí (AOSP) | No, salvo ventanas flotantes | No probado en esta versión. |
| Android 12 / 12L | 31 / 32 | Normalmente sí (AOSP) | Solo en dispositivos con `FEATURE_FREEFORM_WINDOW_MANAGEMENT` | Android 12L añade mejoras para pantallas grandes; no hay API pública de «app pairs». |
| Android 13 | 33 | Normalmente sí (AOSP) | Solo ventanas flotantes | Activity Embedding sirve para actividades de la misma app, no para embeber apps ajenas. |
| Android 14 | 34 | Normalmente sí (AOSP) | Solo ventanas flotantes | Las «parejas de apps» que se ven en algunos dispositivos son funciones del fabricante. |
| Android 15 | 35 | Normalmente sí (AOSP; interfaz probada A) | Ventanas flotantes / escritorio; también con opciones de desarrollo | Probado en emulador: dos apps en ventanas flotantes, selección, favoritos, idiomas, tableta y accesos directos. |
| Android 16 | 36 | Normalmente sí (AOSP) | Ventanas flotantes / escritorio en pantallas grandes | No probado. La app usa `targetSdk 35` y se ejecuta en modo de compatibilidad. |

«Normalmente sí» no es una promesa: algunos dispositivos Android Go, con poca
RAM o con capas que lo desactivan pueden no ofrecer pantalla dividida. En ese
caso SplitEasy muestra «Multiventana no disponible o no confirmada» cuando
detecta poca RAM y no tiene evidencia.

## Por tipo de dispositivo

| Dispositivo | Pantalla dividida | Ventanas flotantes / automático | Notas |
| --- | --- | --- | --- |
| Teléfono estándar | Sí, desde Recientes (AOSP) | Normalmente no (`FEATURE_FREEFORM_WINDOW_MANAGEMENT` ausente) | La vía principal es la guía. |
| Tableta Android 12L+ | Sí | Puede estar disponible | Si el sistema declara freeform, SplitEasy intenta colocar dos ventanas y pide confirmación. |
| Plegable | Sí, el sistema decide el reparto | Poco frecuente | El pliegue y el tamaño de cada mitad los controla el sistema; no se ha probado en hardware. |
| Samsung DeX / escritorio | Sí | Freeform propio de DeX | No verificado; `setLaunchBounds` puede ser ignorado o recolocado. |
| Chromebook con apps Android | Ventanas del sistema | Freeform del entorno | No verificado. |
| Android Go / poca RAM | Puede no existir | No | La app evita afirmar compatibilidad sin evidencia. |

## Por fabricante

Las siguientes rutas son **orientativas**. No se ha inventado ningún menú de
Ajustes: todas parten del flujo de Recientes. Si una ruta no coincide, la guía
general de SplitEasy sigue siendo válida.

| Fabricante / capa | Ruta habitual | Matices |
| --- | --- | --- |
| Samsung (One UI) | Recientes → tocar el icono de la app → «Abrir en vista de pantalla dividida» | Algunos modelos ofrecen «Pareja de apps» en el panel lateral. Es otra ruta, no siempre disponible. En DeX hay ventanas flotantes. |
| Xiaomi, Redmi, POCO (MIUI / HyperOS) | Recientes → mantener pulsada la tarjeta → icono de dos rectángulos | La posición del icono cambia entre versiones; en algunas está en la barra superior. |
| Motorola (My UX) | Recientes → mantener pulsado el icono → «Pantalla dividida» | El nombre puede variar ligeramente según la versión. |
| Google Pixel | Recientes → mantener pulsado el icono → «Pantalla dividida» | Flujo estándar de AOSP. |
| Android genérico (AOSP) | Recientes → mantener pulsado el icono → «Split screen» / «Pantalla dividida» | Probado en Android 10: la opción aparece para apps redimensionables y no para Camera. |
| Otros fabricantes | Probar los pasos generales | Si el fabricante ha movido la opción, SplitEasy no puede adivinarla; muestra los pasos generales y remite a la ayuda del dispositivo. |

En Android 10, al elegir «Split screen» la segunda mitad puede mostrar la
pantalla de inicio o el cajón de aplicaciones, no una lista de Recientes. Por
eso la guía indica que se busque la aplicación en esa mitad si aparece el
cajón.

## Aplicaciones que no admiten multiventana

No hay una API pública fiable para saber si una actividad concreta es
redimensionable: `ActivityInfo.resizeMode` es `@hide`. SplitEasy no etiqueta
las apps como compatibles o incompatibles. En su lugar:

- Avisa de que algunas aplicaciones se abrirán a pantalla completa.
- En el paso 3 indica que, si una aplicación no muestra la opción de pantalla
  dividida, se pruebe con la otra.
- Si el sistema no ofrece la opción para ninguna de las dos, la pareja no se
  puede dividir en ese dispositivo.

Ejemplo comprobado en el emulador Android 10: Calendar muestra «Split screen»;
Camera no lo muestra. Es un comportamiento esperado, no un fallo de SplitEasy.

## Detección de navegación

Android no expone si el usuario usa gestos o tres botones. SplitEasy estima el
modo a partir de los insets de la barra de navegación (24-32 dp suele ser gestos;
40 dp o más, tres botones) y permite corregirlo. Esta estimación no afecta a la
compatibilidad; solo elige el dibujo y el texto del paso 2.

## Cajas de coche con CarPlay / T-Box

Las cajas Carlinkit T-Box no tienen Recientes en su launcher. La vía que se ha
verificado en emulador es activar las opciones de desarrollador
`enable_freeform_support` y `force_resizable_activities`, reiniciar y lanzar dos
ventanas flotantes. En el emulador Android 15 se abrieron Calendar y Camera en
dos ventanas, en horizontal y en vertical. Queda pendiente probarlo en una caja
T-Box física; si el firmware bloquea esas opciones, no hay una vía pública desde
una app. Instrucciones en [`CARPLAY.md`](CARPLAY.md).

## Resumen de evidencia de la versión 0.1.1

| Elemento | Estado |
| --- | --- |
| APK debug y release en Android 15 (API 35) | Probado |
| APK release 0.1.1 en Android 10 (API 29) y Android 15 (API 35) | Probado |
| Selección de pareja, favoritos y guía | Probado |
| Acceso directo anclado y apertura de la pareja | Probado en API 35 |
| Castellano y valenciano | Probado |
| Detección de gestos y tres botones | Probado en API 35 |
| Diseño de tableta (ancho simulado) | Probado |
| Pantalla dividida real completada de extremo a extremo en emulador | No completada por automatización; se verificó el menú del sistema |
| Ventanas flotantes con opciones de desarrollo | Probado en API 35 (Calendar+Camera) y API 29 (Calendar+Clock) |
| Caja T-Box física | Pendiente |
| Teléfonos físicos Samsung, Xiaomi, Motorola, Pixel | Pendiente |
| Android 12, 13, 14 y 16 | Pendiente |
| 38 pruebas unitarias | Probado |
| TalkBack y escala de fuente al 200 % | Pendiente de revisión manual |

Los detalles de las pruebas y el plan pendiente están en
[`TESTING.md`](TESTING.md).
