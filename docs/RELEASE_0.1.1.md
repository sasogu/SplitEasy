# SplitEasy 0.1.1

Versión enfocada en cajas Android para coche con CarPlay, como la **Carlinkit
CPC200-Tbox / T-Box S**, que no tienen Recientes y en las que la guía manual de
pantalla dividida no sirve.

## Descarga

- **APK:** `SplitEasy-0.1.1.apk`
- **Paquete:** `es.sasogu.spliteasy`
- **Android mínimo:** 10 (API 29)
- **Firma:** `CN=Samuel Soriano, OU=SplitEasy, O=SplitEasy, L=Elche, ST=Alicante, C=ES`
- **SHA-256:** `0afeebacc166a69eb026a121cb40772b0d7e9dbd188f92b2c4da3249ef8ed959`

```bash
sha256sum SplitEasy-0.1.1.apk
adb install -r SplitEasy-0.1.1.apk
```

La 0.1.1 usa la misma clave que la 0.1.0, así que actualiza encima sin
desinstalar. Si tenías instalado el APK de depuración, desinstala antes.

## Novedades

- **Modo CarPlay (sin Recientes)**: asistente específico para cajas T-Box.
- Detección de `enable_freeform_support` y `force_resizable_activities` en
  Opciones de desarrollador.
- Intento de apertura en dos ventanas flotantes con límites, tarea nueva y modo
  freeform, aunque el sistema no declare `FEATURE_FREEFORM_WINDOW_MANAGEMENT`.
- Botón para abrir Opciones de desarrollador, comprobar de nuevo el estado y
  ver/copiar comandos ADB.
- Diagnóstico en Ayuda: modelo, API, freeform del sistema, opción de
  desarrollador, forzar redimensionables y modo CarPlay.
- Ajuste de límites con los insets del sistema para que las ventanas no queden
  cortadas.
- 38 pruebas unitarias.

## Activar ventanas flotantes en la caja

1. Ajustes → Acerca de → toca 7 veces «Número de compilación».
2. Opciones de desarrollador → activa **Forzar actividades redimensionables** y
   **Habilitar ventanas flotantes**.
3. Reinicia la caja.
4. Abre SplitEasy → Ayuda → **Cajas CarPlay / T-Box**; comprueba el diagnóstico
   y activa **Modo CarPlay** si no se activó solo.
5. Elige dos apps y pulsa **Abrir en dos ventanas**.

Si no aparecen las opciones, con la caja conectada por USB y depuración USB
activada:

```bash
adb shell settings put global enable_freeform_support 1
adb shell settings put global force_resizable_activities 1
adb reboot
```

## Pruebas

- 38 pruebas unitarias JVM.
- Android 10 (API 29): APK release 0.1.1, flujo principal y dos ventanas
  flotantes reales (Calendar + Clock). Camera se cerró por no ser redimensionable
  en esa versión; la app ya avisa de ese caso.
- Android 15 (API 35): APK debug y release 0.1.1, dos ventanas flotantes reales
  (Calendar + Camera) en horizontal y vertical, detección de opciones de
  desarrollador, modo CarPlay, diagnóstico y tarjeta de configuración.
- **Pendiente:** caja T-Box física, integración real con CarPlay, tacto en la
  pantalla del coche y firmware de cada fabricante.

## Limitaciones

- El firmware de la caja puede bloquear las opciones de desarrollador o ignorar
  el modo freeform. En ese caso ninguna app instalable puede poner dos apps lado
  a lado.
- El modo ventanas usa la clave serializada interna
  `android.activity.windowingMode` de `ActivityOptions`; no es una API pública y
  puede cambiar. No usa reflexión, root ni permisos. Véase
  `docs/LIMITATIONS.md`.
- Algunas apps con `singleTask` o protecciones del fabricante pueden abrirse a
  pantalla completa.
- Es necesario reiniciar después de activar las opciones.
- Las ventanas tienen barras de título del sistema.

## Licencia

MIT. Copyright (c) 2026 Samuel Soriano.
