# UX — Flujos confirmados V1

La UI visual está bajo el contrato de Xauxa y permanece congelada.

## Destinos

### Captura

```text
Cámara / Galería / Múltiples imágenes / Compartir
                    ↓
               detectar QR
                    ↓
                leer/clasificar
                    ↓
              revisar candidato
                    ↓
              guardar destino
```

Un candidato QR no se persiste automáticamente como destino solo por ser detectado.

### Reutilización

```text
Buscar destino
     ↓
Abrir
     ↓
Mostrar QR
     ↓
Reutilizar / Compartir
```

## Operación primero

```text
Registrar PAGO/COBRO
        ↓
Operación
        ↓
Adjuntar comprobante después
```

## Comprobante primero

```text
Compartir/recibir comprobante
           ↓
        Guardar
           ↓
      asociar después
```

Guardar primero minimiza la interacción.

## Eliminación

Eliminar operación con comprobantes:

```text
Advertencia contextual
        ↓
Confirmar
        ↓
Eliminar archivos asociados
        ↓
Conservar histórico mínimo
```

Eliminar comprobante:

```text
Eliminar archivo
        ↓
Operación permanece
```
