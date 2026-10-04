# Estado de implementación — V1 / Release Gate

## Situación actual

La especificación funcional V1 y el contrato UX/UI V1 están congelados y continúan siendo la fuente de verdad para producto, dominio e interacción.

main contiene la fundación local-first, Supabase, Contextos, Operaciones, Comprobantes, búsqueda global, importación por lote y la integración de presentación correspondiente.

No se introducen cambios de UX fuera del contrato congelado.

## Implementado y validado

### Dominio y datos
- Context con persistencia local-first y Supabase.
- contextId? en QR/Destination, Operation y Comprobante.
- relaciones de contexto reversibles.
- búsqueda global sobre Context, QR, Activity y Comprobante; un QR también es encontrable a través del contexto asociado.
- operaciones PAGO/COBRO con histórico mínimo al eliminar (los comprobantes asociados y sus archivos se eliminan junto a la operación; el resto queda intacto).
- comprobantes asociados o independientes.
- asociación/desasociación reversible.
- detección de comprobantes duplicados.
- almacenamiento de bytes separado del estado de UI.
- aislamiento por usuario.
- RLS y Storage privado por usuario.

### Sincronización
- cola durable user-scoped.
- deduplicación por (resource, entityId).
- última mutación gana.
- DELETE sustituye UPSERT pendiente.
- estados PENDING / PROCESSING / FAILED / DEAD_LETTER (cuarentena de errores permanentes).
- backoff exponencial acotado con `nextAttemptAt` y reporte `nextRetryAt`.
- recuperación de PROCESSING tras reinicio.
- resolución de conflictos Last-Write-Wins con tolerancia de skew (`SyncConflictResolver`, servidor gana empates; DELETE viejo no resucita).
- `syncFromRemote` no pisa entidades con mutaciones pendientes; el drain decide.
- `SyncRecoveryCoordinator` con `NetworkMonitor` de plataforma y scope ligado al usuario (`remember(userId)`; `stop()` no cancela el scope).
- procesamiento de CONTEXT / DESTINATION / OPERATION / COMPROBANTE.
- SyncMutationEnqueuer y LocalSyncQueue compartidos por sesión autenticada; el procesador opera sobre los locales crudos compartidos.

### Importación
- flujo IMPORTANDO → ANALIZANDO → RESULTADO → REVISAR → GUARDAR → GUARDADO.
- QR / COMPROBANTE / DESCONOCIDO.
- elementos inválidos o desconocidos no bloquean los válidos.
- deduplicación por huella de contenido: la primera ocurrencia válida se conserva; solo las ocurrencias posteriores quedan como duplicado en revisión. Un elemento DESCONOCIDO nunca convierte a un elemento válido en duplicado.
- duplicados quedan para revisión con su payload disponible para reintento.
- QR y comprobantes se persisten de forma independiente.
- payload temporal eliminado después de persistencia exitosa.
- fallos aislados por candidato y recuperables.
- doble guardado protegido (idempotente a nivel de lote, repositorio y UI).
- la clasificación no-QR es deliberadamente conservadora (Android: QR demostrado por decodificación ZXing; el resto entra como DESCONOCIDO sin importar el MIME).

### Matching de comprobantes
- asociación existente se conserva.
- un único candidato fuerte puede proponerse.
- la señal de "mismo día" compara días de calendario en la zona horaria del dispositivo (inyectable para tests); una ventana rodante de 24 h no es "mismo día".
- candidatos débiles no se convierten en asociación automática.
- múltiples candidatos se presentan como ambigüedad conservando candidatos razonables.
- ausencia de evidencia suficiente deja el comprobante sin asociar.

### Presentation / Compose
- Context screen.
- Global Search.
- registro de Operation con contexto.
- revisión de QR.
- revisión de comprobante.
- advertencia de comprobante duplicado no bloqueante; permite Ver existente o Guardar de todos modos.
- sugerencia determinista de asociación.
- importación bulk con estados y acciones.
- guardas contra doble guardado.
- routing de resultados globales.
- Android QR classification mediante ZXing.

