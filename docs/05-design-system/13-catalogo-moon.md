# Catálogo Moon Design — componentes y geometría

**Estado:** Normativo — catálogo único de componentes (ADR-0044).
**Fuente:** `moon_design` 1.1.0 (Yolo Group, MIT), `moon_tokens` 0.0.6 y `@heathmont/moon-icons` 10.7.4. Fuente Kotlin: `core/ui`.
**Regla:** las equivalencias `Aq*` implementan la anatomía, variantes, tamaños, estados y comportamiento de Moon. Los valores exactos de color, roles, tipografía e iconos están en [09-tokens-moon.md](09-tokens-moon.md). Esta tabla es el inventario único consumido por el gate de completitud.

## 1. Tokens Moon portados a Aq

| Token Moon (`theme/tokens/*.dart`) | Token Aq | Valores |
|---|---|---|
| `MoonSizes.sizes` | `AqSize` | x6s 2 · x5s 4 · x4s 8 · x3s 12 · x2s 16 · xs 24 · sm 32 · md 40 · lg 48 · xl 56 · x2l 64 dp |
| `MoonBorders.borders` | `AqCorner` | interactiveXs 4 · interactiveSm 8 · interactiveMd 12 · surfaceXs 4 · surfaceSm 8 · surfaceMd 12 · surfaceLg 16 dp; suavizado squircle 0,9 |
| `MoonBorders` (ancho) | `AqStroke` | default 1 dp · active 1,5 dp |
| `MoonOpacities.opacities` | `AqStateAlpha` | disabled 0,6 |
| `MoonTransitions.transitions` | `AqMotion` | 200 ms · `Curves.easeInOutCubic` = `AqMotion.Easings.InOutCubic` |
| `MoonShadows.light/dark` | `AqShadow` + `Modifier.aqShadow` | sm, md, lg, xl; capas con blur/offset/spread/alfa por tema; color de paleta |
| `utils/squircle/*` | `AqSquircleShape`, `AqSquircles` | esquina Figma (Bézier + arco central) |

Color, tipografía, iconografía, geometría y movimiento se portan desde Moon; los valores de color/tipo/iconos están en 09-tokens-moon.md. Inter es la única sustitución de familia tipográfica por licencia.

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
| `MoonDropdown` | `widgets/dropdown` | menú de opciones anclado; no es un campo select cerrado | `AqDropdown` | disponible |
| Select / Combobox / Pagination / Sidebar / Leaderboard (solo Figma v1, sin widget en `moon_design` 1.1.0) | archivo Figma «Moon Design System v1» | geometría en `evidencia/figma/moon-v1-tokens-y-componentes.json` | `AqSelect`, `AqCombobox`, `AqPagination`, `AqSidebar`, `AqLeaderboard` | disponible |
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

Transversales: `MoonBaseControl` (foco, hover, pressed, long-press; efectos pulse, scale y foco), `MoonFocusEffect`, `MoonEffectsTheme`, `utils/touch_target_padding.dart` (ampliación del área táctil). `AqButton` aplica el scale pressed de Moon (0,96) al contenido visual, preserva el hit target y lo desactiva con reducción de movimiento. El pressed no cambia el relleno ni el color del texto: PV-01 prohíbe usar `brandFillActive`, `dangerActive` o `surfaceActive` como estados inventados de botón.

## 3. Medidas de referencia Moon (`theme/*/*_sizes.dart`)

Valores transcritos de la versión indicada; `px` = padding horizontal. No son una matriz de auditoría ni un estado de fidelidad por pantalla

| Componente | Tamaño | Alto | Radio | Relleno horiz. | Hueco |
|---|---|---|---|---|---|
| Botón | xs / sm / md / lg / xl | 24 / 32 / 40 / 48 / 56 | InteractiveXs / Sm / Sm / Sm / Md | 8 / 12 / 16 / 16 / 24 | 4 / 4 / 8 / 12 / 16 |
| Input de texto | sm / md / lg / xl | 32 / 40 / 48 / 56 | Xs / Sm / Sm / Sm | 8 / 12 / 12 / 16 | 8 |
| Chip | sm / md | 32 / 40 | InteractiveXs / Sm | 12 / 16 | 8 |
| Tag | x2s / xs / sm | 16 / 24 / 32 | Xs / Xs / Sm | 8 / 8 / 8 | 2 / 2 / 8 |
| Avatar | xs / sm / md / lg / xl / x2l | 24 / 32 / 40 / 48 / 56 / 64 | Xs / Sm / Sm / Sm / Md / Md | — | — |
| Control segmentado | sm / md | 40 / 48 | InteractiveSm | 12 / 16 | 4 / 8 |
| Tab bar | sm / md | 32 / 40 (indicador 2) | Xs / Sm | 12 / 16 | 4 / 8 |
| Acordeón (cabecera) | sm / md | 32 / 40 | InteractiveSm | 8 / 12 | — |
| Switch | x2s / xs / sm | 16 / 24 / 32 (ancho 32 / 44 / 60; pulgar 12 / 16 / 20) | píldora | — | — |

