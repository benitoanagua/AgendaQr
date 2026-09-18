# Modelo — Relaciones

La relación conceptual principal es:

**Usuario → administra → Destinos QR**

Cada destino contiene un QR y metadatos que permiten:

- identificarlo;
- organizarlo;
- recuperarlo.

No se agregan entidades ni relaciones con bancos, billeteras o proveedores de pago.

La razón es que el pago es externo a Agenda QR y no forma parte del núcleo.
