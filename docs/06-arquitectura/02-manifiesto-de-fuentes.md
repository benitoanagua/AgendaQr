# Manifiesto de fuentes

## Producto y negocio

`AgendaQr-main/docs/` es la fuente de verdad para producto, dominio, requisitos, alcance, decisiones y comportamiento aprobado.

## UI/UX

- `Xauxa Design System` — especificación normativa.
- `XauxaXcan-main/` — implementación de referencia de componentes y patrones.

Si existe una discrepancia entre una implementación histórica de XauxaXcan y una regla explícita del Design System, prevalece el Design System.

## Arquitectura técnica

`warawerse-game-main/` es referencia para Kotlin Multiplatform, Compose, modularización, build logic y prácticas de testing compatibles.

WaraWerse no es dependencia de runtime ni fuente de negocio de Agenda QR.

## Proyecto destino

El resultado final vive en `AgendaQr-main/`.
