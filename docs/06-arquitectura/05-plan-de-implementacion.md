# Plan de implementación derivado de V1

## Fase 1 — Base ya existente

- estructura KMP;
- `androidApp` e `iosApp`;
- módulos domain/data/presentation;
- convention plugins;
- version catalog;
- gates.

## Fase 2 — Destinos QR

- Destination;
- persistencia local;
- búsqueda;
- categorías;
- favoritos;
- recuperación;
- importación y revisión;
- mostrar/compartir;
- reemplazo y eliminación.

## Fase 3 — Operaciones

- Operation;
- PAGO/COBRO;
- `occurredAt` y `createdAt`;
- atributos opcionales;
- persistencia local;
- búsqueda por fecha/tipo/importe/persona-entidad.

## Fase 4 — Comprobantes

- Comprobante first-class;
- guardar sin operación;
- provenance;
- asociación reversible;
- múltiples comprobantes;
- almacenamiento de archivos;
- compartir;
- eliminación;
- detección de duplicados no bloqueante.

## Fase 5 — Integridad

- advertencia de cambios sensibles;
- eliminación de comprobantes al eliminar operación;
- histórico mínimo;
- preservación de contexto histórico de destino.

## Fase 6 — Protección y validación

- biometría/PIN opcional;
- pruebas funcionales;
- validación Android;
- boundaries iOS;
- validación iOS mediante Xcode.

## Regla

No se implementa fuera del alcance V1 para completar listas de trabajo. Cada cambio debe rastrearse a la especificación aprobada.
