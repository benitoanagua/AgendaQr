# Modelo de dominio

## Entidad central

### Destination / Destino

Representa un destino QR reutilizable dentro de Agenda QR.

## Modelo conceptual

```text
Destination
├── id
├── name
├── qr
├── category
├── note
├── favorite
├── createdAt
├── updatedAt
└── lastUsedAt
```

## Significado

El destino es el objeto que permite identificar, organizar y recuperar un QR reutilizable.

Los campos temporales forman parte del modelo conceptual, pero sus reglas exactas de actualización no están definidas en el material y por tanto no se inventan aquí.
