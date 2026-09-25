# Xauxa — Brechas detectadas y trazabilidad del laboratorio

Este documento registra, con evidencia de inspección, las diferencias entre el
Xauxa Design System documentado (snapshot v13 del ZIP `xauxa-design-system-kt.zip`)
y lo realmente implementado en `core:ui` de Agenda QR. Lo mantienen actualizado
el laboratorio de componentes y las compuertas de verificación; el laboratorio
muestra estas brechas en vivo, no las corrige.

## Fuentes inspeccionadas

| Fuente | Qué aporta |
|---|---|
| `xauxa-design-system/tokens/tokens.json` (v13) | Fuente canónica de tokens: primitivos, semánticos dark-first, tema light, marca por producto, focus, motion, tipografía. |
| `xauxa-design-system/docs/design-system.html` (v13) | Documento vivo: 9 foundations, 26 componentes (C01–C26 + C1b), 6 patterns, matriz de estados §10, auditoría de contraste, breakpoints §07. |
| `xauxa-design-system/kotlin-compose/` (v13) | `XauxaLiveTile`, `XauxaCommandBar`, `XauxaTurnstileNav`, `ReducedMotion`: código escrito contra APIs reales, **no compilado contra ningún proyecto**. Adoptado en `core:ui` en esta pasada (ver §3, "Kotlin del ZIP: adoptado en esta pasada") — misma advertencia de no-compilado aplica a la adopción. |
| `design-tokens.json` (raíz del repo, v9, **retirado**) | Snapshot documentado ampliado que se migró a `XauxaTokens.kt` y se eliminó: la fuente canónica es Kotlin y no quedan consumidores del JSON (ver historial Git). |
| `core/ui/.../theme/XauxaTokens.kt` | Tokens implementados que el código consume realmente. |

## 1. Deriva de valores entre Xauxa canónico y la implementación

Los tokens implementados de neutral/semántico **no coinciden** con `tokens.json`
v13 (ni con su tema light). Probablemente fueron derivados del snapshot v8 más
una paleta clara con tinte teal propia. No se modifican en esta pasada (es una
decisión de diseño con impacto visual); se registran:

| Token implementado | Valor actual | Xauxa v13 light | Xauxa v13 dark |
|---|---|---|---|
| `Background` | `F8FAFA` | — (no existe el token) | — |
| `Surface` | `FFFFFF` | `surface` = `FFFFFF` ✓ | `gray.95` = `17171B` |
| `Surface2` | `F0F4F3` | `FAFAF9` ✗ | `gray.90` = `1F1F24` |
| `Border` | `D7DEDC` | `E2E2DF` ✗ | `gray.80` = `2A2A30` |
| `TextPrimary` | `17201E` | `17171B` ✗ | `gray.05` = `F4F4F6` |
| `TextSecondary` | `4E5B57` | `6B6B72` ✗ | `gray.40` = `9C9CA6` |
| `TextTertiary` | `6C7773` | `6A6A75` ✗ | `gray.60` = `90909A` |
| `Success` | `2E7D5B` | `green.50` = `3DA35D` ✗ | ídem |
| `Danger` | `B3261E` | `red.60` = `D9534F` ✗ | ídem |
| `Warning` | `8A5A00` | `amber.60` = `B5790A` ✗ | ídem |
| `Info` | `245E9B` | `blue.55` = `3A7BD5` ✗ | ídem |
| `Brand` / `BrandAccent` / `OnBrand` | `0E7D6E` / `7FD9C9` / blanco | teal.60 / teal.30 / white ✓ | ✓ |

Hallazgo de contraste relevante: `TextTertiary` implementado (`6C7773`) es
prácticamente el valor `text-3` que Xauxa **rechazó en v10** por contraste
insuficiente (2.90:1 sobre `surface-3`; lo corrigió a `90909A` dark / `6A6A75`
light, 4.76:1 / 4.65:1). Agenda QR usa ese color para texto deshabilitado de
botones — conviene revisarlo con la auditoría de contraste de Xauxa antes de
dar por cerrada la paleta.

## 2. Tokens documentados: estado tras la migración a Kotlin

- **Dimensión de tema oscuro**: **IMPLEMENTADA con valores de referencia**.
  `DarkXauxaColorScheme` aplica surface/surface-2/surface-3/border/
  text-1/2/3 y semánticos de xauxa v13 dark-first, con fondo `#151218` de
  XauxaXcan. Definidos por referencia, **pendientes de validación visual de
  producto** (cada contrato lo declara PENDING).
