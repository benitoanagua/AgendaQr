# Manifiesto de fuentes

## Producto y negocio

`AgendaQr-main/docs/` es la fuente de verdad para producto, dominio, requisitos, alcance, decisiones y comportamiento aprobado.

## UX funcional

La especificación UX/UI V1 define el comportamiento funcional del producto.

## UI/UX visual

Xauxa es la fuente única de:
- fundamentos visuales;
- tokens;
- componentes;
- patterns;
- estados visuales;
- accesibilidad;
- movimiento;
- lenguaje de composición.

Agenda QR no conserva una segunda interpretación del sistema visual.

## Arquitectura técnica

WaraWerse es referencia para Kotlin Multiplatform, Compose, modularización, build logic y testing.

## Regla de implementación

La implementación debe expresar el dominio de Agenda QR usando Xauxa como lenguaje visual único. Cuando una pantalla necesite una composición nueva, se compone primero con primitives Xauxa existentes.
