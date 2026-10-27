# AGENTS — SplitEasy

## Comandos

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
./gradlew :app:lintDebug
```

El resultado de depuración está en `app/build/outputs/apk/debug/app-debug.apk`.
El de distribución, en `app/build/outputs/apk/release/app-release.apk`.

## Reglas del proyecto

- Android 10+ (minSdk 29), Kotlin, Jetpack Compose y Material 3.
- Solo APIs públicas de Android. No reflexión, no APIs `@hide`/`@TestApi`, no
  accesibilidad, no root, no shell.
- No añadir dependencias sin justificarlo. Evitar red, analítica y publicidad.
- Mantener la paridad de claves entre `values/strings.xml` y
  `values-va/strings.xml`. El castellano es el recurso por defecto.
- Mantener la lógica de dominio pura en `domain/` y cubierta por pruebas JVM.
- No prometer compatibilidad de pantalla dividida por la versión de Android.
  La interfaz debe seguir usando evidencia y estados honestos.
- No incluir claves, keystores ni `keystore.properties` en el repositorio.
- Actualizar `docs/COMPATIBILITY.md`, `docs/LIMITATIONS.md` y
  `docs/TESTING.md` cuando cambie el comportamiento o se pruebe un dispositivo.
- Licencia MIT. No cambiar la licencia sin decisión expresa del autor.

## Modo CarPlay / T-Box

- No eliminar el modo CarPlay ni el diagnóstico: son necesarios para cajas sin
  Recientes.
- Las ventanas flotantes se habilitan con `enable_freeform_support` y
  `force_resizable_activities`; requieren reinicio.
- El lanzamiento freeform usa la clave serializada interna
  `android.activity.windowingMode=5` de `ActivityOptions` para forzar la tarea
  freeform. No usa reflexión ni permisos, pero no es API pública: cualquier
  cambio debe documentarse en `docs/LIMITATIONS.md` y probarse en API 29/35.
- No lanzar la segunda app con retraso: las dos órdenes deben salir mientras
  SplitEasy sigue en primer plano.