### Laboratorio de componentes (herramienta de desarrollo)
- Catálogo navegable (secciones Fundamentos/Componentes/Patrones) de los componentes y tokens Xauxa reales de `core:ui`: 6 fundamentos + 31 componentes + 8 patrones, con inspector derivado de un modelo puro (`core/ui/.../lab/model/`), previews interactivos con datos ficticios, matriz de interacción con veredictos honestos, guía de uso, mapeos nativos, gobierno por contrato y registro de eventos.
- Host WebAssembly: el laboratorio se ejecuta en el navegador mediante el target `wasmJs` de `core:ui` (entrypoint `AgendaQrComponentLabWebMain.kt` + recursos web en `wasmJsMain/resources`). Se construye con `./gradlew componentLabWeb` y se sirve en `build/web/component-lab/`.
- El host Android solo-debug fue retirado (`ComponentLabActivity`, su manifiesto y el `debugImplementation(project(":core:ui"))`); `androidApp` ya no tiene dependencia del lab en ningún build.
- Aislado de navegación, repositorios y servicios de producción; documentación en `docs/04-ux/03-laboratorio-componentes-compose.md` y estado de conformidad visual en `docs/05-design-system/04-component-audit-register.md`.
- Validación: 40 pruebas unitarias del modelo PASS (integridad, búsqueda, matriz, gobierno, patrones, esquemas); features domain/data/presentation PASS sin cambios; compilación Wasm del host PASS y revisión en navegador headless (Chromium) con evidencia registrada en el documento del laboratorio; `assembleDebug` y `assembleRelease` PASS. Host iOS: pendiente.

## CI

PR #42 y PR #44 dejaron el workflow dividido en tareas independientes: domain, data, presentation, androidApp unit tests y assembleDebug. Los `continue-on-error` temporales de diagnosis (PR #51/#52) se retiraron: el gate vuelve a fallar si domain o presentation fallan, y los report-artifacts se conservan para diagnóstico.

El último run de main antes de esta corrección (35941171336) falló en **Domain unit tests**; las tareas posteriores quedaron omitidas. Se invalidó la caché Gradle de CI en esta rama para descartar una caché inconsistente antes de volver a declarar PASS.

### Validación local de esta corrección (Linux x86_64, JDK 17)

- `./gradlew :feature:destinations:domain:testDebugUnitTest --stacktrace` → **PASS** (43 tests, 0 fallos).
- `./gradlew :feature:destinations:data:testDebugUnitTest --stacktrace` → **PASS** (31 tests, 0 fallos).
- `./gradlew :feature:destinations:presentation:testDebugUnitTest --stacktrace` → **PASS** (3 tests, 0 fallos).
- `./gradlew :androidApp:testDebugUnitTest --stacktrace` → **PASS pero NO-SOURCE**: androidApp no declara unit tests propios. La cobertura unitaria Android real vive en los testDebugUnitTest de domain/data/presentation (variantes Android de los módulos KMP).
- `./gradlew :androidApp:assembleDebug --stacktrace` → **PASS** (APK debug generado).
- Verificación de zona horaria (domain, re-ejecutado renovando el daemon de Gradle en cada corrida para que la `TZ` sea efectiva en la JVM de test): `TZ=UTC`, `TZ=America/La_Paz` (UTC−4), `TZ=Pacific/Kiritimati` (UTC+14) y `TZ=Pacific/Midway` (UTC−11) → **PASS (43 tests, 0 fallos) en todas**. Nota: `Asia/Kiritimati` no es una zona válida de tzdb (la JVM la resuelve como GMT); la zona real de Kiritimati (UTC+14) es `Pacific/Kiritimati`.

