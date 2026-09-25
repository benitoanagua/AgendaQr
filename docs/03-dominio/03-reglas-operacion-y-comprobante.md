# Reglas de dominio — Operaciones y comprobantes

## Operación

Una operación representa un registro del usuario sobre algo que ocurrió.

Tipos permitidos:

- PAGO;
- COBRO.

La creación mínima requiere:

```text
id + type + occurredAt
```

(`createdAt` lo genera el sistema al registrar; no lo aporta quien crea la operación.)

Los datos de importe, moneda, persona/entidad, destino, concepto y nota son opcionales.

## Comprobante

El comprobante es un recurso independiente.

Puede existir:

- sin operación;
- asociado a una operación.

Una operación puede tener cero o múltiples comprobantes.

Un comprobante puede pertenecer a cero o una operación.

## Proveniencia

`ENVIADO`, `RECIBIDO` y `DESCONOCIDO` describen cómo llegó el comprobante al usuario/Agenda QR. No expresan dirección del dinero.

## Edición

Una edición sensible de una operación con comprobantes requiere una única confirmación contextual.

Editar la operación nunca edita el archivo del comprobante.

## Eliminación

Al eliminar una operación se eliminan los comprobantes asociados y se conserva únicamente:

- fecha;
- tipo;
- importe;
- persona/entidad.

La eliminación de un comprobante deja intacta la operación.

## Duplicados

Un posible duplicado genera advertencia, pero el usuario puede guardar de todos modos.
