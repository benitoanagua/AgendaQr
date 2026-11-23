# Catálogo Moon Design (contrato de componentes)

**Estado:** Normativo — ADR-0039 / ADR-0040.
**Fuente:** `moon_design` 1.1.0 (`github.com/coingaming/moon_flutter`, commit `177adb7`, MIT © Yolo Group) y `moon_tokens` 0.0.6. Ruta de código citada relativa a `lib/src/`.
**Alcance de este documento:** inventario y medidas verificadas contra la fuente. Las columnas «Estado» se actualizan al portar cada componente. Lo marcado *ver fuente* no se transcribió todavía y se completa al portar.

## 1. Tokens transcritos (hecho en `AqTokens.kt`)

| Token Moon (`theme/tokens/*.dart`) | Token Aq | Valores |
|---|---|---|
| `MoonSizes.sizes` | `AqSize` | x6s 2 · x5s 4 · x4s 8 · x3s 12 · x2s 16 · xs 24 · sm 32 · md 40 · lg 48 · xl 56 · x2l 64 dp |
| `MoonBorders.borders` | `AqCorner` | interactiveXs 4 · interactiveSm 8 · interactiveMd 12 · surfaceXs 4 · surfaceSm 8 · surfaceMd 12 · surfaceLg 16 dp; suavizado squircle 0,9 |
| `MoonBorders` (ancho) | `AqStroke` | default 1 dp · active 1,5 dp |
| `MoonOpacities.opacities` | `AqStateAlpha` | disabled 0,6 |
| `MoonTransitions.transitions` | `AqMotion` | 200 ms · `Curves.easeInOutCubic` = `AqMotion.Easings.InOutCubic` |
| `MoonShadows.light/dark` | `AqShadow` + `Modifier.aqShadow` | sm, md, lg, xl; capas con blur/offset/spread/alfa por tema; color de paleta |
| `utils/squircle/*` | `AqSquircleShape`, `AqSquircles` | esquina Figma (Bézier + arco central) |

Colores, tipografía e iconos de Moon **no** se importan (ADR-0039): los aporta Moon.

## 2. Componentes

| Widget Moon | Fuente | Variantes / tamaños | `Aq*` destino | Estado |
|---|---|---|---|---|
| `MoonAccordion<T>` | `widgets/accordion` | tamaños sm, md, lg, xl | `AqAccordion` | disponible |
| `MoonAlert` | `widgets/alert` | — | `AqAlert` | disponible |
| `MoonAuthCode` | `widgets/auth_code` | forma de campo (`AuthFieldShape`), animación de error | `AqAuthCode` | disponible |
| `MoonAvatar` | `widgets/avatar` | xs, sm, md, lg, xl, x2l; insignia con alineación | `AqAvatar` | disponible |
| `MoonBottomSheet`, `MoonModalBottomSheetRoute` | `widgets/bottom_sheet` | modal y no modal; arrastre y expansión | `AqBottomSheet` | disponible |
| `MoonBreadcrumb`, `MoonBreadcrumbItem` | `widgets/breadcrumb` | — | `AqBreadcrumb` | disponible |
| `MoonButton`, `MoonFilledButton`, `MoonOutlinedButton`, `MoonTextButton` | `widgets/buttons` | xs, sm, md, lg, xl; estados loading, disabled, pressed | `AqButton` | disponible |
| `MoonCarousel` (+ controlador y física) | `widgets/carousel` | — | `AqCarousel` | disponible |
| `MoonCheckbox` | `widgets/checkbox` | — | `AqCheckbox` | disponible |
| `MoonChip` | `widgets/chip` | sm, md | `AqChip` | disponible |
| `MoonCircularLoader`, `MoonLinearLoader` | `widgets/loaders` | circular: x2s…lg · lineal: x6s…x2s | `AqCircularLoader`, `AqLinearLoader` | disponible |
| `MoonCircularProgress`, `MoonLinearProgress` | `widgets/progress` | circular: x2s…lg · lineal: x6s…x2s | `AqCircularProgress`, `AqLinearProgress` | disponible |
| `MoonDotIndicator` | `widgets/dot_indicator` | — | `AqDotIndicator` | disponible |
| `MoonDrawer` | `widgets/drawer` | — | `AqDrawer` | disponible |
| `MoonDropdown` | `widgets/dropdown` | posición de anclaje | `AqDropdown` | disponible |
| `MoonErrorMessage(s)` | `widgets/common/error_message_widgets.dart` | — | `AqErrorMessage` | disponible |
| `MoonFormTextInput`, `MoonTextInput` | `widgets/text_input` | sm, md, lg, xl | `AqTextInput` | disponible |
| `MoonMenuItem` | `widgets/menu_item` | — | `AqMenuItem` | disponible |
| `MoonModal`, `MoonModalRoute` | `widgets/modal` | — | `AqModal` | disponible |
| `MoonPillTab`, `MoonTab`, `MoonTabBar` | `widgets/tab_bar` | variante indicator, pill, custom; tamaños sm, md | `AqTabBar`, `AqPillTab` | disponible |
| `MoonPopover` | `widgets/popover` | posición | `AqPopover` | disponible |
| `MoonProgressPin` | `widgets/progress_pin` | — | `AqProgressPin` | disponible |
| `MoonRadio` | `widgets/radio` | — | `AqRadio` | disponible |
| `MoonSegmentedControl` | `widgets/segmented_control` | sm, md | `AqSegmentedControl` | disponible |
| `MoonSwitch` | `widgets/switch` | x2s, xs, sm | `AqSwitch` | disponible |
| `MoonTable` (+ columna, fila, cabecera, pie) | `widgets/table` | filas xs, sm, md, lg, xl, x2l | `AqTable` | disponible |
| `MoonTag` | `widgets/tag` | x2s, xs, sm | `AqTag` | disponible |
| `MoonTextArea` | `widgets/text_area` | — | `AqTextArea` | disponible |
| `MoonTextInputGroup` | `widgets/text_input_group` | orientación horizontal/vertical | `AqTextInputGroup` | disponible |
| `MoonToast` | `widgets/toast` | original, inverted; recorrido 64 dp; 200 ms entre toasts | `AqToast` | disponible |
| `MoonTooltip` | `widgets/tooltip` | posición | `AqTooltip` | disponible |

