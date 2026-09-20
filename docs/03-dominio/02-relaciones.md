# Modelo de dominio — Relaciones V1

```text
Usuario
 ├── administra → Destination*
 ├── registra   → Operation*
 └── conserva   → Comprobante*

Destination
 └── utiliza → QrAsset

Operation
 ├── contextualiza → Destination?
 └── tiene → Comprobante* (0..N)

Comprobante
 └── pertenece a → Operation? (0..1)
```

## Asociación reversible

```text
Comprobante → Operation A
      ↓ desasociar
Comprobante → sin operación
      ↓ asociar
Comprobante → Operation B
```

Desasociar no elimina el archivo.

## Historial de eliminación

Cuando una operación se elimina, sus comprobantes asociados se eliminan como recursos. La operación deja solamente una huella histórica mínima:

```text
date
type
amount
person/entity
status = operación eliminada
```

No se conserva el archivo del comprobante.

## Contexto histórico de destino

Cambiar el QR actual de un destino no sustituye el contexto histórico de una operación ya registrada.

Ejemplo conceptual:

```text
Enero → QR A
Marzo → QR B
```

La operación de enero no debe pasar a mostrar QR B por el simple hecho de que el destino actual haya cambiado.
