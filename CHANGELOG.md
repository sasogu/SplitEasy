# Changelog

## 0.1.0 — 2026-10-27

Primera versión pública.

- Selección de dos aplicaciones con icono e nombre.
- Parejas favoritas guardadas localmente.
- Accesos directos anclados con la API oficial de shortcuts.
- Apertura automática en ventanas flotantes cuando el dispositivo declara
  `FEATURE_FREEFORM_WINDOW_MANAGEMENT`, con confirmación del usuario.
- Asistente manual con pasos adaptados a Samsung, Xiaomi/Redmi/POCO, Motorola,
  Pixel y Android genérico.
- Detección aproximada de navegación por gestos o tres botones, corregible por
  el usuario.
- Indicador honesto de compatibilidad (pantalla dividida confirmada,
  multiventana, apertura asistida o no confirmada).
- Interfaz en castellano y valenciano.
- Diseño adaptado a teléfonos, tabletas y orientación horizontal.
- Sin permisos de usuario, sin Internet, sin anuncios, sin telemetría y sin
  servidores.
- 34 pruebas unitarias y verificación en emuladores API 29 y API 35.

Limitaciones conocidas: la apertura automática de dos aplicaciones arbitrarias
no es posible con APIs públicas; las instrucciones de fabricantes son
orientativas; las ventanas flotantes no se han probado en hardware real. Véase
`docs/LIMITATIONS.md`.
