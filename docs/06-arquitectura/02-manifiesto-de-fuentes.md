# Manifiesto de fuentes

## Producto y negocio

`AgendaQr-main/docs/` es la fuente de verdad para producto, dominio, requisitos, alcance, decisiones y comportamiento aprobado.

## UX funcional

La especificación UX/UI V1 congelada en `docs/04-ux/02-especificacion-ux-ui-v1.md` define el comportamiento de interacción aprobado para V1.

## UI/UX visual

- `Xauxa Design System` — especificación normativa.
- `Xauxa-main/` — implementación de referencia de componentes y patrones.

Si existe una discrepancia entre una implementación histórica de Xauxa y una regla explícita del Design System, prevalece el Design System.

## Arquitectura técnica

`warawerse-game-main/` es referencia para Kotlin Multiplatform, Compose, modularización, build logic y prácticas de testing compatibles.

WaraWerse no es dependencia de runtime ni fuente de negocio de Agenda QR.

## Proyecto destino

El resultado final vive en `AgendaQr-main/`.

## Regla de implementación

La implementación debe satisfacer el contrato UX/UI congelado sin reinterpretarlo como una nueva fuente de negocio. Si la implementación encuentra una contradicción real con dominio/requisitos, debe documentarse antes de cambiar la UX.