### 3.1 Contrato de ancho, altura y adaptación al contenido

Estas reglas son normativas para `Aq*` y para la galería de componentes; preservan la geometría de Moon y no son estilos alternativos:

- **Botón `fullWidth`:** `AqButton(fullWidth = true)` implementa `MoonButton.isFullWidth`: llena el ancho disponible del contenedor padre; sin la opción conserva el ancho intrínseco determinado por contenido y padding Moon. El alto visual sigue siendo el tamaño Moon seleccionado.
- **Campo de selección:** `AqSelect` es el campo cerrado del catálogo Moon v1 (Figma «Select»): misma anatomía que `AqTextInput` (32/40/48/56 según tamaño, etiqueta flotante solo en xl) con chevron y menú `AqDropdown` anclado. `AqDropdown` sigue siendo solo el menú de opciones; no se debe describir como un campo select ni inventar una altura de campo separada.
- **Área táctil separada:** el contenedor interactivo amplía el hit target a 48 dp sin modificar el tamaño, borde, radio, padding o posición del componente visible.
- **States / Variants en la galería:** los ejemplos se organizan en contenedores flexibles que se ajustan al contenido y al ancho disponible (wrap cuando corresponda). No se fijan anchos de panel ni se recortan labels, estados, variantes o controles; no se usa un ancho fijo para hacer coincidir columnas.
- **Sin restricciones artificiales:** no introducir `width`/`height` fijos, `fillMaxHeight` o `weight` que deformen controles o recorten contenido, salvo que la anatomía Moon de ese componente lo requiera y esté documentado en esta tabla.

**Regla de área táctil (ADR-0044 §4.4):** todo control con alto visual < 48 dp conserva ese alto y amplía el área táctil a 48 dp (como `touch_target_padding.dart` en la fuente), sin cambiar su aspecto.

## 4. Composiciones de Agenda QR

Moon no define algunos patrones de producto como componentes independientes. Se componen con componentes Moon y mecánica de plataforma, sin crear un segundo sistema visual.

| Patrón de producto | Composición permitida |
|---|---|
| Navegación inferior / riel | `AqTabBar` / `AqPillTab` + navegación Compose |
| Acción fija / FAB | `AqButton` (tamaño Moon) + token `AqShadow` cuando la referencia Moon lo requiere |
| Search bar | `AqTextInput` + iconos Moon |
| Celda de lista | `AqMenuItem` + `AqAvatar` / `AqTag` |
| Empty state | icono Moon + texto Moon + `AqButton` |
| Skeleton | `AqLinearLoader` y formas Moon |
| Date/time picker | `AqModal` + controles Moon; selector nativo solo cuando el contrato de plataforma lo exige |
| Swipe actions | `AqButton` revelado por gesto Compose |
| Pull-to-refresh | `AqCircularLoader` + gesto Compose |

## 5. Reglas de implementación

- `core/ui` es la única capa que define tokens y componentes visuales.
- Cada pantalla consume componentes Moon existentes; no crea estilos locales.
- Los tamaños visuales Moon se conservan. El objetivo táctil se amplía a 48 dp mediante `AqTouchTarget`, sin deformar el control.
- Los estados, efectos de foco/pulsación y movimiento se centralizan en `AqBaseControl` y `AqMotion`.
- El catálogo enumera cada símbolo `Aq*` exportado y debe mantenerse sincronizado con `AqDesignSystemGallery`, pruebas y gate de completitud.
- La duración de toast es `AqMotion.ToastDurationMs` = 3 000 ms, el `displayDuration` por defecto de Moon (`toast.dart`); la animación es `defaultTransitionDuration` (200 ms) y la separación/recorrido de toasts sigue el tema Moon.
- No se mantienen fichas de componente separadas: este catálogo, los tokens y el código/test de `core/ui` son la fuente activa.
