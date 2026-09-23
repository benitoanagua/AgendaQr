# Decisiones aprobadas — V1

## D-01 — Propósito

Agenda QR conserva destinos QR y respaldo de operaciones para poder recuperarlos y compartirlos.

## D-02 — Operaciones V1

Los tipos de operación son exclusivamente `PAGO` y `COBRO`.

## D-03 — Operación mínima

Una operación requiere únicamente `id`, `type` y `occurredAt`. Todo el resto es opcional.

## D-04 — Dos timestamps

`occurredAt` y `createdAt` representan eventos diferentes.

## D-05 — Comprobantes first-class

El comprobante es una entidad propia, independiente de la operación.

## D-06 — Asociación flexible

Un comprobante puede estar sin operación o asociado a una operación. La asociación es reversible.

## D-07 — Múltiples comprobantes

Una operación puede tener múltiples comprobantes.

## D-08 — Proveniencia

La proveniencia se limita a `ENVIADO`, `RECIBIDO` y `DESCONOCIDO`.

## D-09 — Guardar primero

Guardar un comprobante debe requerir el mínimo absoluto de interacción.

## D-10 — Duplicados

Se alerta sobre posible duplicado, pero no se bloquea el guardado.

## D-11 — Ediciones sensibles

Solo los cambios sensibles de una operación que ya tiene comprobantes requieren confirmación contextual.

## D-12 — Eliminación

Eliminar una operación elimina sus comprobantes asociados y conserva únicamente un histórico mínimo.

## D-13 — Destinos históricos

Cambiar el QR actual de un destino no reescribe operaciones históricas.

## D-14 — No verificación bancaria

Agenda QR no afirma que un comprobante recibido implique que el dinero fue verificado.

## D-15 — No V1

CRM, obligaciones, saldos, pagos parciales, reembolsos especializados, contabilidad, conciliación bancaria, OCR de comprobantes, IA y cloud complejo quedan fuera de V1.

## D-16 — UX

La UI/UX permanece bajo Xauxa Design System; esta consolidación no crea un sistema visual paralelo.

## D-17 — UX/UI V1 congelada

La especificación `docs/04-ux/02-especificacion-ux-ui-v1.md` es el contrato UX/UI V1 congelado.

Quedan consolidados, sin alterar las reglas de negocio existentes:

- una intención principal por pantalla;
- búsqueda global;
- Galería → Desde otra app → Cámara como jerarquía de entrada;
- registro sin contexto obligatorio;
- comprobantes independientes y asociación reversible;
- importación automática y revisión solo cuando sea necesaria;
- importación masiva tolerante a fallos parciales;
- guardado local y sincronización como estados distintos;
- errores recuperables;
- preservación de intención/draft;
- invariantes de idempotencia y recuperación.

La implementación debe adaptarse al contrato UX/UI; un fallo de implementación no modifica automáticamente la especificación.
