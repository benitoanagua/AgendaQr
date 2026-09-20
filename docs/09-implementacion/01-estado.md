# Estado de implementación — alineación V1

## Situación

La especificación V1 está aprobada y pasa a ser el contrato para la implementación.

El repositorio ya contiene la base KMP de destinos QR, pero su implementación todavía no representa todas las entidades y reglas V1.

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

## Pendiente de alineación V1

- modelo de `Operation`;
- repositorio/persistencia de operaciones;
- modelo de `Comprobante`;
- almacenamiento de archivos;
- asociación reversible comprobante ↔ operación;
- múltiples comprobantes;
- búsqueda de operaciones;
- histórico mínimo;
- eliminación coordinada de archivos;
- confirmación de edición sensible;
- detección de duplicados no bloqueante.

## Regla

Código existente no equivale a capacidad validada en plataforma.
