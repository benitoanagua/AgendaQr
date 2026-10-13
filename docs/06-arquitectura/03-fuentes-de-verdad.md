# Fuentes de verdad y precedencia

## 1. Agenda QR — autoridad de producto

Es la única fuente para producto, requisitos, dominio, alcance, decisiones y reglas de negocio. Cubre también el comportamiento funcional UX (contrato UX/UI V1 congelado).

## 2. Ant Design Mobile — autoridad visual

Ant Design Mobile define la referencia visual de tokens, componentes, estados, formularios, listas y feedback, replicada de forma nativa en Compose. Su contrato normativo es `docs/05-design-system/00-agendamobile-contrato-normativo.md`; ADR-0014 establece su precedencia.

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
| UI/UX visual | Ant Design Mobile / ADR-0014 | obligatorio |
| Tokens | Ant Design Mobile replicado en Compose | obligatorio |
| Componentes y patrones | Ant Design Mobile replicado en Compose | obligatorio |
| Accesibilidad y movimiento | contrato de Compose + convenciones de plataforma | obligatorio |
| Kotlin/KMP | WaraWerse | referencia técnica |
| Compose | WaraWerse | referencia técnica |
| Build logic | WaraWerse | referencia técnica |
| Testing practices | WaraWerse | referencia técnica |

## Resolución de conflictos

1. Negocio: Agenda QR gana.
2. UX funcional: Agenda QR gana.
3. Visual, componentes, tokens, motion y accesibilidad: el contrato normativo Ant Design Mobile y los ADR aprobados ganan; las guías de implementación explican el cómo, no redefinen el qué.
4. Arquitectura técnica: WaraWerse es solo referencia.
5. Una implementación no puede introducir una variante visual local fuera de Ant Design Mobile.

## Regla de implementación

El código de producto combina reglas de negocio de Agenda QR con la referencia visual Ant Design Mobile. Una discrepancia visual se resuelve a favor de AgendaMobile. Cuando una pantalla necesite una composición nueva, se compone primero con componentes AgendaMobile existentes.
