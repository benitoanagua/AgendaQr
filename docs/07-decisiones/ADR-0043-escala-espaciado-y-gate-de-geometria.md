# ADR-0043 — Escala de espaciado, gate de geometría y corrección de la geometría Moon

**Estado:** ACEPTADO — 2026-11-22  
**Relacionado:** ADR-0044, D-12.

## Decisión

1. **Nueva escala de espaciado única `AqLayout`** en `AqTokens.kt`, que concentra los huecos y rellenos de composición expresados exclusivamente con tokens numéricos existentes (`AqSpacing` / `AqSize`), sin literales propios salvo `AqLayout.Zero` (para inicializar operaciones de `Dp`) y los dos valores de relleno vertical de campo que no pertenecen a la escala `AqSize` (`InputPaddingVerticalLg` 6 dp y `InputPaddingVerticalXl` 9 dp, declarados como tokens del sistema). Los valores son:
   - `ScreenMargin` 16 (existe en `AqSpacing`)
   - `BetweenOrganisms` 16 (`AqSpacing.Lg`) — D-12 vigente: hueco entre organismos de una pantalla.
   - `InGroup` 8 (`AqSize.X4s`) — hueco entre elementos de un mismo grupo/celda.
   - `CellPadding` 12 (`AqSize.X3s`) — relleno interno de celda en los 4 lados.
   - `CardPadding` 16 (`AqSize.X2s`) — relleno interno de tarjeta en los 4 lados.
   - `LabelToField` 8 (`AqSize.X4s`) — hueco etiqueta persistente → caja.
   - `DockVertical` 12 (`AqSize.X3s`) — relleno vertical del dock inferior.
   - `InputPaddingVertical*` (xs/sm 2, md 4, lg 6, xl 9) y `InputPaddingHorizontal*` (xs/sm 8, md/lg 12, xl 16) por tamaño de campo (Moon § TextInput).

2. **Regla de huecos: nunca `AqSize` como hueco genérico.** Un hueco entre hermanos usa siempre el valor numérico Moon de la tabla expresado con su token equivalente (p. ej. hueco de input 8 dp = `AqSize.X4s`, hueco botón xs/sm 4 = `AqSize.X5s`, md 8 = `AqSize.X4s`, lg 12 = `AqSize.X3s`, xl 16 = `AqSize.X2s`). `AqSize` es la escala de tamaños Moon (alturas, iconos, radios), no una escala genérica de espaciado. Quedan prohibidos los literales visuales sueltos (ver gate `verifyDesignSystemCompliance` y `AqLayout.Zero` como única excepción de inicialización).

3. **Gate `verifyLayoutGeometry`** registrado en `build-logic/.../agendaqr.design-system.gradle.kts`, dependiente de la suite `AqLayoutGeometryTest`, conectado a `check` y a `verifyDesignSystemCompliance`, siguiendo el patrón de las tareas existentes. Rechaza:
   - `AqQuickAction` con ancho fijo (`.width(AqMetrics.GridIconContainerSize)`).
   - `AqTabBar` / `AqPillTab` con `Modifier.weight` dentro de `horizontalScroll`.

4. **Ampliación de `verifyDesignSystemCompliance`:** prohíbe `androidx.compose.material3.Text` y cualquier import de Material en `core/ui` fuera del adaptador permitido (la capa de tokens y los componentes que adaptan Material conservan sus excepciones registradas E-xx; no se creó ninguna excepción nueva).

