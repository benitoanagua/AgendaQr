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
- estados PENDING / PROCESSING / FAILED.
- backoff y nextAttemptAt.
- recuperación de PROCESSING tras reinicio.
- procesamiento de CONTEXT / DESTINATION / OPERATION / COMPROBANTE.
- SyncMutationEnqueuer y LocalSyncQueue compartidos por sesión autenticada.

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
- Aislado de navegación, repositorios y servicios de producción; documentación en `docs/04-ux/03-laboratorio-componentes-compose.md` y brechas Xauxa en `docs/05-design-system/03-xauxa-brechas-y-trazabilidad.md`.
- Validación: 35 pruebas unitarias del modelo PASS (integridad, búsqueda, matriz, gobierno, patrones); compilación Wasm del host PASS y revisión en navegador headless (Chromium) con evidencia registrada en el documento del laboratorio. Host iOS: pendiente.

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
- iOS sigue sin evidencia de compilación/ejecución real en Xcode. La validación de esta pasada fue solo inspección estática en un host Linux (sin Kotlin/Native para targets Apple): los fuentes usan patrones interop conocidos (Foundation/Application Support con aislamiento por usuario, NWPathMonitor sobre callbackFlow/awaitClose, NSData.create/toByteArray), pero nada de esto sustituye una compilación real.
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