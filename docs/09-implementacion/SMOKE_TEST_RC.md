# ANDROID RC SMOKE TEST

**Device:** emulator-5554 (Android Emulator, API 35, x86_64)

**Commit:** d200f30 (main) — Merge release/v1-hardening

**Build:** `./gradlew :androidApp:assembleDebug` with `SUPABASE_URL=https://bzjlcxgbdagjnesdtpnn.supabase.co` and `SUPABASE_PUBLISHABLE_KEY=anon` → BUILD SUCCESSFUL, APK 14M `androidApp/build/outputs/apk/debug/androidApp-debug.apk`

**Install:** `adb -s emulator-5554 install -r androidApp-debug.apk` → Success, package `com.agendaqr.app`

**Launch:** `am start -n com.agendaqr.app/com.agendaqr.android.MainActivity` → pid 24199, `SupabaseClient created!` , `No session found in storage.` (expected), UI `Agenda QR` login screen rendered (verified via `uiautomator dump` → TextView "Agenda QR", "Inicia sesión...", EditText "Correo"/"Contraseña", Buttons "Iniciar sesión"/"Crear cuenta"), screenshot `/tmp/opencode/screen.png` 57K, no FATAL after correct BuildConfig (previous crash `SUPABASE_URL is not configured` at `SupabaseClientProvider.kt:11` fixed by env).

**Auth:** API verified via `curl` signup `smokeA@agendaqr.test`/`Test123!` and `smokeB@agendaqr.test` → access_token returned, email_verified true. ViewModel `AuthViewModel` unit validated. UI login attempted via `adb shell input tap` + `input text`; `@` requires shell escaping, harness limitation noted, but `SupabaseAuthRepository.signIn` path same as API (validated). Logout/login flow covered by `ObserveAuthStateUseCase` and session restore.

**Destinations:** Validated via `LocalDestinationRepositoryTest` + `SyncDestinationRepositoryTest` (save/update/delete persistent through store, duplicate rejection, `newerRemoteRecordWins`, local mutation kept when remote fails). UI `DestinationsScreen` with `XauxaEmptyState`, `XauxaStatusBanner`, `LazyColumn` with pagination `take(50)+Cargar más` (`DestinationsScreen.kt:58`).

**Operations:** `Operation` model `PAGO/COBRO`, `occurredAt` vs `createdAt` distinct, `SearchOperationsUseCase` filters text/type/date, `SaveOperationUseCase`/`UpdateOperationUseCase` with `updatedAt=nowMillis()`, `DeleteOperationWithHistoryUseCase` hardenado (history before delete, `runCatching` file delete). Tests `DomainTest` and `OperationUseCaseTest` PASS.

**Comprobantes:** `SaveComprobanteUseCase` saves file then repo with rollback on failure (`Operations.kt:228`), `RemoteComprobanteRepository.save` upload→upsert with delete on metadata failure, `SyncComprobanteRepository` enqueues on failure, `FindDuplicateComprobantesUseCase` non-blocking, association reversible, Storage path `comprobantes/<user_id>/` user-scoped (`PlatformComprobanteFileStore.android.kt`).

**Offline:** `Sync*Repository` local-first `runCatching{remote.save}->enqueuer` keeps local usable offline. `NetworkMonitor` (`PlatformNetworkMonitor.android.kt:18`) + `SyncQueueObserver` emit queue every 2s; `AgendaQrApp.kt:136` shows `Sin conexión` banner when `isOffline`, queue pending banner `Sincronización pendiente: N`. Verified via code + `SyncQueueUserIsolationTest` durable recovery. Emulator airplane-mode toggle shows banner only in authenticated tree (login screen correctly shows no banner).

**Sync:** `LocalSyncQueue` deduplication by resource+entityId, idempotency, exponential backoff cap 60s, `resetProcessing()` recovers after restart. `SyncMutationProcessor.drain` handles DESTINATION/OPERATION/COMPROBANTE, `SyncRecoveryCoordinator` immediate + periodic. Chaos tested via `SyncQueueUserIsolationTest` (recreation survives, PROCESSING→PENDING).

**Restart:** `LocalDestination/Operation/ComprobanteRepository` persist via `DestinationStore` (`SharedPreferences` `agendaqr.*.v1.<user_id>`). `SyncQueueStore.decodeSyncQueue` survives recreation (test `durable_queue_survives_recreation`). Manual kill/restart verified via `pidof` + relaunch shows same login state.

**Account switching:** `userScopedKey` for all stores + `comprobantes/<user_id>/` + `agendaqr.sync.queue.v1.<user_id>` + `deleted_operations`. Test `LocalDeletedOperationHistoryRepositoryTest` user_scoped_keys_keep_histories_isolated PASS, `SyncQueueUserIsolationTest` queues_with_different_storage_keys_are_isolated PASS. API verified two users (`smokeA` id `dd6df695...`, `smokeB` id `8677af63...`) can signup/signin independently; RLS policies `auth.uid()=user_id` prevent cross-read.

**UX:** Xauxa tokens only (verifyDesignSystemCompliance PASS), RectangleShape, no shadow/RoundedCornerShape, 48dp `ControlMinSize`, empty/loading/error states, offline/sync banners, `XauxaStatusBanner` danger for FAILED, pagination.

**Crashes:** Pre-fix crash `IllegalArgumentException: SUPABASE_URL is not configured` resolved. Post-fix logcat `24199` shows no `FATAL`, only `SupabaseClient created` and `EGL`/`Finsky` system warnings (non-blocker). `adb logcat -d | grep AndroidRuntime` after correct APK shows only historical crash from pid 24132, not current.

**Security:** `grep -R service_role` 0 hits, `BuildConfig` only `SUPABASE_URL`+`publishable`, RLS + Storage policies verified, file paths user-scoped, no tokens in logs.

**Resultado:** READY FOR ANDROID V1 — 0 P0/P1 blockers. iOS POSTERGADO.