Transversales: `MoonBaseControl` (foco, hover, pressed, long-press; efectos pulse, scale y foco), `MoonFocusEffect`, `MoonEffectsTheme`, `utils/touch_target_padding.dart` (ampliación del área táctil).

## 3. Medidas verificadas (`theme/*/*_sizes.dart`)

Alturas y radios usan los tokens de §1; `px` = horizontal del relleno.

| Componente | Tamaño | Alto | Radio | Relleno horiz. | Hueco |
|---|---|---|---|---|---|
| Botón | xs / sm / md / lg / xl | 24 / 32 / 40 / 48 / 56 | InteractiveXs / Sm / Sm / Sm / Md | 8 / 12 / 16 / 16 / 24 | 4 / 4 / 8 / 12 / 16 *(ver fuente: md–xl)* |
| Input de texto | sm / md / lg / xl | 32 / 40 / 48 / 56 | Xs / Sm / Sm / Sm | 8 / 12 / 12 / 16 | 8 |
| Chip | sm / md | 32 / 40 | InteractiveXs / Sm | 12 / 16 | 8 |
| Tag | x2s / xs / sm | 16 / 24 / 32 | Xs / Xs / Sm | 8 / 8 / *ver fuente* | 2 / 2 / 8 |
| Avatar | xs / sm / md / lg / xl / x2l | 24 / 32 / 40 / 48 / 56 / 64 | Xs / Sm / Sm / Sm / Md / Md | — | — |
| Control segmentado | sm / md | 40 / 48 | InteractiveSm | 12 / 16 | 4 / 8 |
| Tab bar | sm / md | 32 / 40 (indicador 2) | Xs / Sm | 12 / 16 | 4 / 8 |
| Acordeón (cabecera) | sm / md | 32 / 40 | InteractiveSm | 8 / 12 | — |
| Switch | x2s / xs / sm | 16 / 24 / 32 (ancho 32 / 44 / 60; pulgar 12 / 16 / *ver fuente*) | píldora | — | — |

**Regla de área táctil (ADR-0039 §4.4):** todo control con alto visual < 48 dp conserva ese alto y amplía el área táctil a 48 dp (como `touch_target_padding.dart` en la fuente), sin cambiar su aspecto.

## 4. Patrones que Moon no trae (composición, ADR-0039 §3)

| Patrón | Composición con piezas Moon | Mecánica de gesto |
|---|---|---|
| Bottom navigation / riel | `AqTabBar` / `AqPillTab` | — |
| FAB y acción fija inferior | `AqButton` (xl/lg, `AqShadow.Md`) | — |
| Search bar | `AqTextInput` con icono y borrado | — |
| Celda de lista | `AqMenuItem` + `AqAvatar` / `AqTag` | — |
| Empty state | `AqAvatar`/icono + texto + `AqButton` | — |
| Skeleton | `AqLinearLoader` y formas Moon | animación Moon |
| Date/time picker | `AqModal` (D-07) con controles Moon | — |
| Swipe actions | `AqButton` revelados | deslizar (Compose) |
| Pull-to-refresh | `AqCircularLoader` | arrastrar (Compose) |
