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

## Pendiente de las siguientes etapas V1

- almacenamiento físico de archivos de comprobantes;
- asociación reversible como flujo de aplicación;
- múltiples comprobantes desde la UI;
- búsqueda de operaciones;
- histórico mínimo al eliminar;
- eliminación coordinada de archivos;
- confirmación de edición sensible;
- detección de duplicados no bloqueante;
- flujos `Guardar y olvidar`, `Asociación tardía`, `Registro de pago rápido` y `Compartir relámpago`;
- integración Supabase para PostgreSQL, Storage y Auth.

## Nota sobre Supabase

Supabase forma parte del MVP y se incorporará progresivamente. Esta primera etapa mantiene la persistencia local para validar el dominio y los casos de uso sin acoplarlos todavía a la infraestructura remota.

## Regla

Código existente no equivale a capacidad validada en plataforma.
