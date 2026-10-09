# Resultados y evidencia — ronda 2

Última actualización: 2026-10-09 (pasada de pruebas instrumentadas, parcial).

## Estado de la pasada de pruebas

**Dispositivo**: moto g(9) plus (ZY22CRG5TL), Android 11 (API 30), 1080x2400, 400dpi.

**Cuenta de prueba**: ficticia (`qa-ronda2@test.local`), creada contra el
Supabase local (192.168.0.58:54321) mediante el flujo "Crear cuenta" de la app.

### Resultados por clase

| Clase | Tests | Estado | Nota |
|---|---|---|---|
| `ShareIntentsTest` (preexistente) | 3 | **PASS** | Entorno validado. |
| `AccessibilityTreeAuditTest` | 2 | **FALLA** (infraestructura) | Ver hallazgo I-01. |
| `CameraPermissionFlowTest` | 1 | **PENDIENTE** | Requiere sesión activa (hallazgo I-01). |
| `FontScaleScreensTest` | 1 | **PENDIENTE** | Requiere sesión activa. |
| `RotationRestorationTest` | 3 | **PENDIENTE** | Requiere sesión activa. |
| `AnnouncementsTest` | 3 | **PENDIENTE** | Requiere sesión activa. |
| `ReducedMotionTest` | 1 | **PENDIENTE** | Requiere sesión activa. |
| `HapticsEvidenceTest` | 1 | **PENDIENTE** | Requiere sesión activa. |

### Hallazgos

**I-01 (BLOQUEANTE, infraestructura de pruebas):**
`connectedDebugAndroidTest` DESINSTALA la app antes de instalar los APKs de
prueba, perdiendo la sesión de DataStore en cada corrida. Los tests que
asumen el usuario autenticado fallan con "sin ventana" porque la app
arranca en el login.

**Soluciones propuestas** (elegir una para la próxima sesión):
- (a) `@BeforeClass` con login vía UiAutomator (llenar campos + tap).
- (b) TestRule de autenticación: crear cuenta antes de cada clase.
- (c) Usar ActivityScenario + Compose test con semilla de DataStore.

**I-02 (nota):** `~/.gradle/gradle.properties` fija `SUPABASE_URL` al
proyecto REMOTO, que TOMA PRECEDENCIA sobre variables de entorno (la
propiedad Gradle gana sobre `providers.environmentVariable`). Toda corrida
que necesite el Supabase local debe pasar
`-PSUPABASE_URL=http://192.168.0.58:54321 -PSUPABASE_PUBLISHABLE_KEY=...`
explícitamente en la línea de Gradle.

### Cómo reanudar la ejecución

```bash
# 1) Desbloquear el dispositivo (o desbloquear con adb si no tiene PIN).
# 2) Construir con el Supabase local:
./gradlew :androidApp:assembleDebug :androidApp:assembleDebugAndroidTest \
  -PallowDebugSigningForRc=true \
  -PSUPABASE_URL=http://192.168.0.58:54321 \
  -PSUPABASE_PUBLISHABLE_KEY=<anon-key-del-stack-local>

# 3) Instalar y login manual:
adb install -r androidApp/build/outputs/apk/debug/androidApp-debug.apk
adb shell am start -n com.agendaqr.app/com.agendaqr.android.MainActivity
# ... hacer login con qa-ronda2@test.local / test123456 (o crear cuenta) ...

# 4) Ejecutar UNA clase (la reinstalación PIERDE la sesión):
ANDROID_SERIAL=ZY22CRG5TL ./gradlew :androidApp:connectedDebugAndroidTest \
  -PallowDebugSigningForRc=true \
  -PSUPABASE_URL=http://192.168.0.58:54321 \
  -PSUPABASE_PUBLISHABLE_KEY=<anon-key> \
  -Pandroid.testInstrumentationRunnerArguments.class=com.agendaqr.android.<Clase>

# 5) Recuperar capturas:
adb pull /sdcard/agendaqr-ronda2 /tmp/agendaqr-ronda2/
```

### Scripts existentes en el repo (informativo, no tocados)

- `docs/05-design-system/verify-xauxa.sh` — gate de design system.
- `docs/04-ux/visual-hashes/verify.sh` — comparación de hashes de capturas.
- `supabase/tests/run-acceptance.sh` — harness de aceptación SQL.

### Cero archivos de script nuevos

Toda la nueva infraestructura de pruebas es Kotlin bajo
`androidApp/src/androidTest/`.
