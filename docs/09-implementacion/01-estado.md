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
- comprobante duplicado bloqueado.
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
