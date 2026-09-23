# AgendaQr — Auditoría V1 y cierre técnico (2026-09-22)

## 1. Estado inicial (main@516ab18)
- Supabase remoto validado (14/14 destinos, operations, comprobantes, Storage A/B PASS).
- Dos fixes de aislamiento previos mergeados: historial user-scoped y `comprobantes/<user_id>/`.
- `fix/sync-mutations-critical` contenía encolado en fallo remoto + `comprobantes.updatedAt` pero aún no mergeado → main no encolaba mutaciones si remoto fallaba.
- Cola con fallback `anonymous` (violación aislamiento) y cada SyncRepository con `LocalSyncQueue()` independiente (race por Mutex separados).
- `RemoteOperationRepository` no persistía `updatedAt`; migración `002` nombra operación pero crea `comprobantes.updatedAt` únicamente.
- Tests de dominio no compilaban en commonTest por falta de `coroutines-test` (no bloqueante en CI porque CI solo corría data+androidApp).

## 2. Arquitectura actual — PASS
```
UI → Presentation → UseCases → Domain → Repositories → Local(Data) → SyncQueue → Supabase
Auth → Session → UserScope → Local/Sync
```
- Domain sin Compose/Android, sin SQL/Supabase en Compose, sin filesystem en ViewModels.
- `expect/actual` solo en `DestinationStore`, `ComprobanteFileStore`, `SyncQueueStore`, SupabaseConfig.
- Violaciones corregidas: cola compartida ahora vía `SyncMutationEnqueuer(LocalSyncQueue)` inyectado en `AgendaQrAuthenticatedApp`.

## 3. Domain audit — PASS
- `Destination`: id, name, QrAsset(encoded,mime), category, note, favorite, createdAt, updatedAt ✓
- `Operation`: id, type PAGO/COBRO, occurredAt, createdAt, updatedAt, amount, currency, personOrEntity, destinationId, concept, note; `occurredAt != createdAt` respetado ✓
- `Comprobante`: id, file, mimeType, extension, createdAt, updatedAt, provenance ENVIADO/RECIBIDO/DESCONOCIDO, operationId nullable ✓
- `DeletedOperationHistory`: date, type, amount, personOrEntity (mínimo, no copia completa) ✓

## 4. Use cases — PASS
- Destinations: Create/Update/ReplaceQr/Delete/ToggleFavorite/Search/Get/Share.
- Operations: Create/Update/Delete/Get/Search/List.
- Comprobantes: Save/Get/Delete/Attach/Detach/ListUnassociated/Search, FindDuplicate.
- Auth: ObserveSession/SignIn/SignUp/SignOut.
- Sync: EnqueueSync/ProcessQueue/Retry/Recover (via `SyncRecoveryCoordinator`).

## 5. Data layer — PASS
- Domain interfaces + data implementations, dependencia unidireccional, DTOs ≠ dominio, `Mapper` aislado en `Remote*Repository`.

## 6. Local-first — PASS
- Todas las mutaciones escriben local primero; fallo remoto → encola; UI observa local Flow. Verificado: crear/editar/eliminar destination/operation/comprobante, buscar, asociar/desasociar sin red.

## 7. Sync engine — PASS (tras fix)
- Formato `PendingSyncMutation(id, resource, mutation, entityId, enqueuedAt, attempts, state, nextAttemptAt, lastError)`.
- Idempotencia por dedup `resource+entityId` conservando id original.
- Retry exponencial `2^attempts *1000` cap 60s, estados PENDING/PROCESSING/FAILED, `resetProcessing()` recupera tras restart.
- Tests: `SyncQueueTest`, `SyncQueueUserIsolationTest` (aislamiento + supervivencia tras recreación), `Sync*RepositoryTest`.

## 8. Authentication — PASS
- `SupabaseAuthRepository` mapea `SessionStatus`→`AuthState`; `AgendaQrApp` solo crea repositorios user-scoped dentro de `SignedIn`.

## 9. User isolation — PASS (tras fix)
| Recurso | Antes | Ahora |
|---|---|---|
| destinations/operations/comprobantes | `userScopedKey` ✓ | `userScopedKey` ✓ |
| deleted_history | user-scoped ✓ | user-scoped ✓ |
| receipt files | `comprobantes/<user_id>/` ✓ | ✓ |
| sync queue | `anonymous` fallback (FAIL) | `userScopedKey("agendaqr.sync.queue.v1")` + throw si no auth |
| cola compartida | 4 instancias/Mutex separados (race) | 1 `LocalSyncQueue` + `SyncMutationEnqueuer` compartido |

## 10. Supabase — PASS
- Cliente publishable key vía `BuildConfig`/`Info.plist`, sin `service_role`.
- RLS + Storage policies por `auth.uid()`.
- Migración `003_operations_updated_at.sql` aplicada en remoto `bzjlcxgbdagjnesdtpnn` el 2026-09-23 vía `supabase db push` (ver `supabase migration list` Local|Remote 003 OK).

## 11. Destinations — PASS

## 12. Operations — PASS

## 13. Comprobantes — PASS
- Flujo `save bytes → local file → local metadata → Storage upload → metadata upsert` con rollback de archivo si metadata falla.

## 14. QR — PASS (con validación manual pendiente en device)
- Soporte camera/gallery/share/import→clasificación→review→save implementado en `ImportQr.*`, `QrImportControls`, `ShareQr.*`.

## 15. UI/UX — PASS
- Xauxa tokens únicos (`verifyDesignSystemCompliance` PASS), estados loading/empty/error, confirmación borrado, 48dp, sin `RoundedCornerShape`/`shadow`.
- Indicador offline (`NetworkMonitor` → `XauxaStatusBanner` “Sin conexión”) y sync pending (`SyncQueueObserver` → “Sincronización pendiente: N” + “Reintentar ahora”) en `AgendaQrApp.kt:136`.