- **Contenedores de estado** `-bg`: `SuccessBg/DangerBg/WarningBg/InfoBg`
  existen en ambos esquemas y los consumen Toast, InlineResult y StatusBanner.
- **Colores por tipo de QR**: `type-url`, `type-email`, `type-text` (usados por
  XauxaXcan para color-coding de listas; Agenda QR aún no los consume).
- **Anillo de foco**: `focus.ring-color` (= brand-accent), `ring-offset` (2px).
  **CERRADO en esta pasada**: `XauxaColor.FocusRing` + modificador
  `xauxaFocusRing` en clickables personalizados; offset documentado como
  `accessibility.focusOffset` en el snapshot. El pixel-review manual del anillo
  queda pendiente (ver documento del laboratorio).
  Material/Carbon.
- **Motion**: **CERRADO parcialmente en esta pasada**. `XauxaMotion` ganó
  `Easings.Standard/Emphasized/Decelerate` (`androidx.compose.animation.core.Easing`,
  mismas curvas que `EasingStandard`/`EasingEmphasized`/`EasingDecelerate`,
  que se conservan como documentación CSS). Antes ningún componente los
  consumía; ahora los usan `XauxaLiveTile`, `XauxaCommandBar` (transición del
  overflow via `DropdownMenu`, sin tocar) y `xauxaTurnstileEnter`/
  `xauxaTurnstileExit`. Sigue pendiente auditar el resto de animaciones
  existentes (ripple de Material en botones, `AnimatedVisibility` si se
  agrega en el futuro) para que consuman el mismo token en vez de sus
  valores por defecto.
- **Familias tipográficas**: Xauxa documenta Archivo (display) y Roboto (UI);
  `XauxaType` solo define tamaños.

## 3. Componentes documentados (C01–C26) vs implementados

| Xauxa | Componente | Estado en `core:ui` |
|---|---|---|
| C1 | Tile | `XauxaTile` — superficie plana con borde; sin regiones head/body/stats ni alto mínimo 92 del spec |
| C1b | Tile tipográfico | No implementado |
| C2 | Botón | `XauxaPrimaryButton`/`XauxaSecondaryButton`/`XauxaDangerButton` con `isLoading`; sin `enabled` en el textual (brecha) |
| C3 | Fila de lista | `XauxaListRow` ✓ (separador por borde + marcador de 2px) |
| C4 | Badge | `XauxaBadge` ✓ (outline/sólido, rectangular) |
| C5 | Scanner viewport | `XauxaScannerViewport` ✓ (preview real; escaneo real PENDING por plataforma) |
| C6 | Upload zone | `XauxaFileUpload` ✓ (estados; picker real PENDING por plataforma) |
| C7 | Notification / toast | `XauxaToast` ✓ (con acción y descarte) |
| C8 | Settings select / toggle | `XauxaSettingRow` ✓ (toggle cuadrado + modo navegación) |
| C9 | Stats block | `XauxaStatBlock` ✓ (centrado, patrón stats) |
| C10 | Error page | `XauxaErrorPage` ✓ (pantalla completa con reintento) |
| C11 | Icon button | `XauxaIconButton` ✓ (contentDescription + 48dp; sin iconos Material por la compuerta) |
| C12 | Botón de acción de tile | `XauxaTextAction` + `XauxaIconButton` según peso visual |
| C13 | Status badge | `XauxaStatusBanner` — neutro/peligro con `DangerBg`; tercer tono DOCUMENTED |
| C14 | Filter pills | `XauxaFilterChip` ✓ (interactivo; `XauxaCategoryChip` para clasificación estática) |
| C15 | Empty state | `XauxaEmptyState` ✓ |
| C16 | Overflow footer | `XauxaLoadMoreFooter` ✓ (la paginación se compone de él, sin duplicado) |
| C17 | Inline result banner | `XauxaInlineResult` ✓ (tonos -bg); `XauxaStatusBanner` para estado persistente |
| C18 | Loading spinner | `XauxaLoading` — indeterminado solo |
| C19 | Search bar | `XauxaSearchBar` ✓ |
| C20 | Text input / Textarea | `XauxaTextInput` ✓ (textarea con singleLine=false) |
| C21 | Favorite toggle | `XauxaFavoriteToggle` ✓ (control real; integración de producto pendiente, marcado demo) |
| C22 | Category chip | `XauxaCategoryChip` ✓ (estático; distinto del filtro interactivo) |
| C23 | Destination card | `XauxaHeroCard` ✓ (compone `XauxaTile`: número Display + footer) |
| C24 | Modal / Dialog | `XauxaDialog` ✓ (dos acciones, confirmación danger) |
| C25 | Skeleton loading | `XauxaSkeleton` ✓ (estático, sin movimiento decorativo) |
| C26 | Load more / Paginación | `XauxaLoadMoreFooter` ✓ |

