# ADR-0043 — Geometría Moon y contrato de layout

**Estado:** ACEPTADO — vigente  
**Relacionado:** ADR-0044, ADR-0046.

## Decisión

1. La geometría de cada componente Aq* reproduce la versión Moon registrada en el contrato normativo y el catálogo: alto visual, ancho intrínseco, padding, gap, radio, borde, icono y estados. Un token de composición de Agenda QR no puede modificar estas medidas.
2. AqLayout centraliza la composición de pantallas. BetweenOrganisms (16 dp) y DockVertical (12 dp) son decisiones de layout de producto, no tokens de anatomía Moon. Se aplican en el nivel de pantalla y quedan subordinadas a cualquier patrón de composición explícito de Moon.
3. AqButton.fullWidth equivale a MoonButton.isFullWidth: al activarse llena el ancho disponible del padre; al desactivarse mantiene ancho intrínseco. El alto siempre depende del tamaño Moon seleccionado.
4. AqTextInput y AqSelect llenan el ancho disponible sin imponer un alto visual mínimo de 48 dp. Los tamaños equivalentes conservan la altura Moon (por ejemplo, md = 40 dp). El hit target de 48 dp se añade mediante AqTouchTarget, fuera de la caja visual.
5. Los paneles States/Variants de la galería son flexibles y adaptables al contenido: wrap cuando sea necesario, sin ancho fijo que corte estados, variantes o labels.
6. verifyLayoutGeometry y AqLayoutGeometryTest deben proteger estas invariantes junto con la ausencia de ancho fijo en AqQuickAction y de weight dentro de horizontalScroll en AqTabBar/AqPillTab.

## Criterios de aceptación

- El botón full-width ocupa exactamente el espacio disponible del padre sin desbordarse; el modo intrínseco no se estira.
- Botón, input y select conservan sus alturas visuales Moon; la accesibilidad táctil no altera la apariencia.
- Estados y variantes de la galería se muestran completos en el ancho de referencia y en tamaños de fuente accesibles.
- La geometría interna de componentes no cambia al variar AqLayout de composición.

Este ADR define el contrato vigente de geometría y validación. No conserva el registro de síntomas, auditorías de pantalla ni notas de migración; esos apuntes históricos no son especificaciones y no deben reintroducirse como autoridad visual.
