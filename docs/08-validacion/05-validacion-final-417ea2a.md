# AgendaQr — Validación final post-merge 75cc338 (PRs #68 + #69)

Fecha: 2026-10-02. Rama de trabajo: `fix/ios-data-platform-interop`.

## Commit final

```text
main = 417ea2a8a90c5a99d4135bba6c48723a2b52f561
origin/main = 417ea2a8a90c5a99d4135bba6c48723a2b52f561
```

Working tree limpio al inicio. `HEAD` coincide con el esperado.

## PRs #68 y #69

```text
PR #68 = MERGED (ae92613 Merge pull request #68)
PR #69 = MERGED (417ea2a Merge pull request #69)
```

Cambios confirmados en `main`:

- `core/ui/.../motion/ReducedMotion.ios.kt:4`: `import androidx.compose.runtime.getValue`
- `androidApp/src/main/AndroidManifest.xml:4-5`: `CAMERA` + `<uses-feature android:name="android.hardware.camera" android:required="false" />`

## Corrección iOS (#68)

```text
IOS_REDUCED_MOTION = PASS
```

- `:core:ui:compileKotlinIosX64 --rerun-tasks` → BUILD SUCCESSFUL, sin `Property delegate must have a getValue/setValue`.
- `:core:ui:compileKotlinIosSimulatorArm64` → BUILD SUCCESSFUL.

## Corrección Android Lint (#69)

```text
ANDROID_LINT = PASS
```

- `:androidApp:lint` → BUILD SUCCESSFUL.
- Reporte `lint-results-debug.xml`: 17 issues, todas `Warning`, 0 `Error`.
- `PermissionImpliesUnsupportedChromeOsHardware` ausente.

## Tests unitarios (sobre main 417ea2a)

```text
core:ui = PASS (82 tests, 0 failures, 0 errors)
domain = PASS (86 tests, 0 failures, 0 errors)
data = PASS (76 tests, 0 failures, 0 errors, revision race / corruption / defer / ordering / isolation / idempotency OK)
presentation = PASS (6 tests, 0 failures, 0 errors)
global test = PASS (250 tests, 0 failures, 0 errors, 0 skipped)
```

## Android Debug / Release RC

```text
ANDROID_DEBUG = PASS (:androidApp:assembleDebug BUILD SUCCESSFUL)
ANDROID_UNIT_TEST = PASS — NO-SOURCE (:androidApp:testDebugUnitTest NO-SOURCE)
ANDROID_RELEASE_RC = PASS (:androidApp:assembleRelease -PallowDebugSigningForRc=true BUILD SUCCESSFUL)
PRODUCTION_RELEASE = BLOCKED — signing guard exige keystore productivo (sin flag falla como corresponde)
```

## Component Lab

```text
COMPONENT_LAB_BUILD = PASS (componentLabWeb Wasm BUILD SUCCESSFUL)
COMPONENT_LAB_RUNTIME = BLOCKED — sin chromium/chrome en el entorno
```

## Global check — HALLAZGO CRÍTICO

```text
GLOBAL_CHECK_ON_MAIN = FAIL
```

Sobre `main` (417ea2a), `./gradlew check` falla en un **tercer error de código preexistente**,
distinto de los dos ya corregidos:

1. `:feature:destinations:data:compileKotlinIosX64/SimulatorArm64`:
   `PlatformComprobanteFileStore.ios.kt` (constantes NS anidadas inexistentes,
   `NSData.create(bytes:)` con tipos incompatibles, `writeToFile`/`toByteArray` no expuestos)
   y `PlatformNetworkMonitor.ios.kt` (clase ObjC `NWPathMonitor` no expuesta por Kotlin/Native).
2. `:feature:destinations:presentation:compileKotlinIosSimulatorArm64`:
   `ShareQr.ios.kt` (`NSData.create(base64EncodedString:)` inexistente,
   `writeToURL` no expuesto, `popoverPresentationController` no expuesto).
3. `:feature:destinations:data:lintDebug`: `MissingPermission` (`ACCESS_NETWORK_STATE`
   no declarado en el módulo library).

Verificado contra los bindings reales del toolchain (`kotlin-native 2.2.20`)
con compilaciones mínimas `konanc -target ios_x64`: cada símbolo fallido se confirmó
ausente y cada sustituto se confirmó presente a nivel de frontend.

## Rama de corrección (nueva)

```text
branch = fix/ios-data-platform-interop (pushed a origin, commit 458a737)
PR = PENDIENTE (sin gh CLI ni token en el entorno; abrir desde la web contra main)
```

Cambios mínimos, sin arquitectura ni funcionalidades nuevas:

- `PlatformComprobanteFileStore.ios.kt`: constantes NS top-level, `NSData.create` con
  `ByteArray` pineado, `NSFileManager.createFileAtPath`, lectura con `memcpy`,
  `@OptIn(ExperimentalForeignApi)`.
- `PlatformNetworkMonitor.ios.kt`: reescrito sobre la C API `nw_path_monitor_*`
  (misma semántica: callbackFlow + distinctUntilChanged + queue dedicada + cancel).
- `ShareQr.ios.kt`: `Base64.decode` + `NSData` pineado, `createFileAtPath`,
  en iPad `modalPresentationStyle = formSheet` (no existe anchor de popover configurable).
- `feature/destinations/data/src/androidMain/AndroidManifest.xml` (nuevo):
  declara `ACCESS_NETWORK_STATE` (el merger lo unifica con el del app).

Validación sobre la rama:

```text
check = PASS (BUILD SUCCESSFUL)
test = PASS (250 tests, 0 failures, 0 errors)
:shared:compileKotlinIosX64 = PASS (antes bloqueado por el módulo data)
verifyAgendaQrArchitecture / lint / assembleDebug / assembleRelease RC / componentLabWeb = PASS
```

## Cierre — estado real (revisado al archivar este documento)

La contradicción original ("EJECUTADO" vs "Pendiente para cierre real") queda
resuelta así:

- La rama `fix/ios-data-platform-interop` **sí está integrada a `main`** vía
  merge local `2ef3a86` (verificable con
  `git log --oneline --all | grep 2ef3a86`), antecesor del `main` actual.
- **No se abrió PR**: en aquel entorno no había `gh`/token. Queda registrado.
- La rama **ya no existe** (ni local ni remota): `git branch -a` solo muestra
  `main`. Los puntos 1–3 del antiguo "Pendiente para cierre real" quedaron
  obsoletos por la integración ya efectuada.
- El `check`/`lint`/`compileKotlinIosX64` sobre el nuevo `main` se repitieron en
  pasadas posteriores (ver `docs/09-implementacion/01-estado.md`).

```text
main contiene a 2ef3a86 = SÍ (verificado)
ramas fix eliminadas (local + remoto) = SÍ (verificado con git branch -a)
working tree = limpio
```

## Bloqueados externos (no convertir en PASS)

```text
PRODUCTION_RELEASE = BLOCKED (sin keystore)
COMPONENT_LAB_RUNTIME = BLOCKED (sin navegador)
Supabase runtime = BLOCKED (sin daemon Docker; no verificado en esta pasada)
iOS full link/runtime = BLOCKED (Linux sin Xcode; solo compilación kotlin)
Instrumented Android tests = NOT RUN (fuera del entorno)
```
