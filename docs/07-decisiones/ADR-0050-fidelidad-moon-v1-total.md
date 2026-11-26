# ADR-0050 — Fidelidad Moon v1 generada desde el JSON + cierre total de gaps

**Estado:** ACEPTADO — 2026-11-26  
**Relacionado:** ADR-0007, ADR-0044, ADR-0046, ADR-0047, ADR-0048,
ADR-0049.

## 1. Contexto

La pasada anterior (ADR-0049) verificó los componentes Moon uno a uno contra
`moon-v1-tokens-y-componentes.json`, pero la cobertura era manual y parcial:
no existía una prueba que midiera lo renderizado contra la especificación
cruda, ni un gate que exigiera cobertura para todo el registro. Además el
Component Lab tenía rutas fijas de máquina en el toolchain wasmJs, la CI no
compilaba wasmJs y quedaban variantes del spec sin soporte en `Aq*`
(etiqueta `Input Label`, icono derecho en Button/Tag, alturas del progress
bar, estados del empty state, flecha atrás del app bar, variantes del
switch).

## 2. Decisión

1. **Fixture generado, no transcrito.** La tarea Gradle
   `generateMoonSpecFixtures` (build-logic, `@CacheableTask`) lee
   `docs/05-design-system/evidencia/figma/moon-v1-tokens-y-componentes.json`
   y emite `core/ui/build/generated/moonSpec/kotlin/MoonSpecFixtures.kt`
   con una fila canónica por familia+tamaño (84 filas): ancho, alto, radio,
   padding, gap, borde y texto en reposo. El generador elige la variante
   «menos compleja» de cada grupo y rellena radio/borde ausentes en el JSON
   desde hermanos de la misma firma de variante (el extractor omitía esos
   campos en algunas filas).
2. **Dos pruebas de fidelidad.** `MoonSpecFidelityTest` compara los tokens
   que consume cada componente (`MoonSpecActuals.probe`) contra el fixture;
   `MoonSpecRenderedTest` compone cada `Aq*` con `testTag` y mide bounds
   reales (tolerancia 0 dp en geometría). Las métricas de fuente de
   Robolectric inflan el texto, así que las aserciones de altura de
   componentes con texto usan la caja visual (`aq-*-visual`) o relaciones
   relativas, no el texto.
3. **Gate `verifyMoonFidelityCoverage`.** Todo componente público del
   registro debe tener familia en el fixture o una excepción `E-nn`
   justificada en la tabla «Excepciones de cobertura de fidelidad Moon v1»
   del contrato normativo (E-24…E-30: 46 componentes — primitivas, chrome
   de producto, dominio QR/PIN, media y patrones sin familia en el JSON).
   El propio gate lleva fixtures de prueba (`verifyMoonFidelityCoverageFixtures`).
4. **Regla `Input Label` (B3).** En Moon v1 la etiqueta flotante dentro de
   la caja solo existe en tamaño **xl** (56 dp). En sm/md/lg la etiqueta es
   un texto separado encima del campo (variante `Input Label` del JSON:
   14/24 en sm, 16/24 en md/lg, separación 4/8). `AqTextInput` y `AqSelect`
   (y por delegación `AqCombobox`) implementan la regla; la etiqueta superior
   es visual (`clearAndSetSemantics`) porque el nombre accesible ya lo porta
   el `contentDescription` del campo — misma convención que la etiqueta
   flotante. Los campos de formulario de «Editar destino» quedan en md con
   etiqueta superior, conforme al spec.
5. **Textarea y contador (B4).** `AqTextArea` toma la altura base del spec
   (132 dp, `AqLayout.TextAreaHeight`); la pantalla «Editar destino» deja de
   fijar 64 dp. El contador `n / máx` queda alineado a la derecha con 4 dp
   de separación y la tipografía secundaria del sistema (12/16
   `TextSecondary`), en `AqTextInput` y `AqTextArea`.
6. **Chrome de pantalla (B5).** La flecha atrás del `AqAppBar` deja de ser
   un `AqButton` Text (icono de 20 y tinte `link`): ahora es un glifo de 24
   (`AqMetrics.IconSize`) con tinte `TextPrimary` y zona táctil de 48 dp.
   La barra inferior de acción conserva `AqLayout.DockVertical` (12 dp) y
   margen de pantalla 16: son tokens de composición documentados (D-12), no
   anatomía Moon — el JSON no define un componente «bottom action bar».