El primer run de CI posterior al push del commit de cierre (`e3f45f6`, "Android validation" run #108) completó con **success** bajo gate estricto: domain, data, presentation y androidApp tests más assembleDebug ejecutados y verdes (https://github.com/benitoanagua/AgendaQr/actions/runs/35947595021). Runs futuros deben mantenerse verdes; cualquier fallo vuelve a abrir el gate.

### Bugs corregidos en esta pasada

1. Búsqueda global: un QR no aparecía al buscar por el nombre de su contexto (`SearchAgendaQrUseCase`).
2. Deduplicación de importación: todas las ocurrencias de una huella duplicada se marcaban como duplicadas, bloqueando a la primera ocurrencia válida; un DESCONOCIDO con la misma huella convertía a un QR válido en duplicado (`ImportBatch`).
3. Guardado de lote: los candidatos DESCONOCIDO/duplicados no se reportaban como omitidos (`SaveImportBatchUseCase`).
4. Matching: "mismo día" usaba una ventana rodante de 24 h; dos instantes separados exactamente 24 h (días de calendario distintos) empataban y generaban ambigüedad falsa (`SuggestReceiptAssociationUseCase`). Los umbrales de decisión (señal fuerte y margen) no se modificaron.

### Tests añadidos

- `ReceiptMatchingTest.same_day_signal_uses_calendar_days_not_a_rolling_24h_window` (límite exacto de medianoche, zona horaria inyectada).
- `ImportBatchTest.unknown_between_duplicate_occurrences_keeps_first_valid_as_original`.
- `ImportBatchPersistenceTest.duplicate_occurrences_in_one_batch_are_not_persisted_twice` (payload del duplicado se conserva para reintento).
- `AgendaSearchTest.qr_is_not_returned_when_neither_it_nor_its_context_match` (sin falsos positivos vía contexto).
- `DeleteOperationWithHistoryTest` (eliminación con comprobantes: archivos, histórico mínimo, receipts ajenos intactos; operación inexistente sin efectos).
- `SyncMutationProcessorTest` (offline → online: UPSERT de contexto, operación, comprobante con bytes locales, DELETE de destination, fallo con backoff y reintento).

## Pendientes reales

### P0
- ninguno conocido.

### P1 — validación runtime
1. E2E offline → online con kill/restart.
2. cambio de usuario en dispositivo real.
3. validación runtime de estados de importación.
4. validación runtime de asociación de comprobantes.
5. validación de accesibilidad en dispositivo.
6. prueba de errores recuperables en Android.
7. revisión visual manual del laboratorio de componentes en el navegador (claro/oscuro con el toggle, anchos estrecho/medio/escritorio; comandos en `docs/04-ux/03-laboratorio-componentes-compose.md`) y de sus hallazgos de tema oscuro.

### P2 — iOS
- iOS **compila** (verificado en Linux: `:shared`, `:feature:destinations:data`, `:feature:destinations:presentation` y `:core:ui` en `compileKotlinIosSimulatorArm64` → BUILD SUCCESSFUL); **sin validación runtime** (sin Xcode en este entorno).
- NetworkMonitor iOS implementado con `nw_path_monitor_*` (`PlatformNetworkMonitor.ios.kt`: callbackFlow + distinctUntilChanged + queue dedicada + cancel); falta validación en Xcode.
- almacenamiento de comprobantes iOS migrado a Foundation/Application Support y aislado por usuario.
- NetworkMonitor iOS implementado con NWPathMonitor; falta validación en Xcode.
- puntos a verificar en Xcode al primer build: firma exacta de `NSSearchPathForDirectoriesInDomains`/enums ObjC en Kotlin 2.2, nulabilidad del bloque `pathUpdateHandler`, `options = 0u` en `NSData.create(base64EncodedString=...)`, y `keyWindow` (deprecado desde iOS 13) en `ShareQr.ios.kt` — si `keyWindow` devuelve nil en runtime, el share de QR no se presenta.
- completar adquisición iOS de importación bulk (`ImportBatchControls.ios.kt` es stub; `IosQrImportController` es no-op). Camera / Gallery / Multiple / Share en iOS NO están implementados.
- ejecutar compilación y pruebas en Xcode.

### P2 — pruebas de plataforma
- prueba manual de camera/gallery/share.
- prueba de imágenes no-QR.
- prueba de PDFs.
- prueba de lote grande.
- prueba de duplicados.
- prueba de recuperación después de matar la aplicación durante sincronización.

### P3 — capacidad futura
- OCR.
- contabilidad.
- facturación.
- CRM.
- wallet.
- roles multiempresa.
- biometría.
- realtime avanzado.
- paginación de almacenamiento a gran escala.

## Riesgos conocidos
- nowMillis() puede producir colisiones teóricas en el mismo milisegundo; las guardas de persistencia reducen el impacto.
- observe() mantiene colecciones en memoria; el render ya está limitado/paginado en UI, pero el almacenamiento completo seguirá siendo un riesgo a escalas muy grandes.
- iOS no tiene todavía evidencia de ejecución real.

## Regla de cierre

Código presente no equivale a capacidad validada.
Una prueba conceptual PASS no equivale a una prueba runtime PASS.
La siguiente etapa debe cerrar primero evidencia de ejecución y recuperación; no agregar funcionalidades fuera del contrato V1.
## Aplicación del backlog priorizado — 2026-10-01

Esta pasada aplicó los cambios deterministas de A6, A1, A2 y A4 sobre el snapshot local del repositorio. La línea base de Gradle **no pudo ejecutarse** porque el entorno de ejecución no pudo resolver `services.gradle.org` para descargar Gradle 8.13; por tanto ningún resultado de tests/build se declara PASS en esta pasada.

### Cambios aplicados

- **A6:** `productionKotlinSources()` ahora incluye `androidApp`; CI ejecuta `verifyAgendaQrArchitecture`, `:core:ui:testDebugUnitTest`, lint, release assembly y `componentLabWeb`; la validación de secretos Supabase se omite únicamente en PRs provenientes de forks.
- **A1:** creada `supabase/migrations/006_context_fk_set_null_columns.sql`, sin modificar 001–005, para aplicar `ON DELETE SET NULL (context_id)` a las tres FKs compuestas. Añadida prueba de aceptación SQL en `supabase/tests/006_context_fk_set_null_columns.sql`. Pendiente ejecutar contra PostgreSQL/Supabase real.
- **A2:** `PendingSyncMutation` incorpora `revision`; `complete` elimina solo la revisión reclamada y devuelve a `PENDING` una mutación que cambió durante el envío. La corrupción JSON ya no se convierte silenciosamente en cola vacía; el payload corrupto se conserva bajo una clave `.corrupt.<timestamp>` en los stores de plataforma. El drenaje ordena recursos por dependencia y difiere hijos cuando existe un padre pendiente/fallido. Se añadieron regresiones unitarias. Pendiente ejecución de Gradle.
- **A4:** `targetSdk` actualizado a 36; release deja de usar keystore debug silenciosamente y solo lo permite mediante `-PallowDebugSigningForRc=true`; `android:allowBackup` pasa a `false`. CI añade lint y release assembly. Pendiente ejecución/validación en Android.

### Pendientes reales después de esta pasada

- **A2:** validar runtime de cola, corrupción y orden de dependencias; revisar que el almacenamiento de evidencia `.corrupt.*` sea suficiente como estado observable de UI.
- **A3:** clasificación de errores permanentes/`DEAD`, pull/reconexión, prevención de resurrección de borrados, conexión completa de `NetworkMonitor`, borrado remoto de comprobantes por id y lifecycle de scopes.
- **A4:** Back/predictive back y validación Android 16 en dispositivo/emulador; firma release real.
- **A5:** cableado de casos de uso restantes y confirmación de que cada flujo está definido en el contrato UX antes de implementarlo.
- **A7:** validación física y E2E.
- **M1–B:** sin iniciar; respetar el orden del backlog.

### Evidencia de esta pasada

- Se ejecutaron comprobaciones estáticas locales sobre los archivos modificados y todas las invariantes comprobables devolvieron PASS.
- `./gradlew --version` / tareas Gradle no pudieron ejecutarse por falta de acceso DNS a `services.gradle.org`.
- No se declara ningún test Kotlin, build Android, migración Supabase ni laboratorio Wasm como PASS hasta poder ejecutarlo.

## Validación de cierre — 2026-10-02 (ramas fix/* + main, Linux x86_64, JDK 17)

Línea base `main` (1f3e6e9): `check`, `:androidApp:assembleDebug`, `:androidApp:lint`, `componentLabWeb` y `verifyAgendaQrArchitecture` → **PASS**.

Conteos reales de `testDebugUnitTest` (0 fallos, 0 errores):

```text
core:ui = PASS (41 tests)
domain = PASS (43 tests)
data = PASS (58 tests)
presentation = PASS (3 tests)
androidApp = PASS pero NO-SOURCE (sin unit tests propios)
global = PASS (145 tests)
```

- Zona horaria (domain, con `./gradlew --stop` entre corridas): `TZ=America/La_Paz` y `TZ=Pacific/Kiritimati` → **PASS** en todas las ramas tocadas.
- iOS: `compileKotlinIosSimulatorArm64` de `shared`, `data`, `presentation` y `core:ui` → **PASS** (compilación Kotlin/Native; link/runtime en Xcode sigue pendiente).
- CI añade job `ios-compile` (macos-latest, JDK 17), job no bloqueante `supabase-acceptance` (postgres:15 + `supabase/tests/run-acceptance.sh`) y el check de secretos Supabase ahora advierte en vez de fallar (los unit tests no usan secretos).
- Documentos renumerados sin prefijos duplicados en `04-ux`, `06-arquitectura` y `08-validacion`; `VALIDATION_FINAL_417ea2a.md` archivado en `docs/08-validacion/05-validacion-final-417ea2a.md` con la contradicción de cierre resuelta.

Sigue BLOCKED (ver entregable de cierre): keystore de producción, runtime Supabase (sin daemon Docker), link/runtime iOS en Xcode, runtime del laboratorio (sin navegador), tests instrumentados/E2E en dispositivo.


## Correcciones UX/MVP aplicadas — 2026-10-04

Sobre `main`, sin declarar runtime PASS:

- `supabase/.branches/` quedó excluido del tracking mediante `.gitignore`.
- `XauxaTheme` dejó de forzar dark theme y respeta `isSystemInDarkTheme()`.
- S01 Inicio recuperó la jerarquía congelada: Buscar domina; Agregar QR y Registrar son acciones secundarias; Contextos dejó de ser navegación principal.
- S02 Añadir ordena adquisición como Galería → Galería (varios) → Cámara y documenta Desde otra app como entrada de primer nivel mediante Compartir.
- La asociación de comprobantes ahora expone Desasociar desde el detalle de operación y reutiliza `UnassociateComprobanteUseCase`; se añadió regresión de asociación/desasociación.
- La advertencia de comprobante duplicado dejó de bloquear el guardado: el usuario puede Guardar de todos modos.
- La importación de comprobantes conserva el MIME recibido en `SaveComprobanteUseCase`.
- La transición turnstile de destinos invierte dirección en rutas de regreso; el predictive back físico de Android sigue pendiente de validación runtime.
- Estas correcciones son cambios de código; no convierten por sí mismas los journeys runtime en PASS.



### Correcciones adicionales posteriores

- RF-15: se incorporó edición de operaciones desde el detalle. Los campos sensibles se comparan contra el estado original y, si existen comprobantes, se solicita una única confirmación contextual antes de guardar; el archivo del comprobante no se modifica.
- RF-18: la búsqueda textual de operaciones ahora incluye fecha y tipo además de importe, moneda, persona/entidad, concepto y nota.
- RF-12: si la sugerencia automática de asociación de un comprobante no encuentra coincidencias, la bandeja ofrece igualmente las operaciones existentes para selección manual.
- Estas capacidades siguen requiriendo ejecución runtime para declararse PASS.


- RF-14: la advertencia de duplicado ahora permite abrir el comprobante existente directamente o guardar otra copia de forma intencional; se corrigió el cableado de `allowDuplicate`.
- RF-13: un resultado de comprobante en la búsqueda global abre directamente el visor del comprobante, incluso si está asociado o no está en la bandeja visible.
- UX: se retiró el filtro "Recientes" de destinos porque el contrato de dominio había eliminado la regla de "uso reciente"; no se inventa una ventana temporal sin especificación.
- Los cambios anteriores son correcciones estáticas sobre `main`; CI queda pendiente y no se declara runtime PASS.

## Cierre MVP local — 2026-10-04 (main 75a205d → 4b04587, Linux x86_64, JDK 17)

Pasada de cierre sobre `main` con ejecución real: emulador Android (AVD `agenda_qr`, API 34), Supabase local completo vía podman (`supabase start` con socket docker-compatible: Auth+Postgrest+Storage en `127.0.0.1:54321`, migraciones 001–006 aplicadas) y APK debug construido con `SUPABASE_URL=http://10.0.2.2:54321`. La regla de firma de release (A4) exige keystore de producción; toda compilación local de `androidApp` usa `-PallowDebugSigningForRc=true` (guard intencional, no defecto).

### BUILD — PASS

- `./gradlew build -PallowDebugSigningForRc=true` → **BUILD SUCCESSFUL** (raíz, todas las variantes: Android debug+release, compilaciones iOS/Native, wasm del laboratorio, todos los tests, lint).
- `./gradlew verifyAgendaQrArchitecture` → **PASS** (compliance Xauxa + límites de arquitectura + invariants web).
- `./gradlew componentLabWeb` → **PASS**.
- Nota: `./gradlew build` sin el flag falla en configuración por el guard de firma release de A4 (keystore de producción no disponible en este entorno); es comportamiento intencional.
- Corrección de infraestructura: `kotlin.daemon.jvmargs=-Xmx3g` (la generación del binario wasm moría con GC overhead limit exceeded).

### TESTS — PASS

`testDebugUnitTest`, ejecutados en el build completo (0 fallos, 0 errores):

```text
domain        = PASS (43 tests)
data          = PASS (69 tests)
presentation  = PASS (19 tests)
core:ui       = PASS (41 tests)
androidApp    = NO-SOURCE (sin unit tests propios, como está documentado)
global        = PASS (172 tests)
```

Los tests de presentación declarados históricamente como "3 tests" nunca habían compilado: el commonTest introducido en merges recientes tenía errores de compilación en todas las plataformas. Las suites de sync nombradas en la pasada de cierre (SyncResilienceTest, SyncRecoveryCoordinatorTest, SyncQueueIdempotencyTest, SyncQueueTest, SyncMutationProcessorTest, SyncComprobanteRepositoryTest, SyncQueueUserIsolationTest) ejecutaron PASS dentro del total de data.

### Supabase — PASS (aceptación SQL) / PASS (runtime local)

- `supabase/tests/run-acceptance.sh` contra PostgreSQL 15 real (podman) → **ACCEPTANCE PASS** (migraciones 001–006 + prueba de FK `ON DELETE SET NULL` del 006). RLS *behavior* validado en runtime local vía API con sesiones de usuario (ver aislamiento).
- Migraciones aplicadas en el stack local; `supabase migration list` muestra 001–006 aplicadas.

### Runtime Android — PASS (flujos ejecutados)

Evidencia recolectada con uiautomator dumps, consultas SQL directas a Postgres, `run-as` para inspección de la cola y análisis de píxeles para tema. Todas contra el backend local real.

- **Auth**: signup e2e.a, sesión persistente, sign-out (tras corregir ANR), signin e2e.b, vuelta a e2e.a con datos intactos.
- **FLUJO A (QR por galería)**: Añadir → Galería → QR reconocido por ZXing → S09 "Revisar QR" → Guardar → fila en `public.destinations` → edición de metadata (nombre "Carniceria Don Bife") → visible en DB → búsqueda global → apertura desde resultado.
- **FLUJO B (desde otra app)**: `ACTION_SEND image/png` vía resolver del sistema (con grant): app cerrada (arranque frío) y app abierta → importación → diálogo/visor correctos. Un `am start` directo sin paso por el resolver no otorga el URI (comportamiento del harness, no de la app); la lectura de una URI sin permiso ya no tumba el proceso (banner recuperable "No se pudo abrir el archivo compartido.").
- **FLUJO C (operación)**: PAGO y COBRO creados; occurredAt ≠ createdAt verificado (02/10 vs createdAt real); edición de monto (150.50→175→180) con push verificado en Postgres; búsqueda por texto ("Carniceria", "Mercado") desde la lista y desde la búsqueda global.
- **FLUJO D (comprobante adjunto)**: "Adjuntar comprobante ahora" → bandeja → "Asociar a operación existente" → ambigüedad "¿A cuál corresponde?" → asociación → **retorno al detalle** (fix PR #78 verificado) → abrir visor → compartir (FileProvider "Sharing image") → Desasociar → volver a asociar. Edición de campo sensible con comprobante asociado muestra la advertencia exacta del contrato ("Esta operación tiene comprobantes. El comprobante no será modificado." [Cancelar] [Guardar cambio]) y el comprobante no se toca.
- **FLUJO E (comprobante independiente)**: share de imagen → "Comprobante recibido" → Guardar sin operación → bandeja ("Comprobantes sin asociar: 1") → asociación posterior. PDF compartido → comprobante con "Archivo: pdf", visor con "PDF adjunto (sin vista previa)" y Compartir funcional.
- **FLUJO F (duplicados)**: mismo archivo compartido dos veces → diálogo con el texto congelado exacto "Parece que este comprobante ya está guardado." [Ver existente] [Guardar de todos modos]; ambas acciones ejecutadas y verificadas (copia deliberada persistida; "Ver existente" abre el visor). La detección no bloquea el guardado deliberado.
- **Sincronización E2E**: UPSERT de operación/comprobante/destino contra backend real (filas verificadas en SQL); UPDATE directo online; pull de cambios remotos (una fila insertada por curl apareció en el app).
- **Offline/online**: modo avión → banner "Sin conexión…" → operación COBRO creada → "Sincronización pendiente: 1" persistido tras kill de la app → al reactivar red, drain automático sin intervención → fila en Postgres, cola vacía, sin duplicados, sin resurrecciones.
- **Kill/restart**: kill con mutación pendiente (estado y datos persistidos en SharedPreferences) y kill simulado durante sync (estado PROCESSING escrito en disco) → al abrir, `resetProcessing` recupera → push correcto → cola vacía. PROCESSING no queda bloqueado.
- **Aislamiento por usuario**: usuario B (e2e.b) ve cero destinos, cero operaciones, cero comprobantes (UI + PostgREST con su JWT + Storage vacío); claves locales user-scoped (dos sufijos de user id en prefs); la cola de A no aparece para B; datos de A intactos al volver. RLS efectivo.
- **Búsqueda global**: resultados QR/Actividad/Comprobante con chip de tipo semántico; QR→detalle de destino, Actividad→detalle de operación, Comprobante→visor directo (RF-13); la consulta se conserva al volver (S05); el substring "comprobante" localiza recibos por id/archivo.
- **Navegación/Back**: Inicio → Añadir → Galería → Revisar QR → Back → vuelve a **Añadir** (flujo congelado PR #78); sin instancias duplicadas de pantallas (la app enruta por estado, single-activity; `singleTask` además evita instancias paralelas de MainActivity).
- **Manifest/share/FileProvider**: `ACTION_SEND image/*` ✓ (vivo y frío), `ACTION_SEND application/pdf` ✓, FileProvider ✓ ("Sharing image", "Sharing 1 file" con `shared_comprobante.pdf`), MIME preservado, intents nuevos con Activity viva ✓ (onNewIntent). `ACTION_SEND_MULTIPLE`: el harness del shell (`--esa`) entrega `String[]`, no `ArrayList<Parcelable>`, así que no es reproducible por adb; la ruta múltiple quedó validada por "Galería (varios)" (S12 con ✓ reconocidos / ? pendientes, superficie única) y el parsing por inspección.
- **Cámara (S03)**: permiso solicitado correctamente → cámara del sistema → foto → retorno a la app sin crash y sin import falso. La escena virtual del emulador no contiene QR, así que el *decod* de un QR real por cámara queda NO VALIDADO.
- **Tema**: dark (`cmd uimode night yes`) y light verificados por análisis de píxeles (fondo/texto/inversión + acento de marca); sin crash en el cambio de configuración.
- **Accesibilidad (estática + targets)**: touch targets ≥ 48dp (440dpi → 132px) en Home y detalle; acciones icon-only etiquetadas ("Marcar favorito", "Buscar en Agenda QR", "Más opciones"); headings semánticos en títulos. TalkBack interactivo queda NO VALIDADO (sin entorno de lector de pantalla en headless).
- **Laboratorio de componentes (Wasm)**: `componentLabWeb` construido y servido; cargado en Brave headless (Chromium) con **0 errores de consola**, render de paleta Xauxa oscura + acento de marca, y navegación interactiva con cambios de pantalla verificables por diff de píxeles; las 40 pruebas de integridad del catálogo pasan dentro de core:ui. La matriz visual completa de estados por componente requiere revisión humana → se mantiene el pendiente de revisión manual.

### iOS — compilación PASS / runtime BLOCKED

- `compileKotlinIosSimulatorArm64` de `shared`, `domain`, `data`, `presentation` y `core:ui` (+ test de presentation y core:ui) → **PASS** en Linux.
- Runtime, firma, cámara, galería, share sheet, Foundation/Application Support real, NetworkMonitor real → **BLOCKED** (sin Xcode en este entorno).

### Defectos encontrados y corregidos en esta pasada (todos con evidencia runtime o test)

1. **`main` no compilaba** (presentation): literales de string partidos en líneas físicas en el diálogo de duplicado; `OperationAction.Edit` inexistente cableado al botón "Editar"; `operationTypeLabel` privada usada desde otro archivo. Además el provenance se mostraba como enum técnico (`it.name`). → commit 1b74631 (+ regresión OperationEditActionTest).
2. **El commonTest de presentation nunca compiló en ninguna plataforma** (los "tests" no se ejecutaban): imports duplicados ambiguos, llamadas suspend en lambda no-suspend, parámetro `unassociate` faltante. → a9e47f4.
3. **Dos clases privadas top-level con el mismo nombre** (`MemoryQueueStore`) en un paquete rompían la compilación de tests en Kotlin/Native. → a9e47f4.
4. **Toda la sincronización remota estaba rota** (PGRST204): las filas serializaban camelCase contra columnas snake_case; ningún push llegó jamás al backend. Encontrado con Supabase local real. → 26f74c6 (+ RemoteRowSerializationTest). Incluye manifest debug con cleartext para validar contra stack local.
5. **La búsqueda global (S04) era inalcanzable**: `onOpenSearch`/`onOpenContexts` eran parámetros muertos; la barra de inicio filtraba destinos prometiendo búsqueda global. → b309395.
6. **Doble superficie de importación**: cada import emitía lote (S12) y revisión (S09); tras "Guardar reconocidos" reaparecía una revisión vieja del mismo archivo (riesgo de doble guardado). → b309395.
7. **Shares con app cerrada se perdían en silencio**: canales sin retención; y los shares con URI ilegible/revocada **tumbaban el proceso** (FileNotFoundException/SecurityException sin manejar). → b309395.
8. **El diálogo de comprobante recibido era invisible** fuera de la superficie de operaciones. → b309395.
9. **kotlinx-datetime 0.6.2 vs 0.7.1**: supabase-kt fuerza 0.7.1 y `:domain` compilaba contra la clase real de 0.6.x → `NoClassDefFoundError` en runtime → **toda sugerencia de asociación fallaba** y el diálogo quedaba en "Estamos analizando" para siempre. Catálogo actualizado a 0.7.1 + degradación honesta a NONE. → b309395 (+ OperationSuggestionFailureTest).
10. **Drains fallaban con cualquier operación con monto**: `amount numeric(20,6)` llega como número JSON y el row lo esperaba como String. Serializador tolerante con canonicalización. → 9049f6f (+ regresión).
11. **"Cerrar sesión" congelaba la app (ANR)**: la propiedad `signOut` quedaba eclipsada por la función miembro y `runCatching { signOut() }` se recursaba infinitamente en el hilo principal. Encontrado con el trace del ANR durante la prueba de cambio de usuario. → 0f7938d (+ AuthViewModelSignOutTest).
12. **Shares vía resolver acumulaban instancias paralelas de MainActivity**: la instancia vieja (composición viva, colectores activos) consumía el canal retenido y el diálogo aparecía en la instancia invisible; un PDF compartido no producía UI. `singleTask` + entrega por `onNewIntent`. → 2842854.
13. **Enums técnicos en resultados de búsqueda** (PAGO/COBRO/RECIBIDO): mapeados a semántica en el render. → 2842854 (+ GlobalSearchResultSemanticsTest).
14. **Fechas en UTC**: el editor proponía "mañana" como fecha por defecto en UTC-4 por la tarde (observado: dispositivo 03/10 23:xx, editor 04/10/2026). Fechas civiles ahora en la zona del dispositivo (inyectable para tests). → 4b04587 (+ regresión).

### Defecto de harness documentado (no de la app)

Una cirugía manual sobre `shared_prefs/agendaqr.xml` con re-escape incorrecto corrompió el XML y la app cayó al backup (pérdida de un dato de prueba). Repetida correctamente (reemplazo crudo byte a byte), la recuperación de PROCESSING funcionó. Se lista para dejar constancia de que esa pérdida fue artefacto de prueba, no del producto.

### Pendientes reales después de esta pasada

- **Contextos sin vía de creación (FAIL como capacidad, gap de contrato)**: `SaveContextUseCase` existe pero ninguna UI lo invoca; no hay forma de crear un contexto en toda la app, por lo que S06, el selector S08 y los resultados de búsqueda de tipo Contexto están muertos en la práctica. El contrato congelado no define un flujo de creación, y el backlog A5 exige confirmar el flujo en el contrato UX antes de implementarlo: no se inventó UX en esta pasada. Es la brecha principal entre lo documentado ("main contiene Contextos") y la realidad.
- **iOS runtime** — BLOCKED (sin Xcode): launch, login, persistencia, import, cámara/galería/share (siguen siendo stubs de adquisición), NetworkMonitor real, restart.
- **TalkBack interactivo y predictive back físico** — NO VALIDADO en headless.
- **Decod de QR real vía cámara** — NO VALIDADO (la escena virtual del emulador no contiene QR).
- **`ACTION_SEND_MULTIPLE` con URIs parcelables reales** — NO VALIDADO por harness (la ruta múltiple sí está validada por Galería (varios)).
- **Revisión visual humana completa del laboratorio** (claro/oscuro, anchos, matriz de estados) — pendiente.
- **RLS *behavior* contra el proyecto remoto del usuario** — la validación fue contra el stack local (mismas migraciones).

### Regla de cierre (se mantiene)

Código presente no equivale a capacidad validada. Esta pasada convirtió afirmaciones documentales en evidencia de ejecución o en defectos corregidos; lo que sigue pendiente está listado con su razón exacta.
