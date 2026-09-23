# ANDROID DISTRIBUTION

**Current commit:** 2fea517 → c67dbaf → 651d8d2 (main) + distribution branch `release/android-v1-distribution` (pending merge)

**Version:** 0.1.0

**VersionCode:** 1

**ApplicationId:** com.agendaqr.app

**Signing:**

- **RC:** `~/.android/debug.keystore` (androiddebugkey, SHA1 D7:EF:32:EE:72:5E:84:08:6A:E1:4E:D4:95:FE:77:30:B1:28:86:91) via `build-logic/src/main/kotlin/agendaqr.android-application.gradle.kts:18` with `isMinifyEnabled=false`. Used for internal RC, emulator smoke tests.

- **Production:** Prepared via env vars `ANDROID_KEYSTORE_FILE`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD` in same file `build-logic/...:25` — if vars present and file exists, production keystore is used; otherwise fallback to debug with warning. Private material stays outside Git (tracked in `.gitignore:1` `*.jks`, `*.keystore`, `keystore.properties`). No keystore, passwords, or private keys committed.

**APK:** `androidApp/build/outputs/apk/release/androidApp-release.apk` — 9.6M (9986900) SHA-256 `7ba6111d9ac69f54e4340b23a34d2eb50cc50046c2f7162bd311a11aab7966fd` — v2 signed `Android Debug` Verified true

**AAB:** `androidApp/build/outputs/bundle/release/androidApp-release.aab` — 9.2M SHA-256 `2515c4288116b2b534f3aaea98287422270280f5e4f691f2c4e23bb7c6af17be` — `bundleRelease` BUILD SUCCESSFUL, `signReleaseBundle` with debug keystore (same fallback).

**Build:** PASS — `SUPABASE_URL`+`publishable` injected via `providers.gradleProperty`/`environmentVariable` for both debug/release (`data/build.gradle.kts:14`); `./gradlew :androidApp:bundleRelease` and `:assembleRelease` BUILD SUCCESSFUL.

**Install:** PASS — `adb -s emulator-5554 install -r androidApp-release.apk` Success, `pm list` shows `com.agendaqr.app`, `versionCode=1 versionName=0.1.0`.

**Smoke:** PASS — startup `SupabaseClient created!` no FATAL (fixed `ACCESS_NETWORK_STATE`), login `smokeA@agendaqr.test` → `No destinations` main list, `Add destination` screen, `Operaciones` reachable; offline banner and sync pending logic present.

**Security:** PASS — `grep -R service_role` 0 hits; only `SUPABASE_URL`+`publishable` in `BuildConfig`/`Info.plist`; Storage private `comprobantes/<user_id>/`, RLS `auth.uid()=user_id`; `.gitignore` blocks `*.jks`/`*.keystore`; GitHub Actions `env: SUPABASE_URL/KEY` from secrets, not hardcoded; no tokens in logs (verified `logcat`).

**Google Play readiness:** PENDING — **NOT READY FOR PLAY SUBMISSION** (READY FOR ANDROID V1 RC). Pending product input:

- App icon (adaptive icon) — check `mipmap`
- Screenshots / feature graphic
- Short & full description (ES)
- Privacy policy URL (required for Data Safety)
- Data Safety form: data collection (Auth email, Storage), encryption, deletion
- Content rating, target audience, content declarations
- Account deletion URL (if Play requires for Auth)
- Production keystore creation + `ANDROID_KEYSTORE_*` secrets in Play Console / CI
- `versionCode` increment strategy for next release
- `minify`/`R8` disabled (`isMinifyEnabled=false`) — enable for production if needed

**Pendientes:** Only product input above; no P0/P1 code blockers.