7. **Switch (B2).** Geometría verificada contra el JSON y `moon_flutter`:
   pistas 32/44/60, pulgares 12/16/24 con relleno 2/4/4, sombra `sm`,
   etiqueta 14/24 Regular con gap 8, transición 200 ms y objetivo táctil
   ≥ 48 dp sin deformar la pista (la pista queda centrada dentro del nodo).
   Se añade `labelFirst` para la fila de ajuste convencional (texto a la
   izquierda, control a la derecha) y se aplica en «Editar destino».
   Colores: la pista apagada usa `AqColor.Border` (`beerus`; `#292929` en
   oscuro según la tabla normativa de tokens — el `#444444` del archivo
   Figma v1 no prevalece sobre el contrato) y la encendida
   `AqColor.BrandFill` (`piccolo`).
8. **Variantes Moon implementadas (B7).** `AqButton.iconAtEnd` (`Icon=Right`),
   `AqTag.trailingIcon` (`Icon=Right`/`Both`), `AqProgressBar.height`
   (lineal 2/4/8/12/16 dp; el pin es `AqProgressPin`),
   `AqEmptyPlaceholder.secondaryActionLabel` (`Multi button`) e
   `illustration` (`Simple+ Illu`). Todas registradas en el lab. El tooltip
   gana el borde `beerus` de 1 px del spec (excepción `E-31` en la tabla de
   bordes del contrato).
9. **Portabilidad del toolchain wasmJs (A1).** `agendaqr.wasm-app.gradle.kts`
   aplica los overrides de rutas (`agendaqr.wasm.yarn`,
   `agendaqr.wasm.binaryen`, `agendaqr.wasm.nodeDownload`, y equivalentes por
   variable de entorno) solo si la propiedad/entorno está definida y el
   archivo existe; si no, queda el comportamiento por defecto del plugin.
   La reflexión `setAccessible` quedó aislada en una función interna con
   comentario (no hay API pública equivalente en Kotlin 2.2; si Kotlin 2.3
   la retira, solo hay que ajustar ese punto). Documentado en
   `15-component-lab.md` (propiedades en `~/.gradle/gradle.properties`,
   nunca en el repo).
10. **CI.** `android-validation.yml` compila `:core:ui:compileKotlinWasmJs`
    y `:core:ui-lab:wasmJsBrowserDistribution` con `actions/setup-node`.

## 3. Alternativas descartadas

- **Verificación solo pixel (Robolectric + capturas)**: las capturas no
  detectan desviaciones de padding/gap de 4 dp de forma fiable ni son
  comparables a nivel de token; el fixture JSON + medición de bounds da una
  guarda exacta y regenera sola cuando cambia la spec.
- **`hasFloatingLabel` como booleano suelto por tamaño**: se mantuvo el
  parámetro (compatibilidad) pero su efecto quedó limitado a xl; la regla
  vive en el componente, no en cada pantalla.
- **Cambiar `darkBeerus` global a `#444444`**: afectaría todos los bordes y
  divisores; el valor `#292929` está en la tabla normativa de tokens
  (`09-tokens-moon.md`) y prevalece sobre el archivo Figma v1.

## 4. Consecuencias

- `verifyAgendaQrArchitecture` incluye `verifyMoonFidelityCoverage` (+
  fixtures): un componente nuevo del registro sin familia Moon ni excepción
  `E-nn` rompe el gate.
- `MoonSpecFixtures.kt` es código generado en `build/`; nunca se edita a
  mano ni se versiona.
- Los tests de accesibilidad de campos siguen pasando porque la etiqueta
  superior no duplica semántica (el campo ya se anuncia por
  `contentDescription`).
- `AqSelect` sale de `legacySymbols`: el nombre quedó reasignado al campo de
  selección Moon v1 (el `AqSelect` del sistema de diseño anterior se retiró en 5ef8693).
  La fidelidad la garantiza el fixture JSON + el registro, no la lista de
  símbolos.
- `verifyDesignSystemCompliance` solo lee la tabla «Registro de excepciones
  del gate» del contrato: la tabla de cobertura E-24…E-30 comparte el
  prefijo `E-nn` con otro significado y no debe confundirse con excepciones
  de API.
- La galería de capturas (`captureGallery`) se regenera con
  `-Pagendaqr.capture=true`; las evidencias pixel de ADR-0048 siguen siendo
  la referencia visual complementaria.
