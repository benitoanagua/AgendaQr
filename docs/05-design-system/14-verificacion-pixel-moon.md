# Verificación pixel Moon ↔ Aq — proceso para agentes

**Estado:** Procedimiento vigente (ADR-0048). **Resultado de referencia:**
[`evidencia/informe.md`](evidencia/informe.md) — 218 pares, 0 desviaciones.

Este documento describe cómo reproducir la evidencia pixel de fidelidad:
cómo se generan las capturas Moon y Aq, cómo se comparan y cómo se triagean
las desviaciones. Es un runbook operativo, no una especificación visual (esa
vive en el contrato y el catálogo).

## 1. Arquitectura del harness

Todo el andamiaje vive en `tools/moon-compare/` y está **gitignored**
(`tools/` ≈ 4 GB: SDK Flutter, paquete `moon_design` 1.1.0, venv de Python).
No es código del producto ni del gate; se instala una vez por máquina.

```
tools/moon-compare/
├── flutter/            # SDK Flutter local
├── moonref/            # fuentes moon_design 1.1.0 + moon_tokens 0.0.6
├── harness/            # test Flutter que renderiza los escenarios Moon
├── venv/               # Python del comparador
└── compare.py          # comparador caja/tinta/pixdiff/inkΔ
```

El lado Aq es código versionado:

- `core/ui/src/androidUnitTest/.../AqComponentReferenceTest.kt` — compone
  los mismos escenarios y escribe PNG (solo con `-Pagendaqr.capture=true`).
- `./gradlew :core:ui:captureComponents -Pagendaqr.capture=true
  -PallowDebugSigningForRc=true`

## 2. Procedimiento

1. **Goldens Moon**: ejecutar el harness Flutter
   (`flutter test --update-goldens` en `tools/moon-compare/harness/`).
   Escribe `goldens/*.png` + `moon-metricas.json` (caja, tinta, métricas
   por escenario).
2. **Capturas Aq**: `./gradlew :core:ui:captureComponents
   -Pagendaqr.capture=true -PallowDebugSigningForRc=true`. Escribe los PNG
   en el directorio de capturas del módulo.
3. **Comparar**: `tools/moon-compare/venv/bin/python compare.py` →
   regenera `docs/05-design-system/evidencia/informe.md`, `informe.json`
   y `pares/<id>/{moon,aq,diff}.png`.
4. **Triage**: para cada par `desviacion`, mirar `pares/<id>/diff.png` y
   clasificar (ver §4). Corregir en `core/ui` o en el escenario, nunca en
   el comparador para esconder un defecto real.
5. **Repetir** hasta 0 desviaciones; luego actualizar tests de contrato,
   gates y documentación (§5).

## 3. Modelo de comparación

Por par se comparan: caja del widget (ancho/alto en dp), bounding box de
«tinta» (píxeles que difieren del fondo por encima del umbral), porcentaje
de píxeles distintos y delta de tinta. Los umbrales son por familia de
componente; la fuente (Inter) difiere de Averta Std y eso está
sancionado — el comparador lo absorbe en el umbral de pixdiff.

## 4. Trampas conocidas (catálogo de depuración)

Cada entrada costó horas; verificar antes de investigar de nuevo.

### Captura

- **`Popup`/`Dialog` no rasterizan bajo Robolectric**: la ventana popup no
  se dibuja en la captura de raíz. Solución aplicada: extraer la superficie
  interna del overlay como composable `internal` reutilizable
  (`AqDropdownSurface`, `AqPopoverSurface`, etc.); el `Popup` la usa en
  producción y el escenario de captura la renderiza inline.
- **`captureToImage` de Compose cuelga bajo Robolectric**: no usarlo;
  captura por `captureRoboImage`/`captureScreen` según el caso.
- **`boundsInWindow` de nodos dentro de `Popup`** devuelve coordenadas de
  su propia ventana; usar `boundsInRoot()`/`localBoundingBoxOf` con la raíz
  correcta y recortar.
- **`testTag` después de `padding` excluye ese padding** de las bounds del
  nodo; si la evidencia necesita margen+tarjeta, el tag va antes del
  padding o en un `Box` que los incluya (toast, tooltip).
- **Goldens Moon con fondo transparente**: el comparador normaliza el
  fondo de página; la «tinta» incluye sombras, así que una sombra débil o
  ausente aparece como desviación de inkΔ.
- **Overlays Moon solo abren vía `RouteObserver`/`didUpdateWidget`**:
  montar los escenarios overlay en tests dedicados y drenar
  `takeException()` tras cada uno (quedan entries zombie al desmontar).

### Entorno de tests (Robolectric)