5. **Decisiones aplicadas (D1–D7):**
   - D1: Añadir destino — primera acción según el documento (`Elegir de la galería`) se queda `AqButtonVariant.Primary`; la segunda (`Cámara`) pasa a `Outlined`.
   - D2: Barra inferior — `AqNavDestination.label` ya existe; las pantallas raíz no exigen etiquetas visibles adicionales, no se toca.
   - D3: `backLabel` + título que no caben a 360 dp — el título cede ancho primero (`maxLines = 1`, ellipsis); si aún no cabe, `backLabel` colapsa a solo icono con `contentDescription`. Sin cambiar copy.
   - D4: Etiqueta duplicada — sección con un solo campo elimina el encabezado de sección redundante y deja la etiqueta del campo (aplicado en `DestinationEditorScreen`: `LabelNombre` / `LabelNota` / `Contexto` / `Favorito` ya no usan `AqSectionTitle`).
   - D5: Errores de validación de Inicio de sesión — pasan a `AqTextInput(isError, errorMessage)` con los textos actuales; las `AqAlert` apiladas se eliminan.
   - D6: S9 (subtítulo «Muéstralo») — documentado como base64 de una cabecera PNG (`iVBORw0KGgoAAA…`) en `QrDisplayEffects`/`destination detail`; se reporta como causa de producción (mapeo del modelo de destino QR), no se inventa texto.
   - D7: Filtro «CONTEXT» — `AppStrings` no contiene ese literal mayúscula; el forzado `type.name` en `GlobalSearchScreen.typeDisplayName` se quita y se resuelve a `AppStrings.TipoContexto` / `TipoQr` / `TipoActividad` / `TipoComprobante`.

6. **Correcciones de síntomas (causa raíz en core/ui):**
   - S1: `AqQuickAction` eliminó `.width(48 dp)` y `padding(2 dp)`; la celda acepta el ancho del padre (llamador pasa `weight(1f)`), etiqueta 2 líneas centrada (`maxLines = 2`, `TextAlign.Center`), relleno vertical `CellPadding`/`horizontal InGroup`, icono 24 dp (`AqSize.Xs`), compuesto con `Modifier.clickable(role = Button)` + `AqSquircles` sin `AqButton` (no soporta multilínea).
   - S2: `AqTabBar` / `AqPillTab` usan `BoxWithConstraints`; si el ancho intrínseco total cabe reparten con `weight(1f)`, si no usan ancho intrínseco + `horizontalScroll` (nunca `weight` dentro de `horizontalScroll`). Alto 32/40, padding horizontal 12/16, hueco X5s/X4s, etiqueta `maxLines = 1`. ImportBatch y ContextScreen dejan de truncar.
   - S3: `AqScreen` recibe `contentSpacing = AqLayout.BetweenOrganisms` aplicado con `Arrangement.spacedBy` al Column de contenido; dock inferior con `DockVertical`, padding horizontal `ScreenMargin`, sin el `Row` + `Spacer` vacío.
   - S4: `AqTextInput` columna `spacedBy(LabelToField)`, etiqueta sin `heightIn(48)`, relleno vertical/horizontal por tamaño, wrapper de toque invisible ≥ 48 dp, helper/error/contador con padding 12 start/end y 8 top. Igual en `AqTextArea`, `AqAuthCode`; `AqSearchInput` elimina el `Column` redundante. `androidx.compose.material3.Text` sustituido por `AqText`.
   - S5: `AqMenuItem` quita `Spacer(weight(1f, fill = false))`; bloque de texto `weight(1f, fill = true)`, trailing `wrapContentWidth`, padding `CellPadding`, hueco bloques `InGroup`, título-subtítulo `X5s`. Rejilla de Inicio y lista de Actividades revisadas.
   - S6: `AqAlert` padding 16, hueco horizontal 12, vertical 8, alto mínimo 56; varias acciones en `Row` debajo del mensaje alineadas al final; cierre con `AqIcons.Close`.
   - S7: `AqAppBar` título de raíz a partir de `ScreenMargin`; D3 aplicado.
   - S8: `AqPinEntry`/`AqPinDots` + `AqKeypad` con separación ≥ 16 dp (`AqLayout.InGroup`).
   - S9: documentado en ADR-0043 §6 (base64 de cabecera PNG, causa de producción, no se inventa texto).
   - S10: D1 (dos primarios → uno Outlined), D4 (título de sección repetido como label).

## Justificación

La migración a Moon cambió símbolos y tokens pero no las medidas, por lo que la geometría seguía rota (etiquetas una letra por línea, pestañas truncadas, bloques a 0 dp, campos desalineados, etc.). Centralizar el espaciado en `AqLayout` y expresar cada hueco con el valor numérico Moon de su token evita que los componentes definan geometría propia y hace la corrección verificable por prueba y por gate estático.
