# Changelog

## 0.1.1 — 2026-10-27

- Modo CarPlay para cajas como Carlinkit T-Box, que no tienen Recientes.
- Detección de ventanas flotantes activadas desde Opciones de desarrollador
  (`enable_freeform_support` + `force_resizable_activities`).
- Asistente específico para activar ventanas flotantes, reiniciar la caja,
  comprobar el estado y ver/copiar comandos ADB.
- Diagnóstico de compatibilidad en Ayuda (modelo, API, freeform, modo CarPlay).
- Lanzamiento freeform con límites de ventana y tarea nueva, necesario cuando
  el sistema no aplica los límites a una tarea existente.
- Prueba en emuladores Android 15 (Calendar + Camera) y Android 10
  (Calendar + Clock): dos aplicaciones en ventanas flotantes colocadas
  arriba/abajo (vertical) o izquierda/derecha (horizontal).
- 38 pruebas unitarias.

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
