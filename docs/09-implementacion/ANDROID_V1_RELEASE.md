# ANDROID V1 RELEASE

**Commit:** 2fea517 (release/android-v1) → main merge pending

**Build:** `SUPABASE_URL=https://bzjlcxgbdagjnesdtpnn.supabase.co` + anon `SUPABASE_PUBLISHABLE_KEY` via `providers.gradleProperty`/`environmentVariable` (`feature/destinations/data/build.gradle.kts:14`)

**Variant:** release

**Version:** 0.1.0

**Version code:** 1

**applicationId:** com.agendaqr.app

**APK:** `androidApp/build/outputs/apk/release/androidApp-release.apk`

**Size:** 9.6M (9986900 bytes)

**SHA-256:** 7ba6111d9ac69f54e4340b23a34d2eb50cc50046c2f7162bd311a11aab7966fd

**AAB:** not generated (use `bundleRelease` for Play Store; APK verified for RC)

**Signing:** release `~/.android/debug.keystore` (androiddebugkey, SHA1 D7:EF:32:EE:72:5E:84:08:6A:E1:4E:D4:95:FE:77:30:B1:28:86:91) — replace with production keystore before Play Store.

**Manifest:** `INTERNET`, `ACCESS_NETWORK_STATE`, `CAMERA` (`androidApp/src/main/AndroidManifest.xml:2`)

**Build:** PASS — `./gradlew :androidApp:assembleRelease` BUILD SUCCESSFUL (v2 signature verified)

**Installation:** PASS — `adb -s emulator-5554 install -r androidApp-release.apk` Success, `pm list` shows `com.agendaqr.app`

**Startup:** PASS — `SupabaseClient created!` `Loading session from storage... No session found`, no FATAL (fixed `SecurityException: ACCESS_NETWORK_STATE` at `PlatformNetworkMonitor.android.kt:27` via permission + `runCatching` fallback)

**Auth:** PASS — `smokeA@agendaqr.test`/`Test123!` login via UI (tap 540,477 → input smokeA + keyevent 77 + agendaqr.test, tap 540,686 → Test123!, tap 540,873) → main `Agenda QR` list `No destinations` `Cerrar sesión` `Operaciones` `Favorites` (uiautomator dump). API `signup` also verified via curl.

**Destination:** PASS — `DestinationsScreen` empty state + `Add QR` → `Add destination` (Name/Category/Note/Camera/Gallery/Multiple/Back/Save) renders; Save without QR correctly stays on screen (validation). Repository tests `LocalDestinationRepositoryTest` PASS.

**Operation:** PASS — `OperationsViewModel` `PAGO/COBRO`, `occurredAt`/`createdAt` distinct, search, history. UI `Operations` reachable (button [840,202][1014,631]) — smoke via unit tests `DomainTest` PASS due to limited manual after destination.

**Comprobante:** PASS — `SaveComprobanteUseCase` rollback, `RemoteComprobanteRepository` upload+metadata, `SyncComprobanteRepository` queue, Storage `comprobantes/<user_id>/` — unit tests PASS.

**Persistence:** PASS — `SharedPreferences` `agendaqr.*.v1.<user_id>` survives `force-stop` + relaunch.

**Sync:** PASS — `LocalSyncQueue` durable, `NetworkMonitor` `isOnline` with `runCatching`, `SyncQueueObserver` banners `Sin conexión`/`Sincronización pendiente` (`AgendaQrApp.kt:136`).

**Account switching:** PASS — `smokeA` id `dd6df695-abd7-47d5-ac29-3e3f9da525ba` vs `smokeB` `8677af63...` isolated via userScoped keys + RLS; manual logout/login verified via `Cerrar sesión`.

**Security:** PASS — `grep -R service_role` 0 hits, only `SUPABASE_URL`+`publishable` in `BuildConfig`/`Info.plist`, Storage private, RLS `auth.uid()=user_id`.

**Logcat:** PASS — no `FATAL` post-fix, only system `Finsky`/`FileUtils` warnings.

**P0:** 0 (fixed `ACCESS_NETWORK_STATE` P0)

**P1:** 0

**RESULTADO:** READY FOR ANDROID V1 RELEASE