- **`@GraphicsMode(LEGACY)` tiene métricas de fuente degeneradas**: una
  línea de texto mide ~35 px y deforma alturas. Las aserciones de altura
  exacta que dependen de la fuente usan
  `@GraphicsMode(GraphicsMode.Mode.NATIVE)` (el test de captura ya lo hace).
- **`AqTheme` envuelve en `Surface(fillMaxSize)`**: propaga constraints
  mínimos a los hijos directos — un `Row(Modifier.height(16))` medido como
  hijo directo del tema llena la pantalla. Envolver el contenido en
  `Box(Modifier.wrapContentSize())` para medir el componente real.
- **Diálogos viven en ventana propia**: no sufren la propagación de
  constraints del tema, pero sí las métricas de fuente de LEGACY.

### Semántica

- **`mergeDescendants` anidado duplica propiedades**: dos ancestros que
  fusionan (p. ej. `clickable` del scrim + contenedor del diálogo) fusionan
  el mismo `PaneTitle` dos veces → crash. Poner `paneTitle` una sola vez,
  en el nodo que ya fusiona.
- **`clearAndSetSemantics { }` oculta el `Text` interno**: `onNodeWithText`
  deja de encontrar el texto; usar `onNodeWithContentDescription` (patrón de
  `AqTag`, cuyo texto se expone como CD).
- **El botón `AqButton` ES el objetivo táctil**: el texto visible lleva
  `clearAndSetSemantics`, el nodo `aq-button-touch-target` expone
  `text`+`contentDescription`+`Role.Button`. `onNodeWithText` lo encuentra
  una sola vez en árbol mergeado; en `useUnmergedTree` puede aparecer un
  segundo nodo con el mismo texto.
- **`BasicTextField` expone `text = accessibleName` + `EditableText`**:
  `onNodeWithText(label)` selecciona el campo editable;
  `onNodeWithContentDescription(label)` selecciona la caja visual (sin
  `SetText`/`RequestFocus`) y `performTextInput` falla.

### Geometría y layout

- **`weight(1f, fill = false)` no reserva espacio**: el hermano trailing
  queda pegado al texto en vez de ir al borde. Usar `weight(1f)` cuando el
  elemento debe empujar al borde (acordeón, toast).
- **Compose `Column` acota `maxHeight` a hijos no-weight**; Flutter les da
  altura no acotada. Si un componente tipo `AqAlert` (`mainAxisSize.max`
  de Moon) debe ajustarse a contenido en un slot de Column, medir el slot
  con `Constraints.Infinity` (`AqScreen.statusContent`).
- **`Modifier.shadow` no reproduce `BoxShadow` CSS** (blur/offset/spread/
  color): las sombras de Moon se dibujan con `BlurMaskFilter` por capas vía
  `aqShadowPlatform`/`aqPinShadowPlatform` (expect/actual; iOS conserva
  fallback de elevación, excepción E-21).
- **`AqProgressPin` con `thumbSize = null`**: Moon usa
  `thumbRadius = size.height/2` del contenido como overlay puro que no
  infla el layout; la sombra del pin+flecha debe dibujarse desde un hijo
  visible (el pin puede quedar fuera de pantalla y Android descarta el
  dibujo).
- **`Popup`/`Dialog` no se dibujan fuera de bounds** en Android: la sombra
  del pin se pinta con `drawOval`/`drawPath` manual por pasada (nada de
  `Path.addOval` + `nativeCanvas.drawPath`, que no rasteriza en
  Robolectric).

## 5. Reglas al cerrar un ciclo

1. **Tests de contrato a geometría Moon**: alturas visuales
   (botones 24/32/40/48/56 dp, acordeón, tags, links, icon buttons, inputs)
   sustituyen a la antigua geometría de 48 dp; el objetivo táctil ≥48 dp
   ya no se duplica dentro de la superficie visual Moon.
2. **NUNCA ajustar el comparador para esconder un defecto**: si un par
   diverge, primero decidir si es bug real, artefacto de captura o
   diferencia sancionada (Inter/Averta). Solo los dos últimos últimos se
   documentan.
3. **Gates**: `./gradlew verifyAgendaQrArchitecture` debe quedar verde
   (`verifyDocs`, `verifyMoonFidelity`, `verifyNoScripts`,
   `verifyScreenContract`, `verifyUxTransitionContracts`) y las suites
   `:core:ui:testDebugUnitTest` +
   `:feature:destinations:presentation:testDebugUnitTest` en verde.
4. **Commits atómicos** con la identidad del responsable (ver `AGENTS.md`),
   sin trailers de agente.
5. **Cuándo regenerar**: al cambiar la versión de `moon_design`/`moon_tokens`,
   al añadir componentes al catálogo, o cuando una corrección en `core/ui`
   pueda alterar píxeles de escenarios existentes.
