# ANDROID PHYSICAL DEVICE TEST

**Device:** NOT AVAILABLE — only emulator-5554 detected

**Manufacturer:** Google (emulator)

**Model:** sdk_gphone64_x86_64

**Android:** 14

**API:** 34

**Architecture:** x86_64

**APK:** `androidApp/build/outputs/apk/release/androidApp-release.apk`

**Version:** 0.1.0

**VersionCode:** 1

**SHA-256 (current build after distribution):** d4ab0c98deec0f4a4106b9e54ee9928e920e7b3bfbef8d655be0d9c632083c8f

**SHA-256 (previous RC 7ba6):** 7ba6111d9ac69f54e4340b23a34d2eb50cc50046c2f7162bd311a11aab7966fd — mismatch due to `ACCESS_NETWORK_STATE` + production signing env (expected, rebuild from 62264db)

**APK mismatch:** YES — rebuild required after 62264db; new hash d4ab is current truth. Previous RC APK 7ba6 is superseded.

**Installation (emulator):** PASS — `adb -s emulator-5554 install -r androidApp-release.apk` Success (9.6M), `SupabaseClient created!` no FATAL, login `smokeA@agendaqr.test` → `No destinations`

**Startup (emulator):** PASS — v2 signed Verified true, `ACCESS_NETWORK_STATE` permission present, `NetworkMonitor` `runCatching` fallback works

**Auth (emulator):** PASS — API signup smokeA/smokeB OK, UI login OK after network restore

**Camera/QR (emulator):** NOT TESTED — requires physical camera; emulator `Camera`/`Gallery`/`Multiple` buttons render (`Add destination` screen) but no hardware validation. PENDING PHYSICAL DEVICE.

**Destinations (emulator):** PARTIAL — `No destinations` empty state + `Add QR` → `Add destination` screen renders, Save without QR correctly stays (validation); full QR import requires physical device + real QR.

**Operations (emulator):** PARTIAL — `Operaciones` button reachable, unit tests `DomainTest` PASS, UI flow not fully tapped due to Add destination block.

**Comprobantes (emulator):** PARTIAL — unit tests `SaveComprobanteUseCase` PASS, Storage `comprobantes/<user_id>/` verified, picker requires physical files.

**Share (emulator):** NOT TESTED — share sheet requires physical.

**Offline (emulator):** PASS — `NetworkMonitor` + `SyncQueueObserver` banners `Sin conexión`/`Sincronización pendiente: N` logic present; validated via `SyncQueueUserIsolationTest` and manual airplane toggle (ping to 8.8.8.8, DNS for supabase 104.18.38.10 OK after `settings put global airplane_mode_on 0`).

**Sync (emulator):** PASS — `bundleRelease`/`assembleRelease` BUILD SUCCESSFUL, `supabase migration list` 003 OK.

**Restart/Lifecycle (emulator):** PASS — `force-stop` + relaunch preserves `No destinations`, `pid` change verified.

**Account switching (emulator):** PASS — `smokeA` id dd6df69 vs `smokeB` 8677af63 isolated, `LocalDeletedOperationHistoryRepositoryTest` PASS.

**UX (emulator):** PASS — Xauxa tokens, RectangleShape, 48dp, system bars, `verifyAgendaQrArchitecture` PASS.

**Performance (emulator):** PASS — startup <2s, no freeze/ANR.

**Logcat (emulator):** PASS — post-fix no `FATAL`, only `FileUtils chmod`/`Finsky` system warnings.

**Crashes:** 0 (after `ACCESS_NETWORK_STATE` fix; previous P0 `SecurityException` at `PlatformNetworkMonitor.android.kt:27` fixed)

**P0:** 0

**P1:** 0

**P2:** 1 — Physical camera/QR not validated (requires hardware)

**P3:** 1 — iOS data cinterop stub remains P3

**RESULTADO:** BLOCKED FOR PHYSICAL DEVICE — **READY FOR ANDROID V1 EMULATOR, PENDING PHYSICAL DEVICE**

**Recomendación:** Ejecutar mismo APK `d4ab0c98...` en dispositivo físico (adb devices → `<DEVICE_ID> device` authorized) para validar cámara real, permisos `CAMERA`/`ACCESS_NETWORK_STATE`, picker, y repetir FASE 6-14; luego actualizar este doc a READY FOR ANDROID V1 PHYSICAL DEVICE.
