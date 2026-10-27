# SplitEasy 0.1.0

Primera versión pública de SplitEasy: un gestor sencillo para preparar parejas
de aplicaciones y abrir la pantalla dividida con el menor número de pasos
posible.

## Descarga

- **APK:** `SplitEasy-0.1.0.apk`
- **Paquete:** `es.sasogu.spliteasy`
- **Android mínimo:** 10 (API 29)
- **Firma:** `CN=Samuel Soriano, OU=SplitEasy, O=SplitEasy, L=Elche, ST=Alicante, C=ES`
- **SHA-256:** `d303c5cefeeee28b74060ad28fb91901473f7b84d39b64b842ea231dead554a6`

Comprobación después de descargar:

```bash
sha256sum SplitEasy-0.1.0.apk
adb install -r SplitEasy-0.1.0.apk
```

## Novedades

- Selección de dos aplicaciones con nombre e icono.
- Parejas favoritas guardadas solo en el dispositivo.
- Accesos directos anclados en la pantalla de inicio.
- Intento de ventanas flotantes en dispositivos que declaran
  `FEATURE_FREEFORM_WINDOW_MANAGEMENT`.
- Asistente manual con instrucciones para Samsung, Xiaomi/Redmi/POCO,
  Motorola, Pixel y Android genérico.
- Detección aproximada de gestos o tres botones, corregible.
- Interfaz en castellano y valenciano.
- Diseño adaptado a teléfonos, tabletas y horizontal.
- Sin permisos de usuario, sin Internet, sin anuncios, sin telemetría y sin
  servidores.
- 34 pruebas unitarias y verificación en emuladores Android 10 y Android 15.

## Limitaciones conocidas

- Android no permite que una aplicación normal coloque dos aplicaciones
  cualesquiera en pantalla dividida mediante APIs públicas. SplitEasy intenta
  ventanas flotantes cuando el sistema lo permite y, si no, guía paso a paso.
  No usa APIs ocultas, reflexión, accesibilidad ni root.
- Las rutas de los fabricantes son orientativas y pueden cambiar según el
  modelo y la versión.
- Las ventanas flotantes no se han probado en hardware real; el emulador
  estándar no declara esa función.
- Algunas aplicaciones no admiten multiventana y se abrirán a pantalla completa.
- No hay copia de seguridad en la nube: las parejas se guardan solo en este
  dispositivo.

Consulta `docs/LIMITATIONS.md`, `docs/COMPATIBILITY.md` y `docs/TESTING.md` en
el repositorio para el análisis completo.

## Licencia

MIT. Copyright (c) 2026 Samuel Soriano.
