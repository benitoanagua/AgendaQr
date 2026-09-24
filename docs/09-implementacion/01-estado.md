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
- búsqueda global sobre Context, QR, Activity y Comprobante.
- operaciones PAGO/COBRO con histórico mínimo al eliminar.
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
- duplicados quedan para revisión.
- QR y comprobantes se persisten de forma independiente.
- payload temporal eliminado después de persistencia exitosa.
- fallos aislados por candidato y recuperables.
- doble guardado protegido.
- la clasificación no-QR es deliberadamente conservadora.

### Matching de comprobantes
- asociación existente se conserva.
- un único candidato fuerte puede proponerse.
- candidatos débiles no se convierten en asociación automática.
- múltiples candidatos se presentan como ambigüedad.
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

## CI

PR #42 amplió el workflow para ejecutar domain, data, presentation, androidApp unit tests y assembleDebug.

El cambio está mergeado en main mediante a75a8cf996741c3a94ba5a3d97656653f1d20366.

El run de main asociado es 35940764289; al momento de esta actualización seguía en ejecución. No se debe declarar PASS hasta que finalice.

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

### P2 — iOS
- iOS sigue arquitectónicamente preparado pero no está validado funcionalmente.
- reemplazar IosComprobanteFileStore en memoria por almacenamiento persistente con Foundation.
- implementar NWPathMonitor real en PlatformNetworkMonitor.ios.kt.
- completar adquisición iOS de importación bulk.
- conectar Camera / Photos / Share mediante el boundary nativo existente.
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