### Kotlin del ZIP: adoptado en esta pasada

`kotlin-compose/` del ZIP traía `XauxaLiveTile`, `XauxaCommandBar`,
`XauxaTurnstileNav` y `ReducedMotion` (§05/§07/§08 del checklist KMP), sin
compilar contra ningún proyecto real (advertencia del propio README del
ZIP). Se adoptaron en `core:ui` con estos cambios respecto al original:

| Componente del ZIP | Cambio al adoptarlo en `core:ui` |
|---|---|
| `ReducedMotion.kt` | Era Android-only (`Settings.Global`). Se convirtió en `expect`/`actual` real: `ReducedMotion.android.kt` (idéntico al ZIP), `ReducedMotion.ios.kt` (nuevo — `UIAccessibility.isReduceMotionEnabled` + `UIAccessibilityReduceMotionStatusDidChangeNotification`, mismo patrón de interop que `PlatformNetworkMonitor.ios.kt`), `ReducedMotion.wasmJs.kt` (nuevo — lee `prefers-reduced-motion` una vez al montar vía `@JsFun`; no observa cambios en vivo, PENDING, es el laboratorio de desarrollo, no producto). |
| `XauxaLiveTile.kt` | Puerto directo a `core:ui/.../components/`, cambiando `design.xauxa.tokens.XauxaColors`/`XauxaMotion` por `XauxaColor.Surface`/`XauxaSpacing.Lg`/`XauxaMotion.DurationMediumMs`/`XauxaMotion.Easings.Standard` propios, y `LocalReducedMotion` del expect/actual nuevo en vez del `design.xauxa.components` Android-only. |
| `XauxaCommandBar.kt` | Puerto con un cambio obligatorio: `Icons.Default.MoreVert` está prohibido por la compuerta `verifyDesignSystemCompliance` (`bannedImports` en `agendaqr.design-system.gradle.kts`). Se reemplazó por `XauxaIconButton` + glifo de texto `"⋮"`, el mismo patrón que ya usa `XauxaToast` para su botón de descarte `"×"`. `tonalElevation` se fija a `XauxaSpacing.None` (invariante 03: separación por borde, nunca sombra). |
| `XauxaTurnstileNav.kt` | El ZIP lo tipa contra `AnimatedContentTransitionScope<NavBackStackEntry>` (navigation-compose). Agenda QR no tiene esa dependencia — navega con un `when` sobre estado de pantalla en `AgendaQrApp.kt`. Se generalizó a `AnimatedContentTransitionScope<S>` para usarlo con `AnimatedContent(targetState = ...)` directo, sin agregar navigation-compose. |

**Conectado a producto** (no solo disponible en `core:ui`):

- `ProvideReducedMotion` envuelve `AgendaQrSharedApp()` (`shared/.../AgendaQrShared.kt`), el único punto de entrada compartido por Android/iOS/wasmJs — así `LocalReducedMotion.current` (que `XauxaLiveTile` ya lee por default) es real en las tres plataformas desde una sola llamada, en vez de duplicarla por `MainActivity`/`MainViewController`.
- `xauxaTurnstileEnter`/`xauxaTurnstileExit` envuelven el `when (route)` de `DestinationRoute` en `AgendaQrApp.kt` con `AnimatedContent`. Deliberadamente **no** se tocó el `when` externo de `showImportBatch`/`showSearch`/`showContexts`/`showOperations` (banderas booleanas independientes con wiring de viewModel distinto por rama): envolverlo a ciegas sin poder compilar/probar en este pase era más riesgo que valor. La dirección `reverse` (atrás vs. adelante) no se infiere del `route` — ts3 del checklist original ya avisaba que esto no resuelve predictive back — así que hoy toda transición entra desde la derecha; diferenciar back queda **PENDING**, documentado también en el propio `XauxaTurnstileNav.kt`.
- `XauxaCommandBar` reemplaza los dos `Row` de acciones secundarias en `DestinationDetailScreen` (pantalla de detalle/foco, cumple cb1): `Edit`/`Share` visibles, `Delete` a overflow a propósito (acción destructiva, no a un toque de las otras).

