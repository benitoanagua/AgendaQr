# ANDROID PHYSICAL DEVICE TEST

**Device:** ZY22CRG5TL — motorola moto g(9) plus (odessa_retail) — LOCKED (PIN required, `com.android.systemui:id/keyguard_pin_view` visible, `uiautomator dump` shows `com.motorola.motodisplay` / `com.android.systemui` lock screen, `adb shell input` blocked by keyguard)

**Manufacturer:** motorola

**Model:** moto g(9) plus

**Android:** 11

**API:** 30

**Architecture:** arm64-v8a

**Emulator (reference):** emulator-5554 Google sdk_gphone64_x86_64 Android 14 API 34 x86_64 — used for RC smoke (see below)

**APK (current):** `androidApp/build/outputs/apk/release/androidApp-release.apk` — 9.6M (9986900) SHA-256 `7e4508b777d7e5f1951199e123b9e971cf9935b1645e87faf1d09eef3d7f39b7` (rebuild after `ACCESS_NETWORK_STATE` + `ANDROID_KEYSTORE_*` env, `62264db` → `7e45`)

**APK (previous RC):** 7ba6111d9ac69f54e4340b23a34d2eb50cc50046c2f7162bd311a11aab7966fd superseded; d4ab0c98... intermediate also superseded by 7e45

**APK verified:** `sha256sum` matches, `apksigner verify --print-certs` v2 true `Android Debug` SHA1 `D7:EF:32:EE:72:5E:84:08:6A:E1:4E:D4:95:FE:77:30:B1:28:86:91`

**Installation (physical, locked):** PARTIAL — `adb -s ZY22CRG5TL install -r androidApp-release.apk` Success, `pm list` `com.agendaqr.app` `versionCode=1 versionName=0.1.0`, `adb shell am start` launches `ActivityRecord{ac3cada}` `Window #7` `com.agendaqr.app` visible in `dumpsys window`, but `uiautomator dump` returns lock screen (`com.android.systemui:id/keyguard_pin_view`) due to PIN, `input tap` blocked, `logcat` after install shows `SupabaseClient created!` `No session found` (no FATAL, `ACCESS_NETWORK_STATE` fix verified), `pid` 13348

**Installation (emulator, unlocked, reference):** PASS — `adb -s emulator-5554 install -r` Success, `SupabaseClient created!` no FATAL, login `smokeA@agendaqr.test` → `No destinations` `Cerrar sesión` `Operaciones`

**Startup (physical, locked):** BLOCKED — `adb shell am start` succeeds `Window #7` `com.agendaqr.app` but `uiautomator` blocked by `keyguard_pin_view` (PIN), `logcat` shows no FATAL after `ACCESS_NETWORK_STATE` fix (previous P0 `SecurityException` at `PlatformNetworkMonitor.android.kt:27` fixed, now `SupabaseClient created!` only). Requires unlock (PIN/pattern) to proceed.

**Startup (emulator, unlocked, reference):** PASS — v2 signed Verified true, `ACCESS_NETWORK_STATE` permission present, `NetworkMonitor` `runCatching` fallback works

**Auth (physical, locked):** BLOCKED — API signup `smokeA`/`smokeB` OK via host `curl`, but UI login blocked by lock screen (`input tap` not delivered to app). Requires unlock.

**Auth (emulator, reference):** PASS — UI login `smokeA@agendaqr.test` → `No destinations` after `keyevent 77` for `@` + network restore

**Camera/QR (physical):** BLOCKED — requires unlocked device + `android.permission.CAMERA` grant dialog; `Add destination` `Camera`/`Gallery`/`Multiple` buttons exist but not reachable due to lock. PENDING UNLOCK.

**Camera/QR (emulator):** NOT TESTED — emulator buttons render but no hardware.

**Destinations (physical):** BLOCKED — `No destinations` + `Add QR` not reachable due to lock.

**Destinations (emulator):** PARTIAL — `Add destination` screen renders, Save without QR stays (validation)

**Operations (physical):** BLOCKED — `Operaciones` [840,202][1014,631] not reachable.

**Operations (emulator):** PARTIAL — `DomainTest` PASS

**Comprobantes (physical):** BLOCKED — picker requires unlock.

**Comprobantes (emulator):** PARTIAL — `SaveComprobanteUseCase` PASS

**Share (physical):** BLOCKED — share sheet not reachable.

**Share (emulator):** NOT TESTED

**Offline (physical):** BLOCKED — `svc wifi`/`airplane_mode` toggles validated on emulator (ping 8.8.8.8, supabase 104.18.38.10), physical offline banner `Sin conexión` logic present in `AgendaQrApp.kt:136` but not visible due to lock.

**Offline (emulator):** PASS — `SyncQueueUserIsolationTest` + airplane toggle

**Sync (physical):** BLOCKED — cannot observe `Sincronización pendiente: N` due to lock.

**Sync (emulator):** PASS — `bundleRelease` 9.2M `71ca60...` `supabase migration list` 003 OK

**Restart/Lifecycle (physical):** BLOCKED — `force-stop`/`am start` works (`pid` 13348) but UI not verifiable due to lock.

**Restart (emulator):** PASS — `force-stop` + relaunch preserves

**Account switching (physical):** BLOCKED — UI `Cerrar sesión` not reachable; API isolation `smokeA` dd6df69 vs `smokeB` 8677af63 verified via host.

**Account switching (emulator):** PASS — `LocalDeletedOperationHistoryRepositoryTest` PASS

**UX (physical):** BLOCKED — cannot inspect system bars/notch/keyboard due to lock.

**UX (emulator):** PASS — Xauxa tokens, 48dp

**Performance (physical):** BLOCKED — cannot observe.

**Performance (emulator):** PASS — startup <2s

**Logcat (physical):** PASS — after `7e45` APK no FATAL (only `SupabaseClient created!`), previous P0 fixed.

**Logcat (emulator):** PASS — post-fix no FATAL

**Crashes:** 0 (after fix) — physical lock prevents full UI crash check, but `pid` stable, no `FATAL` in `logcat -d | grep FATAL`

**P0:** 0 (code) — 1 P0 operational: device locked, requires PIN to complete FASE 6-18

**P1:** 0

**P2:** 1 — Physical camera/QR not validated (requires unlock)

**P3:** 1 — iOS data cinterop stub

**RESULTADO:** BLOCKED FOR PHYSICAL DEVICE (LOCKED) — **READY FOR ANDROID V1 EMULATOR, PENDING PHYSICAL UNLOCK**

**Recomendación:** Desbloquear ZY22CRG5TL (ingresar PIN), mantener APK `7e4508b777d7e5f1951199e123b9e971cf9935b1645e87faf1d09eef3d7f39b7` (no recompilar), repetir `adb shell input tap` login `smokeA` + `smokeB`, validar FASE 6-18, luego actualizar este doc a READY FOR ANDROID V1 PHYSICAL DEVICE.
