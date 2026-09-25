# Agenda QR — Documentación

Esta carpeta es la referencia normativa del producto y de la implementación. La especificación funcional V1 aprobada y el contrato UX/UI V1 congelado son las referencias para el comportamiento.

## Fuente de verdad

1. **Agenda QR**: producto, dominio, requisitos, alcance y decisiones aprobadas.
2. **UX/UI V1 de Agenda QR**: comportamiento de interacción congelado en `docs/04-ux/02-especificacion-ux-ui-v1.md`.
3. **Xauxa Design System + Xauxa**: UI/UX visual, tokens, componentes y referencia técnica para lectura/clasificación de QR.
4. **WaraWerse**: referencia técnica para Kotlin Multiplatform, modularización, Gradle y patrones de implementación.

Una referencia técnica no puede introducir reglas de negocio que no estén aprobadas para Agenda QR.

## Mapa

| Sección | Contenido |
|---|---|
| `01-producto` | propósito, alcance, límites y declaración V1 |
| `02-requisitos` | requisitos funcionales y no funcionales |
| `03-dominio` | QR, destino, operación, comprobante y relaciones |
| `04-ux` | flujos y especificación UX/UI congelada |
| `05-design-system` | contrato visual Xauxa aplicado al producto |
| `06-arquitectura` | estructura técnica, fuentes y Kotlin Way |
| `07-decisiones` | decisiones de producto aprobadas |
| `08-validacion` | escenarios, simulaciones, estados, eventos e invariantes |
| `09-implementacion` | estado y trazabilidad de la implementación |
| `10-fuera-de-alcance` | capacidades excluidas de V1 |

## Documentos de referencia inmediata

- UX/UI congelada: `04-ux/02-especificacion-ux-ui-v1.md`
- Flujos resumidos: `04-ux/01-flujos-confirmados.md`
- Suite de estados/eventos: `08-validacion/02-suite-estados-eventos-v1.md`
- Requisitos funcionales: `02-requisitos/01-requisitos-funcionales.md`
- Decisiones aprobadas: `07-decisiones/01-decisiones-aprobadas.md`
- Fuentes de verdad: `06-arquitectura/01-matriz-de-fuentes.md`

## Regla documental

Una simulación, hipótesis, implementación histórica o conveniencia técnica no se convierte en requisito sin una decisión explícita.

La implementación debe reflejar el dominio V1 aprobado y el contrato UX/UI congelado. Si existe una contradicción, se documenta y resuelve antes de modificar una fuente de verdad.
