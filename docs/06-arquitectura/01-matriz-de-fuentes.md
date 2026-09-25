# Matriz de fuentes de verdad

| Área | Fuente de verdad | Uso |
|---|---|---|
| Producto | Agenda QR | obligatorio |
| Dominio | Agenda QR | obligatorio |
| Requisitos | Agenda QR | obligatorio |
| Alcance y límites | Agenda QR | obligatorio |
| Decisiones | Agenda QR | obligatorio |
| UX funcional | Agenda QR | obligatorio |
| UX/UI congelada | Agenda QR + Xauxa Design System | obligatorio |
| UI/UX visual | Xauxa Design System + Xauxa | obligatorio |
| Tokens | Xauxa Design System | obligatorio |
| Componentes/patterns | Xauxa Design System + Xauxa | obligatorio |
| Kotlin/KMP | WaraWerse | referencia técnica |
| Compose | WaraWerse | referencia técnica |
| Build logic | WaraWerse | referencia técnica |
| Testing practices | WaraWerse | referencia técnica |

## Resolución de conflictos

1. Negocio: Agenda QR gana.
2. UX funcional: Agenda QR gana.
3. UX/UI visual: Xauxa Design System gana sobre implementaciones históricas.
4. Arquitectura técnica: se reutiliza de WaraWerse solamente cuando no contradice las reglas anteriores.
5. No se copian dominios de WaraWerse a Agenda QR.

La especificación UX/UI congelada se valida contra el Design System; no crea un sistema visual alternativo.
