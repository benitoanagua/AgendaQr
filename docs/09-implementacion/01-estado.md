# Estado de implementación — alineación V1

## Situación

La especificación V1 está aprobada y pasa a ser el contrato para la implementación.

El repositorio ya contiene la base KMP de destinos QR y ahora incorpora la primera etapa de implementación de operaciones y comprobantes.

## Base existente

- KMP Android/iOS;
- `androidApp` e `iosApp`;
- módulos domain/data/presentation;
- persistencia local de destinos;
- búsqueda, favoritos y categorías;
- importación QR;
- QR fullscreen;
- share inbound/outbound;
- contrato visual Xauxa.

## Implementado en esta etapa

- modelo de `Operation`;
- modelo de `Comprobante`;
- `OperationRepository`;
- `ComprobanteRepository`;
- persistencia local serializada de operaciones;
- persistencia local serializada de comprobantes;
- repositorios locales Android/iOS;
- soporte para comprobantes sin operación;
- asociación y desasociación mediante `operationId`;
- pruebas unitarias de guardar, actualizar, recuperar, eliminar y recargar desde el mismo store;
- rechazo de IDs duplicados.

## Estado tras auditoría V1 (2026-09)

- autenticación Supabase (signIn/signUp/signOut, SessionStatus, JWT);
- RLS por `user_id` en destinations/operations/comprobantes/deleted_operation_history;
- Storage privado `comprobantes` con prefijo `<user_id>/`;
- persistencia local user-scoped: `agendaqr.destinations.v1.<user_id>`, `agendaqr.operations.v1.<user_id>`, `agendaqr.comprobantes.v1.<user_id>`, `agendaqr.deleted_operations.v1.<user_id>`;
- archivos comprobantes aislados `comprobantes/<user_id>/`;
- cola durable user-scoped `agendaqr.sync.queue.v1.<user_id>` con estados PENDING/PROCESSING/FAILED, backoff exponencial, deduplicación por recurso+entityId, recuperación tras restart;
- repositorios Sync* con local-first + encolado en fallo remoto, `updatedAt` para resolución de conflictos (last-write-wins);
- flujo comprobante: guardar local → Storage upload (upsert) → metadata upsert con rollback de archivo si falla;
- asociación reversible comprobante↔operación y múltiples comprobantes por operación;
- búsqueda operaciones por texto/tipo/rango de fechas;
- detección de duplicados no bloqueante por hash de bytes;
- eliminación operación con historial mínimo (date/type/amount/person) y borrado coordinado de comprobantes+archivos;
- UI: destinos, operaciones, bandeja de comprobantes sin asociar, detalle con comprobantes adjuntos, compartir;
- Android validado: `testDebugUnitTest` + `assembleDebug` + `verifyAgendaQrArchitecture` PASS;
- iOS: arquitectura KMP actual/actual lista, sin validación funcional en dispositivo (UNTESTED).

## Pendiente / Fuera de V1

- indicador offline y sync pending en UI;
- paginación de listas;
- realtime / suscripción remota continua;
- biometría/PIN opcional;
- OCR, contabilidad, facturación, CRM, wallet, roles multiempresa.

## Regla

Código existente no equivale a capacidad validada en plataforma.