**Sin verificar** (honesto, como el resto de este documento): nada de lo
anterior se compiló contra un proyecto Android/iOS real en este pase — el
entorno donde se escribió no tiene SDK de Android ni acceso a los
repositorios Maven de Google/JetBrains. Antes de dar esto por cerrado falta:
correr `verifyDesignSystemCompliance` (debería pasar: sin íconos Material,
sin hex/dp/sp crudos, sin `RoundedCornerShape`/`shadow(` fuera de
`XauxaTokens.kt`), compilar los tres targets, y el mismo chequeo de VoiceOver
pendiente que ya señalaba cb5 en el ZIP original para el botón de overflow.

### Decisiones de fidelidad visual (XauxaXcan → AgendaQr)

Verificadas contra `wwwroot/assets/styles/*.css` y capturas del catálogo:

| Referencia | Decisión en AgendaQr | Evidencia |
|---|---|---|
| Botones `uppercase bold tracking-wide` (`buttons.css`) | Etiquetas en mayúsculas con `LetterSpacingWide` en primary/secondary/danger/text-action | `XauxaSecondaryButton`, captura secondary |
| Tile header `bg-primary` + h2 uppercase (`tiles.css`) | Nuevo `XauxaTileHeader` opt-in (banda Brand, sin `shadow-lg` por invariante); `XauxaSection` existente no cambia de estructura | Captura tileheader |
| Filtros `uppercase bold` + activo `bg-primary` (`history.css`) | `XauxaFilterChip` en mayúsculas; activo Brand | Preview filter-chip |
| Stats: valor `text-primary font-bold font-mono`, etiqueta `uppercase` (`stats.css`) | `XauxaStatBlock` valor Brand + `FamilyMono`, etiqueta en mayúsculas | Captura stat |
| `tile-stats` footer con `border-t-2` | Footer de hero con separador `BorderStrong` (2px, dentro de la invariante 1–2px) | Captura hero |
| Scan-frame 3px + badges `rounded-sm` | Marco y marcadores en `BorderStrong` (2px); radios fijados a 0 por invariante | Contrato + capturas |
| Sin webfont (sans del sistema + `font-mono`) | `FamilyUi` = sistema, `FamilyMono` = monoespaciada; webfonts Archivo/Roboto pendientes por decisión de producto | Specimen de tipo |
| Neutros con tinte púrpura (`#FFF7FF`, `#151218`) | Se conserva el tinte teal de AgendaQr (`brand` teal por `color.product.agendaqr` de la referencia complementaria y ADR-0002) | Esquemas |
| Press `scale(0.96)`, `animate-pulse`, `slide-in-down` | Ripple de Material para pressed; sin loops (invariante 07); skeleton estático | Componentes |

### Decisión vigente que limita variantes

ADR-0002 (v12) **rechazó** la variación de acento por categoría de tile: un
solo acento de marca, resto semántico. El laboratorio no introduce tonos por
categoría.

## 4. Tema oscuro: estado real

- `AgendaQrTheme(darkTheme = true)` provee `DarkXauxaColorScheme` vía
  `LocalXauxaColorScheme` y mapea `MaterialTheme` al esquema. Los componentes
  Xauxa leen la fachada `XauxaColor`, por lo que **sí cambian** con el tema.
- La producción no usa el wrapper y queda en esquema claro, idéntica a antes.
- En el laboratorio, el toggle de preview oscuro aplica el esquema de
  referencia. El inspector lo marca PENDING: valores definidos por
  referencia, no verificados por producto.
- Soporte honesto por componente en el catálogo: `PENDING` para los 31
  componentes y la fundación de color; `NOT_APPLICABLE` para fundamentos no
  cromáticos.

## 5. Trazabilidad del catálogo del laboratorio

