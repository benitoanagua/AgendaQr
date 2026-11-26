# ADR-0048 — Evidencia pixel de fidelidad Moon: comparador, goldens y sombras nativas

**Estado:** ACEPTADO — 2026-11-25  
**Relacionado:** ADR-0044, ADR-0046, ADR-0047.

## 1. Contexto

ADR-0047 cerró la fidelidad Moon con una auditoría de código y contratos:
la matriz `auditoria-fidelidad-moon.md` quedó en 0 desviaciones, pero la
verificación era estática (geometría, tokens, tests de contrato). El ciclo
siguiente exigía evidencia a nivel píxel: capturas reales de los componentes
`Aq*` comparadas contra los widgets `moon_design` 1.1.0 renderizados por
Flutter, con umbrales objetivos.

La comparación pixel a pixel destapó divergencias que la auditoría estática
no veía: sombras de elevación débiles o ausentes en software, posicionamiento
de overlays distinto del `MoonAnchoredPopup`, geometría de `AqProgressPin`
con `thumbSize = null`, padding de celdas de `AqTable`, semántica duplicada
de `paneTitle` en overlays anidados y métricas de fuente degeneradas en
Robolectric `LEGACY`.

## 2. Decisión

1. **Harness Moon**: los escenarios de `moon_design` 1.1.0 se renderizan con
   Flutter (`flutter_test` + `matchesGoldenFile`) en `tools/moon-compare/`,
   directorio de trabajo local **no versionado** (~4 GB de SDK y venv). Los
   goldens y `moon-metricas.json` son la referencia.
2. **Captura Aq**: `AqComponentReferenceTest` (`core:ui` androidUnitTest,
   `@GraphicsMode(NATIVE)`) compone los mismos 218 escenarios y escribe los
   PNG. Los overlays (`AqDropdown`, `AqPopover`, `AqModal`, `AqDrawer`,
   `AqBottomSheet`, `AqToast`, `AqTooltip`) exponen su superficie interna
   como composable `internal` reutilizable para captura inline, porque las
   ventanas `Popup`/`Dialog` de Compose no rasterizan de forma fiable bajo
   Robolectric.
3. **Comparador**: `tools/moon-compare/compare.py` compara caja, tinta,
   pixdiff e inkΔ por par con umbrales por familia; emite
   `docs/05-design-system/evidencia/informe.md`, `informe.json` y `pares/`
   (artefactos versionados). Estado final del ciclo: **218 pares, 0
   desviaciones, 0 solo-Moon, 0 solo-Aq**.
4. **Sombras nativas**: `Modifier.shadow` de Compose no reproduce la
   geometría `BoxShadow` de Moon (blur, offset, spread, color). Se añade
   `aqShadow`/`aqShadowPlatform` (`AqShadowPlatform` expect/actual:
   `BlurMaskFilter` por capas en Android; fallback de elevación en iOS,
   excepción E-21 del gate) y `aqPinShadowPlatform` para la sombra del
   pin+flecha de `AqProgressPin`, que debe dibujarse fuera de los bounds del
   layout.
5. **Posicionamiento anclado compartido**: `AqAnchoredPopup` centraliza la
   fórmula de `MoonAnchoredPopup` (ancho máximo por distancia al ancla,
   posición preferida, flip) usada por `AqDropdown` y `AqPopover`; el centro
   X del ancla pasa a ser estado observable para recalcular el ancho.
6. **Tests de contrato actualizados a Moon**: las alturas visuales de
   controles (`AqButton` xs…xl = 24/32/40/48/56 dp, acordeón, tags, links,
   icon buttons, inputs) sustituyen a las antiguas 48 dp de layout; las
   aserciones de altura exacta que dependen de métricas de fuente corren con
   `@GraphicsMode(NATIVE)` y contenido envuelto en `Box(wrapContentSize)`
   porque `AqTheme` (`Surface(fillMaxSize)`) propaga constraints mínimos y
   el modo `LEGACY` de Robolectric tiene métricas de fuente degeneradas.

## 3. Consecuencias

- La fidelidad visual queda declarada con evidencia reproducible y
  versionada (`docs/05-design-system/evidencia/`); la matriz de auditoría
  (ADR-0047) se mantiene como registro de la especificación por widget.
- `tools/moon-compare` no es código del producto ni del gate: es andamiaje
  local. `verifyMoonFidelity` sigue siendo el gate ejecutable; la evidencia
  pixel se regenera manualmente al cambiar la versión de `moon_design` o al
  añadir componentes al catálogo.
- La sombra nativa es una adaptación de plataforma (D-10 ampliado): la
  geometría de capas sigue siendo la de `MoonShadows`; solo cambia el
  rasterizador. iOS conserva el fallback de elevación hasta tener el
  pintor equivalente.
- Los tests de `core:ui` que afirmaban geometría pre-Moon se reescribieron
  contra la especificación Moon; `AqTextInput` deshabilitado expone semántica
  `disabled()` y `paneTitle` vive una sola vez por overlay.
