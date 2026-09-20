# Agenda QR — Documentación

Esta carpeta es la referencia normativa del producto y de la implementación. La especificación funcional V1 aprobada es la fuente de verdad para el comportamiento de negocio.

## Fuente de verdad

1. **Agenda QR**: producto, dominio, requisitos, alcance y decisiones aprobadas.
2. **Xauxa Design System + XauxaXcan**: UI/UX, tokens, componentes y referencia técnica para lectura/clasificación de QR.
3. **WaraWerse**: referencia técnica para Kotlin Multiplatform, modularización, Gradle y patrones de implementación.

Una referencia técnica no puede introducir reglas de negocio que no estén aprobadas para Agenda QR.

## Mapa

| Sección | Contenido |
|---|---|
| `01-producto` | propósito, alcance, límites y declaración V1 |
| `02-requisitos` | requisitos funcionales y no funcionales |
| `03-dominio` | QR, destino, operación, comprobante y relaciones |
| `04-ux` | flujos y comportamientos funcionales; la UI visual permanece bajo Xauxa |
| `05-design-system` | contrato visual Xauxa aplicado al producto |
| `06-arquitectura` | estructura técnica, Kotlin Way y hosts |
| `07-decisiones` | decisiones de producto aprobadas |
| `08-validacion` | escenarios, simulaciones y criterios de validación |
| `09-implementacion` | estado y trazabilidad de la implementación |
| `10-fuera-de-alcance` | capacidades excluidas de V1 |

## Regla documental

Una simulación, hipótesis, implementación histórica o conveniencia técnica no se convierte en requisito sin una decisión explícita.

La implementación debe reflejar el dominio V1 aprobado: destinos QR reutilizables, operaciones PAGO/COBRO y comprobantes como respaldo de operaciones.
