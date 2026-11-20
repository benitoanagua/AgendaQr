# ADR-0028 — Moon Design System como referencia de patrones móviles (Polaris primario)

**Estado:** ACEPTADO — 2026-11-19  
**Relaciona:** ADR-0025, ADR-0027, contrato normativo de diseño,  
[`11-matriz-gaps-mobile.md`](../05-design-system/11-matriz-gaps-mobile.md),  
especificación UX/UI V1 congelada.

## 1. Decisión

Agenda QR **conserva Polaris** (reimplementación Kotlin/Compose, tokens
`@shopify/polaris-tokens@9.4.2`, iconografía oficial) como **única autoridad
visual**: color, tipografía, radios, elevación, jerarquía y tokens semánticos.

Se adopta **Moon Design System** ([flutter.moon.io](https://flutter.moon.io/),
paquete `moon_design` Flutter) **solo como referencia de patrones de
interacción móvil** para cubrir gaps donde Polaris (sistema web/admin) no
especifica comportamientos nativos de teléfono.

**No se introduce dependencia Flutter ni runtime Moon.** No se copian tokens,
colores ni tipografía de Moon. La API pública permanece `Aq*`.

## 2. Motivación

Polaris orienta experiencias de admin en escritorio. Agenda QR es
local-first en Android e iOS. La matriz de cobertura ya clasificó los
patrones necesarios; varios estaban COMPLETE en `core/ui` pero aún no
aplicados de forma uniforme en las pantallas (p. ej. `AqExpansionPanel` en
S07, filtros por tipo en S04).

Moon aporta ejemplos maduros de:

- accordion / panel expandible con encabezado interactivo único;
- chips de filtro en listas heterogéneas;
- bottom sheets y action sheets densos;
- entrada de códigos / PIN con feedback de error;
- densidad táctil y floating labels en formularios.

## 3. Reglas

1. Todo valor visual sale de tokens Polaris (contrato normativo vigente).
2. Un patrón Moon se porta solo si hay caso de producto en UX/UI V1 o
   extensión registrada (X-*).
3. El portado se implementa en `core/ui` como `Aq*` y se consume en
   `feature/.../presentation`.
4. Las desviaciones o extensiones se documentan en el contrato y, si aplica,
   en la matriz de cobertura.
5. Navegación raíz sigue convenciones nativas Android/iOS (no drawer Moon
   por defecto).

## 4. Aplicación a pantallas (prioridad)

| Pantalla | Patrón | Acción |
|---|---|---|
| S07 Registrar | Accordion "Más detalles" | **Hecho** — `AqExpansionPanel` |
| S04 Buscar | Chips de tipo con resultados mixtos | **Hecho** — `AqChipGroup` |
| Detalle destino | Contenido QR expandible | Conservar U-12/D9 (3 líneas + Ver más) |
| S00 Acceso local | Feedback PIN en puntos | **Hecho** — `AqPinDots(error=)` |
| S12 Importación | Filtros en revisión de pendientes | **Hecho** — chips Todos/Duplicados/Sin clasificar |
| S02 Añadir | Lista Galería→Cámara→PDF | **Conservado** (V1.17: galería primaria; sheet no aplica con 3 fuentes) |
| S01 | Carrusel favoritos | Ya `AqCarousel` |

## 5. Consecuencias

- Polaris sigue siendo el contrato visual verificable.
- Moon no aparece en dependencias Gradle ni en UI de producto.
- Las pruebas de contrato/layout/a11y existentes siguen siendo la puerta de
  calidad; los cambios de pantalla se validan con la suite de presentation.

## 6. No hacer

- Dependencia de `moon_design` o assets Moon.
- Sustituir Inter / tokens Polaris por tipografía o escala Moon.
- Bottom sheet para selectores de fecha/contexto en S07 (D-07: `AqDialog`).
