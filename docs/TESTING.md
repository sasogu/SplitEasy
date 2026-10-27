# Pruebas

## Pruebas automatizadas

```bash
./gradlew :app:testDebugUnitTest
```

38 pruebas de JVM, sin emulador:

| Clase | Pruebas | Qué cubre |
| --- | --- | --- |
| `CapabilityEvaluatorTest` | 10 | Evidencia de pantalla dividida, freeform, opciones de desarrollador, poca RAM, fallos y versiones modernas sin confirmación. |
| `SplitBoundsCalculatorTest` | 6 | Mitades en horizontal y vertical, márgenes, hueco, píxeles impares y casos inválidos. |
| `NavigationModeEstimatorTest` | 5 | Insets de gestos, tres botones, landscape y umbrales. |
| `VendorDetectorTest` | 5 | Samsung, Xiaomi/Redmi/POCO, Motorola, Pixel y genérico. |
| `AppSearchTest` | 6 | Tildes, mayúsculas, nombre de paquete, prioridad de prefijo y consulta vacía. |
| `PairCodecTest` | 6 | Ida y vuelta, Unicode, tabuladores, cabecera incorrecta y líneas corruptas. |

También se ejecuta:

```bash
./gradlew :app:lintDebug
```

La versión 0.1.1 termina con 0 errores de lint. Los avisos restantes son
sugerencias de dependencias actualizadas, recursos no usados y KTX.

## Pruebas manuales realizadas

### Emulador Android 15 (API 35), APK debug y release

- Instalación e inicio sin bloqueos.
- Selección de primera y segunda aplicación; avance automático al segundo
  selector.
- Búsqueda tolerante a tildes y mayúsculas.
- Prevención de elegir la misma app en las dos mitades.
- Guardado de pareja, estado «Pareja guardada» y persistencia tras reiniciar.
- Guía manual, selector de gestos/tres botones y diagramas.
- Confirmación «Ya las tengo abiertas» y actualización del indicador a
  «Pantalla dividida compatible».
- Creación de acceso directo anclado en el lanzador AOSP y apertura de la
  pareja desde el acceso directo.
- Cambio de idioma a valenciano con `cmd locale set-app-locales` y revisión de
  textos.
- Cambio de navegación con el overlay `gestural`: el selector pasó de «Tres
  botones» a «Gestos».
- Diseño de tableta con `wm size 2000x1200` y `wm density 240`: paneles en dos
  columnas.
- APK release 0.1.1 instalado y probado con R8.

### Ventanas flotantes en Android 15 (API 35)

- `adb shell settings put global enable_freeform_support 1`
- `adb shell settings put global force_resizable_activities 1`
- Reinicio del emulador.
- SplitEasy detectó las opciones, mostró «Multiventana compatible» y el modo
  CarPlay; el diagnóstico mostró sistema: No, desarrollador: Sí, forzar: Sí.
- Dos apps (Calendar y Camera) se abrieron en dos tareas freeform con límites
  distintos.
- En horizontal quedaron izquierda/derecha; en vertical, arriba/abajo.
- Se ajustaron los límites con los insets del sistema para que las ventanas no
  quedaran cortadas.
- La segunda app se lanzó correctamente desde segundo plano con una tarea nueva.
- La tarjeta de configuración del modo CarPlay se mostró al desactivar las
  opciones y se pudo abrir Opciones de desarrollador / comprobar de nuevo.

### Emulador Android 10 (API 29), APK release 0.1.1

- Instalación del APK firmado y arranque sin errores de R8.
- Selección de aplicaciones, guardado de pareja, guía, confirmación y cambio del
  indicador de compatibilidad.
- Selector de aplicaciones, búsqueda y ayuda.
- Pantalla de tableta simulada con `wm size` y `wm density`.
- Comprobación del flujo de Recientes de AOSP: al mantener pulsado Calendar
  aparece «Split screen»; en Camera no aparece porque no admite redimensionado.
  Es un caso real que la app documenta como advertencia.
- Tras elegir «Split screen», la segunda mitad puede mostrar la pantalla de
  inicio; desde ahí se abre el cajón de aplicaciones. La guía ya indica que se
  busque allí la otra app.

### Lo que no se ha podido completar en automatización

- **División real de dos apps de extremo a extremo**: el emulador AOSP permitió
  abrir el menú «Split screen» y el selector de la segunda mitad, pero la
  selección automatizada desde el cajón abrió la app a pantalla completa. La
  guía se verificó contra el menú real del sistema. Es una limitación del
  entorno de prueba, no una afirmación de que la guía falle en un dispositivo
  físico.
- **Caja T-Box física**: pendiente. La ruta freeform funciona en el emulador,
  pero el firmware de la caja puede bloquear las opciones o el protocolo de
  ventana.
- **Fabricantes reales**: no se probó en Samsung, Xiaomi, Motorola ni Pixel
  físicos. Las rutas son orientativas.
- **TalkBack y escala de fuente al 200 %**: la interfaz usa descripciones y
  tamaños mínimos, pero no se hizo una pasada manual con lector de pantalla.
- **Android 12, 13, 14 y 16**: pendientes. La app compila con SDK 35 y usa APIs
  disponibles desde API 29.

## Plan de pruebas pendiente para dispositivos físicos

1. Instalar el APK release y comprobar la firma y el hash.
2. Repetir el flujo en una caja Carlinkit T-Box física con CarPlay y comprobar
   las dos ventanas, el tacto y el reinicio.
3. Repetir el flujo con navegación por gestos y con tres botones.
4. Probar parejas: navegador + notas, correo + calendario, YouTube + notas y una
   app de banca (posible no redimensionable).
5. Comprobar una app sin actividad de lanzador (no debe aparecer) y una app que
   no admita intents externos.
6. Probar una app no redimensionable y verificar el aviso del paso 3.
7. Confirmar si el acceso directo anclado abre la guía o la pareja.
8. Repetir en tableta/plegable y, si existe, en DeX/escritorio.
9. Revisar TalkBack, fuente al 200 %, modo oscuro y rotación.
10. Revisar los textos valencianos en un dispositivo con idioma valenciano o
    mediante el selector de idioma por aplicación.
11. Documentar el resultado en `COMPATIBILITY.md` antes de cambiar la versión.

## Comprobaciones del APK

```bash
apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
aapt2 dump permissions app/build/outputs/apk/release/app-release.apk
sha256sum app/build/outputs/apk/release/app-release.apk
```

Resultado esperado de permisos: solo el permiso interno de firma de AndroidX
para receptores dinámicos, sin permisos de usuario ni `QUERY_ALL_PACKAGES`.