| Entrada del catálogo | Fuente de verdad del código | Referencia Xauxa |
|---|---|---|
| `XauxaScreen` | `core/ui/.../components/XauxaComponents.kt` | §03 superficies planas, regla 02/03 |
| `XauxaSection` | ídem | §03, grid de página §08 |
| `XauxaTile` | ídem | C1 |
| `XauxaTileHeader` | `XauxaExtendedComponents.kt` | tile-header (`tiles.css`): banda Brand sin sombra |
| `XauxaPrimaryButton` / `XauxaSecondaryButton` / `XauxaDangerButton` / `XauxaTextAction` / `XauxaIconButton` / `XauxaFilterChip` | `XauxaComponents.kt` + `XauxaExtendedComponents.kt` | C2 (+§10 estados) |
| `XauxaTextInput` / `XauxaSearchBar` / `XauxaSettingRow` | `XauxaExtendedComponents.kt` | C19/C20, patrón settings |
| `XauxaToast` / `XauxaInlineResult` | ídem | Notificaciones, C13/C17 |
| `XauxaStatusBanner` | `XauxaComponents.kt` | C13/C17 |
| `XauxaLoading` / `XauxaSkeleton` / `XauxaLoadMoreFooter` | ambos | C18/C25/C26 |
| `XauxaEmptyState` / `XauxaErrorPage` | ambos | C15, error-page |
| `XauxaListRow` / `XauxaStatBlock` / `XauxaBadge` / `XauxaCategoryChip` | `XauxaExtendedComponents.kt` | Filas, stats, badges, C14/C22 |
| `XauxaHeroCard` / `XauxaDialog` | ídem | C23/C24 |
| `XauxaFavoriteIndicator` / `XauxaFavoriteToggle` | ambos | C21 |
| `XauxaScannerViewport` / `XauxaFileUpload` | `XauxaExtendedComponents.kt` | Scanner-viewport, uploader (PENDING plataforma) |
| `XauxaQrPreview` | expect + actuals Android/iOS/Wasm (web muestra placeholder; PENDIENTE) | C23/P3 (patrón QR) |
| `XauxaLiveTile` | `core/ui/.../components/XauxaLiveTile.kt` | Checklist KMP §05; no está en el catálogo del laboratorio todavía (PENDING) |
| `XauxaCommandBar` / `XauxaOverflowAction` | `core/ui/.../components/XauxaCommandBar.kt`; conectado en `DestinationDetailScreen` | Checklist KMP §07; no está en el catálogo del laboratorio todavía (PENDING) |
| `xauxaTurnstileEnter` / `xauxaTurnstileExit` | `core/ui/.../components/XauxaTurnstileNav.kt`; conectado en `AgendaQrApp.kt` (ruta de destinos) | Checklist KMP §08; dirección "reverse" no conectada aún (PENDING) |
| `LocalReducedMotion` / `ProvideReducedMotion` | `core/ui/.../motion/ReducedMotion.kt` (expect) + actuals Android/iOS/Wasm; conectado en `AgendaQrSharedApp()` | Checklist KMP §05 (lt4); base de accesibilidad de movimiento para todo lo anterior |
| `XauxaColor` / `XauxaSpacing` / `XauxaMetrics` / `XauxaType` / `XauxaMotion` (`xauxa-focus` documenta el anillo) | `XauxaTokens.kt` | §01 color, §04 spacing, §06 métricas, §02 tipografía, §05 motion, §07 breakpoints, §10 foco |

Cada entrada del inspector enlaza los tokens que consume con `XauxaTokenIndex`
(modelo del lab), que valida contra la capa de tokens real en las pruebas.

## 6. Conclusiones accionables

1. Decidir si la paleta implementada se alinea a Xauxa v13 light o se registra
   como paleta propia de Agenda QR; hoy es una mezcla sin decisión explícita.
2. Añadir la dimensión dark a los tokens es prerrequisito para cualquier
   declaración de soporte de tema oscuro.
3. Priorizar las brechas de API que afectan a pantallas previstas: campo de
   texto (C20), chips/filtros (C14/C22), modal (C24), tonos del banner con `-bg`.
4. Habilitar Roborazzi para fijar los previews del lab como evidencia visual
   automatizada (hoy no existe infraestructura de screenshot tests).
5. **Nuevo esta pasada**: compilar y correr `verifyDesignSystemCompliance` +
   los tres targets (Android/iOS/wasmJs) sobre `XauxaLiveTile`,
   `XauxaCommandBar`, `xauxaTurnstileEnter`/`Exit` y el `ProvideReducedMotion`
   expect/actual — nada de esto se verificó contra un proyecto real (ver
   nota de "Sin verificar" arriba).
6. **Nuevo esta pasada**: agregar `XauxaLiveTile` y `XauxaCommandBar` al
   catálogo del laboratorio (`LabComponentCatalog.kt`) — hoy solo existen en
   `core:ui` y conectados a un punto de producto, pero no aparecen en el
   inspector/catálogo como el resto de C01–C26.
7. **Nuevo esta pasada**: decidir la dirección `reverse` de
   `xauxaTurnstileEnter`/`xauxaTurnstileExit` para navegación hacia atrás
   (hoy siempre entra desde la derecha — ver `XauxaTurnstileNav.kt`, ts3) y
   evaluar si el `when` de `showImportBatch`/`showSearch`/`showContexts`/
   `showOperations` en `AgendaQrApp.kt` debe sumarse a la transición turnstile
   o queda fuera de alcance (ts5).
