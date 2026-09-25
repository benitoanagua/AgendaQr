# Fuentes de verdad y precedencia

## 1. Agenda QR — autoridad de producto

Es la única fuente para producto, requisitos, dominio, alcance, decisiones y reglas de negocio. Cubre también el comportamiento funcional UX (contrato UX/UI V1 congelado).

## 2. Xauxa — autoridad visual

Xauxa define la UI/UX visual completa: tokens, fundamentos, componentes, patterns, estados, accesibilidad, movimiento, tipografía y composición.

No se mantienen excepciones visuales heredadas ni una paleta propia de Agenda QR.

## 3. WaraWerse — referencia técnica

Se utiliza solo como referencia para Kotlin Multiplatform, Compose, modularización, Gradle/build logic y prácticas de testing. No introduce lógica de negocio.

## Matriz por área

| Área | Fuente de verdad | Uso |
|---|---|---|
| Producto | Agenda QR | obligatorio |
| Dominio | Agenda QR | obligatorio |
| Requisitos | Agenda QR | obligatorio |
| Alcance y límites | Agenda QR | obligatorio |
| Decisiones | Agenda QR | obligatorio |
| UX funcional | Agenda QR | obligatorio |
| UI/UX visual | Xauxa | obligatorio |
| Tokens | Xauxa | obligatorio |
| Componentes y patterns | Xauxa | obligatorio |
| Accesibilidad y motion | Xauxa | obligatorio |
| Kotlin/KMP | WaraWerse | referencia técnica |
| Compose | WaraWerse | referencia técnica |
| Build logic | WaraWerse | referencia técnica |
| Testing practices | WaraWerse | referencia técnica |

## Resolución de conflictos

1. Negocio: Agenda QR gana.
2. UX funcional: Agenda QR gana.
3. Visual, componentes, tokens, motion y accesibilidad: Xauxa gana.
4. Arquitectura técnica: WaraWerse es solo referencia.
5. Una implementación no puede introducir una variante visual local de Xauxa.

## Regla de implementación

El código de producto combina reglas de negocio de Agenda QR con el lenguaje visual de Xauxa. Una discrepancia visual se resuelve a favor de Xauxa. Cuando una pantalla necesite una composición nueva, se compone primero con primitives Xauxa existentes.