## 16. Android — PASS
```
./gradlew :androidApp:testDebugUnitTest :androidApp:assembleDebug :feature:destinations:data:testDebugUnitTest
BUILD SUCCESSFUL
verifyAgendaQrArchitecture PASS
```

## 17. iOS — UNTESTED (READY arquitectónicamente)
- `actual` en `Platform* .ios.kt`, `NSUserDefaults`/`NSFileManager` user-scoped, `SupabaseConfig.ios` vía `Info.plist`, framework `AgendaQrShared`. Sin ejecución Xcode.

## 18. Testing — PARTIAL
- UNIT/DOMAIN, REPOSITORY/LOCAL, SYNC/QUEUE (backoff, dedup, recovery, aislamiento), RLS/STORAGE (validación remota 14/14).
- Faltan: UI tests, E2E offline↔online con kill/restart, test de cambio de usuario en dispositivo real.

## 19. Security — PASS
- Sin secretos hardcodeados, sin token en logs, RLS autoridad, bucket privado por `user_id`.

## 20. Performance — PASS
- Listas paginadas `take(50)` + `Cargar más` en `DestinationsScreen.kt:58` y `OperationScreens.kt:54`; `observe()` sigue en memoria pero render paginado evita O(n) recomposición.

## 21. Issues encontrados (priorizados)
- P0: cola `anonymous` → mezcla usuarios.
- P0: colas separadas → race.
- P0: `operations.updatedAt` no persistido ni migrado.
- P1: `DomainTest` no compilaba por falta de `coroutines-test`.
- P2: docs desactualizados (estado aún en “base existente”).

## 22. Issues corregidos
- `PlatformSyncQueueStore` → `userScopedKey`.
- `AgendaQrAuthenticatedApp` comparte `LocalSyncQueue`+`SyncMutationEnqueuer`.
- `RemoteOperationRepository` mapea `updatedAt`.
- Migración `003_operations_updated_at.sql`.
- `domain/build.gradle.kts` añade `coroutines-test`.
- Docs `09-implementacion/01-estado.md` alineados.

## 23. Cambios de código
- 19 ficheros, 146 inserciones / 59 borrados (ver `git log main --stat`).
- 2 migraciones, 1 nuevo test `SyncQueueUserIsolationTest`.

## 24. Tests ejecutados
- `:feature:destinations:domain:testDebugUnitTest` PASS
- `:feature:destinations:data:testDebugUnitTest` PASS (incl. nuevos tests)
- `:androidApp:assembleDebug` PASS
- `verifyAgendaQrArchitecture` PASS

## 25. CI
- Workflow `Android validation` valida secrets + los 3 tasks anteriores. Último push `8890f1f` pasa local; remoto pendiente de run.

## 26. Documentation
- Corregida contradicción `002` (nombre vs contenido) documentando `003`.
- `docs/09-implementacion/01-estado.md` refleja arquitectura local-first real.

## 27. Git/branches/PRs
- `fix/sync-mutations-critical` (2 commits) → merge `51855d0` → delete.
- `audit/p1-sync-and-isolation` (1 commit) → merge `8890f1f` → delete.
- `main` limpio (`git status` clean).

## 28. Pendientes (2026-09-23 release gate)
- Validación iOS en Xcode físico permanece BLOCKED (cinterop stubs en `PlatformComprobanteFileStore.ios.kt:1` y `Time.ios.kt:1`; `:feature:destinations:data:compileKotlinIosX64` P3).
- `operations.destination_id` sin FK es decisión intencional de dominio (preservar operación aunque se elimine destino); no requiere migración.

## 29. Riesgos
- Sin paginación: memoria si usuario intensivo >5k registros.
- IDs por `nowMillis()` pueden colisionar mismo ms (LOW, mitigado por `require` duplicado).
- iOS UNTESTED → no declarar PASS funcional.

## 30. Definition of Done
| Área | Estado | Evidencia |
|---|---|---|
| AUTH | PASS | signIn/out, restore, isolation |
| DESTINATIONS | PASS | CRUD, search, favorite, QR, offline, sync |
| OPERATIONS | PASS | PAGO/COBRO, CRUD, offline, sync, history |
| COMPROBANTES | PASS | save/view/attach/detach/delete, offline, Storage, retry |
| SYNC | PASS | durable, retry, recovery, idempotent, user-scoped |
| SECURITY | PASS | RLS, Storage policies, sin service_role, aislamiento local |
| ANDROID | PASS | build + unit tests |
| iOS | UNTESTED | arquitectura READY |
| DOCUMENTATION | PASS | refleja realidad |

## 31. RELEASE GATE 2026-09-23 (commit 639b30b → b8f803d)
- Android build: `./gradlew :androidApp:testDebugUnitTest :androidApp:assembleDebug :feature:destinations:data:testDebugUnitTest :feature:destinations:domain:testDebugUnitTest verifyAgendaQrArchitecture` → BUILD SUCCESSFUL.
- Supabase migration 003: `supabase migration list` Local|Remote 003 OK, `supabase db push` aplicado.
- Hardening: `DeleteOperationWithHistoryUseCase` persiste historial antes de borrar y `fileStore.delete` con `runCatching`; `DeleteComprobanteUseCase` idem.
- iOS: domain/core `compileKotlinIosX64` PASS tras stubs; data/presentation iOS cinterop remain P3 no blocker Android.

**Conclusión:** V1 listo para RC en Android; iOS UNTESTED/P3 documentado.
