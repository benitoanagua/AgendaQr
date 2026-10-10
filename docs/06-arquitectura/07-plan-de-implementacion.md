# Plan de implementación derivado de V1

## Módulo 1 — Base ya existente

- estructura KMP;
- `androidApp` e `iosApp`;
- módulos domain/data/presentation;
- convention plugins;
- version catalog;
- gates.

## Módulo 2 — Destinos QR

- Destination;
- persistencia local;
- búsqueda;
- categorías;
- favoritos;
- recuperación;
- importación y revisión;
- mostrar/compartir;
- reemplazo y eliminación.

## Módulo 3 — Operaciones

- Operation;
- PAGO/COBRO;
- `occurredAt` y `createdAt`;
- atributos opcionales;
- persistencia local;
- búsqueda por fecha/tipo/importe/persona-entidad.

## Módulo 4 — Comprobantes

- Comprobante first-class;
- guardar sin operación;
- provenance;
- asociación reversible;
- múltiples comprobantes;
- almacenamiento de archivos;
- compartir;
- eliminación;
- detección de duplicados no bloqueante.

## Módulo 5 — Integridad

- advertencia de cambios sensibles;
- eliminación de comprobantes al eliminar operación;
- histórico mínimo;
- preservación de contexto histórico de destino.

## Módulo 6 — Protección y validación

- biometría/PIN opcional;
- pruebas funcionales;
- validación Android;
- boundaries iOS;
- validación iOS mediante Xcode.

## Regla

No se implementa fuera del alcance V1 para completar listas de trabajo. Cada cambio debe rastrearse a la especificación aprobada.
