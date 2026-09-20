# Modelo de dominio — V1

## QR

Representa el instrumento QR que puede ser detectado, leído y clasificado.

```text
QrAsset
├── encoded
└── mimeType
```

La clasificación describe qué contiene técnicamente el QR. No define cómo el usuario lo organiza.

## Destination / Destino

Representa un lugar o cuenta reutilizable asociado a un QR.

```text
Destination
├── id
├── name
├── qr
└── organization
```

La organización puede conservar los metadatos aprobados para el destino, como categoría, nota y favorito.

Un destino puede existir sin operaciones.

## Operation / Operación

Representa el hecho que el usuario registra como ocurrido.

```text
Operation
├── id
├── type
├── occurredAt
├── createdAt
├── amount?
├── currency?
├── personOrEntity?
├── destination?
├── concept?
└── note?
```

Tipos:

```text
PAGO
COBRO
```

El único conjunto obligatorio para crear una operación es:

```text
id + type + occurredAt
```

## Comprobante

Es evidencia retenida por el usuario.

```text
Comprobante
├── id
├── file
├── createdAt
├── provenance?
└── operation?
```

La proveniencia es:

```text
ENVIADO
RECIBIDO
DESCONOCIDO
```

La proveniencia no representa el sentido financiero del dinero.

## Reglas temporales

`occurredAt` y `createdAt` tienen semánticas diferentes.

## Independencia

Un comprobante puede existir sin operación.

Una operación puede existir sin comprobante.

Una operación puede tener múltiples comprobantes.

Un comprobante pertenece a cero o una operación.
