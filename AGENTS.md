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
