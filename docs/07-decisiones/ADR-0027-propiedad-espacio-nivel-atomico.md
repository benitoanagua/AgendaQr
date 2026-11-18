# ADR-0027 — Propiedad del espacio por nivel atómico

**Estado:** ACEPTADO — 2026-11-18
**Relaciona:** ADR-0025 (adopción de Polaris), ADR-0026 (cierre de la
migración), ADR-0007 (verificación de layout), ADR-0008 (guía de voz),
contrato normativo de diseño v8.3 e informe
[`11-auditoria-atomic-spacing.md`](../05-design-system/11-auditoria-atomic-spacing.md).

## 1. Decisión

Se adopta una regla única de **propiedad del espacio**: cada nivel de la
jerarquía atómica posee exactamente un tipo de espacio y ningún nivel invade
el del siguiente.

| Nivel | Posee | No posee |
|---|---|---|
| Átomo | Tamaño intrínseco, altura de control y, como máximo, el relleno interno de una píldora o etiqueta | Margen externo ni separación entre hermanos |
| Molécula | Separación y alineación entre sus átomos; altura de fila de campo | Margen de sección, fondo, borde o radio de tarjeta |
| Organismo | Relleno de sección/tarjeta (16 dp), separación interna de moléculas, fondo, borde, radio y sombra | Margen de pantalla ni insets del sistema |
| Plantilla | Margen de pantalla (16 dp), separación entre organismos, insets, ancho máximo y slots de navegación | Relleno interno de tarjetas o campos |
| Página | Composición de plantillas y organismos, copy y estado | Cualquier número de espaciado suelto |

Reglas operativas:

1. **Un solo dueño por espacio.** El margen de pantalla se aplica una sola
   vez: o lo pone la plantilla, o lo pone el host edge-to-edge. Un componente
   no acepta un parámetro para pedirlo.
2. **Pasos canónicos.** Relleno de sección/tarjeta: 16 dp (`AqSpacing.Lg`).
   Densidad vertical de fila: 12 dp (`AqSpacing.MdPlus`). Separación mínima
   entre átomos: 4 dp (`AqSpacing.Sm`). Margen de pantalla: 16 dp.
3. **Los mensajes pertenecen al campo.** Ayuda y error se separan 4 dp del
   control y se alinean a su borde izquierdo; el grupo de opciones pinta el
   mensaje una sola vez.
4. **Cero literales.** Ningún valor visual fuera de `AqTokens.kt`, incluido
   `0.dp`, y ningún `padding`, `PaddingValues` o `spacedBy` con números
   sueltos.
5. **Objetivo táctil intacto.** Las alturas son mínimas (`defaultMinSize`),
   no fijas: el contenido crece con la escala de fuente hasta 200 % sin
   recorte.

## 2. Alcance

Se aplica a todo `core/ui` y a `feature/destinations/presentation`. No cambia
el contrato funcional, el dominio, la sincronización ni el copy. Las páginas
solo componen; los valores siguen viviendo en la capa de tokens.

La auditoría completa, el inventario por nivel y la tabla de hallazgos están
en [`11-auditoria-atomic-spacing.md`](../05-design-system/11-auditoria-atomic-spacing.md).

## 3. Desviaciones aceptadas

| ID | Desviación | Motivo |
|---|---|---|
| D-12 | Las agrupaciones de tarjetas de página usan un ritmo de 16 dp, mientras `light-mobile` publica `space-card-gap` de 8 dp (`09-instantanea…:511`) | Con 16 dp de relleno interno, un hueco de 8 dp entre tarjetas lee como un solo bloque; 16 dp conserva la separación de secciones a 360 dp y coincide con el ritmo de margen de pantalla. `AqScreenScaffold` aplica ese mismo paso de 16 dp por defecto entre organismos, de modo que la página no tenga que repetirlo |

Desviaciones ya registradas que esta decisión no altera: D-01 (`1rem` =
16 dp), D-02 (targets ≥ 48 dp), D-06 (escala hasta 200 %), D-10 (sombras) y
D-11 (contenedores tonales oscuros). Las extensiones X-01…X-07 y las
preguntas PV-01…PV-08 siguen vigentes.

Hallazgos aceptados con seguimiento (detalle en el informe, §6):

- El generador de QR de la capa de datos usa negro/blanco puros fuera de
  tokens porque el mapa de bits debe ser escaneable en cualquier tema (X-03).
- `AqQrPreview` conserva una descripción `"QR"` fija por compatibilidad con
  su contrato actual; el copy debería llegar del llamador según ADR-0008.
- Los bloques de estado (`AqEmptyState`, `AqLoading`, `AqSkeleton`,
  `AqLoadMoreFooter`) conservan su relleno de sección como organismos.
- `AqIconButton`, `AqFavoriteToggle` y `AqProgressBar` imponen target/ancho
  contractuales sobre el modifier del llamador.

## 4. Verificación

- **Contratos por nivel.** `AqAtomicSpacingContractTest` mide banner sin
  margen propio, margen del host, tarjeta 16 dp, fila 12 dp, dock con margen
  de pantalla, error del importe dentro del campo y que el contenido
  expandido quede limitado a `ContentMaxWidth` y centrado.
  `AqUiPolishContractTest` añade la alineación de etiqueta/valor/ayuda/error,
  48 dp de campo, 16 dp de tarjeta y banner, fila de 12 dp, opciones sin
  margen externo y el relleno/borde del botón secundario.
- **Contraste.** `AqContrastPairsTest` cubre etiqueta/ayuda/error sobre
  `surface` e `inputSurface` en ambos temas con AA (4.5:1), además de foco y
  borde funcional (3:1).
- **Gate estático.** `verifyDesignSystemCompliance` prohíbe literales `.dp`,
  `.sp`, `Color(...)` y hex fuera de `AqTokens.kt`, y ahora también
  `padding(número)`, `PaddingValues(número)` y `spacedBy(número)`.
  `verifyDesignSystemFixtures` prueba ambos lados de cada regla.
- **Capturas.** `captureGallery` regenera 31 capturas claras y 31 oscuras a
  360 dp; `verifyScreenContract` verifica las 19 superficies y las 62
  capturas contra el manifiesto.
- **Pruebas.** `:core:ui:testDebugUnitTest` y
  `:feature:destinations:presentation:testDebugUnitTest` pasan.
- **No verificado.** iOS y el checklist manual en dispositivo siguen fuera
  del alcance de este entorno (ADR-0007, ADR-0026). El escalado al 200 % se
  conserva en producción y queda como revisión manual (D-06).

## 5. Consecuencias

- El espaciado deja de ser una decisión local de cada pantalla: se puede
  auditar por nivel y el gate detecta la reaparición de números sueltos.
- Un componente nuevo debe declarar su nivel y consumir solo los tokens que
  le corresponden; si necesita un espacio externo, lo pide al dueño del
  nivel superior.
- Los componentes que hoy aplican insets o márgenes de plantilla
  (`AqTopBar`, `AqNavBar`, `AqNavRail`, `AqHeroHeader`) quedan documentados
  como borde de plantilla y no como deuda.
- Cualquier futura desviación de densidad se registra en el contrato y en
  este ADR antes de consumirse, siguiendo ADR-0025.
