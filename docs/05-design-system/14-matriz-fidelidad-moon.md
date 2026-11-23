# Matriz de fidelidad Moon → Compose

**Estado:** Normativo — ADR-0039 / ADR-0044 / ADR-0046.  
**Autoridad numérica y de comportamiento:** `moon_design` 1.1.0
(`github.com/coingaming/moon_flutter`, commit `177adb7`, MIT © Yolo Group) y
`moon_tokens` 0.0.6. Las medidas se transcribieron a
`core/ui/.../theme/AqTokens.kt`; ningún componente define literales visuales.

| # | Widget Moon | Fuente (`lib/src/`) | Variantes / tamaños / estados en Moon | `Aq*` destino | Variantes / tamaños / estados en Aq* | Medidas verificadas | Efectos (pressed/hover/focus/pulse/scale) | Semántica a11y | Test que lo cubre | Usos en feature/ | Veredicto |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 1 | `MoonButton` / Filled / Outlined / Text | `widgets/buttons` | xs, sm, md, lg, xl; loading, disabled, pressed | `AqButton` | xs…xl; Primary/Default/Danger/Light/Outline/Text; loading, disabled, pressed | sí (AqTokens + AqLayoutGeometryTest) | scale al pulsar (AqBaseControl, 200 ms, InOutCubic); reducir movimiento lo inmoviliza | `Role.Button`, contentDescription, estado cargando | `AqButtonStyleTest`, `AqLayoutGeometryTest` | S07, S12, AqDockAction, Alert | fiel |
| 2 | `MoonTag` | `widgets/tag` | x2s, xs, sm; tonos | `AqTag` | x2s, xs, sm; tonos AqTone | sí (AqTokens + AqLayoutGeometryTest) | — (insignia no presionable) | contentDescription | `AqLayoutGeometryTest` | S01, S06, menu item trailing | fiel |
| 3 | `MoonSwitch` | `widgets/switch` | x2s, xs, sm; checked/unchecked, disabled | `AqSwitch` | x2s, xs, sm; checked/unchecked, disabled | sí (AqTokens + AqLayoutGeometryTest) | — (control binario) | `Role.Switch`, selected, contentDescription | `AqPhase3ContractTest`, `AqLayoutGeometryTest` | S02/S08 editor (favorito) | fiel |
| 4 | `MoonChip` | `widgets/chip` | sm, md; selected, disabled, pressed | `AqChip` | sm, md; selected, disabled, pressed | sí (AqLayoutGeometryTest) | scale al pulsar implícito vía estado | `Role.Button`, selected | `AqChipGroupTest` | galería, filtro | fiel |
| 5 | `MoonMenuItem` | `widgets/menu_item` | leading/title/subtitle/trailing, selected, pressed, largo | `AqMenuItem` | leading, title, subtitle, trailing, selected, onLongClick | sí (AqLayoutGeometryTest) | pressed (fondo SurfaceActive) | `Role.Button`, selected, contentDescription | `AqLayoutGeometryTest` | S01, S04, S06, S07, carousel | fiel |
| 6 | `MoonTextInput` / `MoonFormTextInput` | `widgets/text_input` | sm, md, lg, xl; focused, error, disabled | `AqTextInput` | xs…xl; focused, error, disabled, helper, contador | sí (AqTokens) | borde de foco (Focus) | contentDescription, error (liveRegion), label asociado | `AqMultilineTextFieldTest`, `AqTextInput` uso | S02, S07, editor | parcial* |
| 7 | `MoonTextArea` | `widgets/text_area` | sm…xl; multiline | `AqTextArea` | multiline, minLines | no (geometría no verificada contra fuente) | borde de foco | contentDescription, error | — | galería | parcial |
| 8 | `MoonCheckbox` | `widgets/checkbox` | checked/unchecked, disabled | `AqCheckbox` | checked/unchecked, disabled | no (geometría no verificada) | — | `Role.Checkbox`, selected | `AqPhase3ContractTest` | galería | parcial |
| 9 | `MoonRadio` | `widgets/radio` | selected, disabled | `AqRadio` | selected, disabled | no | — | contentDescription | `AqSelectTest` (indirecto) | galería | parcial |
| 10 | `MoonSegmentedControl` | `widgets/segmented_control` | sm, md; seleccionado, conteo, deselección (-1) | `AqSegmentedControl` | sm, md; seleccionado, conteo | no (geometría no verificada) | — | `Role.RadioButton`, selected | `AqSegmentedControlTest` | S01, S07 | parcial |
| 11 | `MoonTabBar` / `MoonPillTab` | `widgets/tab_bar` | indicator, pill, custom; sm, md | `AqTabBar`, `AqPillTab` | indicator, pill; sm, md; conteo | no (geometría no verificada) | — | `Role.Tab`, selected | `AqLayoutGeometryTest` (ancho etiqueta) | S01, S04, S06, AqScreen nav | parcial |
| 12 | `MoonAccordion` | `widgets/accordion` | sm, md, lg, xl; expanded/collapsed | `AqAccordion` | expanded/collapsed | no | pressed (fondo) | contentDescription | `AqExpansionPanelTest` | — | parcial |
| 13 | `MoonAlert` | `widgets/alert` | tonos success/danger/warning/info/neutral; descartable | `AqAlert` | tonos AqTone; dismiss/confirm | no | — | `LiveRegionMode.Polite`, contentDescription | `AqAlertTest`, `AqLayoutGeometryTest` | S09, status banner | parcial |
| 14 | `MoonToast` | `widgets/toast` | original, inverted; 64 dp entre toasts, 200 ms | `AqToast` | show/dismiss, acción Deshacer | no | — | `LiveRegionMode.Polite` | `AqToastTest` | toast host | parcial |
| 15 | `MoonBottomSheet` / `MoonModalBottomSheetRoute` | `widgets/bottom_sheet` | modal/no modal, arrastre, expansión | `AqBottomSheet` | title, dismiss, arrastre | no | — | contentDescription | `AqBottomSheetTest` | S03 detalle, destination-detail | parcial |
| 16 | `MoonModal` / `MoonModalRoute` | `widgets/modal` | scrim, foco atrapado | `AqModal` | show, title, confirm/dismiss | no | — | foco atrapado, scrim clicable | `AqModalTest`, `AqDialogAccessibilityTest` | S07, S08, S09 | parcial |
| 17 | `MoonDrawer` | `widgets/drawer` | — | `AqDrawer` | — | no | — | — | — | — | parcial |
| 18 | `MoonDropdown` / `MoonPopover` | `widgets/dropdown`, `widgets/popover` | posición de anclaje | `AqDropdown`, `AqPopover` | posición | no | — | — | — | overflow | parcial |
| 19 | `MoonTooltip` | `widgets/tooltip` | posición | `AqTooltip` | posición | no | — | contentDescription | `AqTooltipTest` | S03 copiado | parcial |
| 20 | `MoonAuthCode` | `widgets/auth_code` | forma de campo, animación error | `AqAuthCode` / `AqPinEntry` / `AqPinDots` | campos, error | no | — | contentDescription | `AqPinDotsErrorTest` | S00 local access | parcial |
| 21 | `MoonAvatar` | `widgets/avatar` | xs, sm, md, lg, xl, x2l; insignia | `AqAvatar` | xs…x2l; insignia, initials | no | — | contentDescription | `AqThumbnailSizeTest` (indirecto) | menu item leading | parcial |
| 22 | `MoonCircularLoader` / `MoonLinearLoader` | `widgets/loaders` | circular x2s…lg; lineal x6s…x2s | `AqCircularLoader`, `AqLinearLoader` | varios tamaños | no | animación | contentDescription | `AqCarouselTest` (indirecto) | galería | parcial |
| 23 | `MoonCircularProgress` / `MoonLinearProgress` | `widgets/progress` | circular x2s…lg; lineal x6s…x2s | `AqCircularProgress`, `AqLinearProgress` | progreso | no | — | contentDescription | `AqProgressTest` (indirecto) | S09 | parcial |
| 24 | `MoonDotIndicator` | `widgets/dot_indicator` | — | `AqDotIndicator` | total, selected | no | — | contentDescription | — | galería | parcial |
| 25 | `MoonErrorMessage(s)` | `widgets/common/error_message_widgets.dart` | — | `AqErrorMessage` | message | no | — | — | — | campo error | parcial |
| 26 | `MoonBreadcrumb` / `MoonBreadcrumbItem` | `widgets/breadcrumb` | — | `AqBreadcrumb` | items | no | — | — | — | galería | parcial |
| 27 | `MoonCarousel` + controlador | `widgets/carousel` | físicas, paginación | `AqCarousel` | items, paginación | no | — | — | `AqCarouselTest` | S03 / carrusel | parcial |
| 28 | `MoonProgressPin` | `widgets/progress_pin` | — | `AqProgressPin` | — | no | — | — | — | — | parcial |
| 29 | `MoonTextInputGroup` | `widgets/text_input_group` | horizontal/vertical | `AqTextInputGroup` | orientación | no | — | — | — | — | parcial |
| 30 | `MoonTable` + columna/fila/cabecera/pie | `widgets/table` | filas xs…x2l | `AqTable`, `AqDataTable` | filas xs…x2l | no | — | — | `AqTable` uso | S12 | parcial |

\* `AqTextInput` está marcado **parcial** porque, si bien sus tamaños y rellenos se
transcribieron de `AqLayout` (ver fuente resuelta), su geometría completa contra
la fuente (radio por tamaño, relleno vertical exacto, salto de etiqueta) aún
carece de una prueba de geometría dedicada que la bloquee.

**Regla de área táctil (ADR-0039 §4.4 / ADR-0046):** todo control con alto visual
< 48 dp conserva su alto y amplía el área táctil a 48 dp mediante
`AqTouchTarget`, sin cambiar su aspecto. Verificado por prueba de geometría en
botón xs, tag x2s, interruptor x2s y chip sm (`AqLayoutGeometryTest`).

**Veredictos:**
- `fiel`: apariencia, estados y medidas verificadas contra la fuente Moon y
  cubiertos por prueba.
- `parcial`: componente presente y funcionante, pero falta verificar alguna
  medida o efecto contra la fuente (excepción documentada; se completa en
  Fase 3).

## Patrones que Moon no trae (composición con piezas Moon, ADR-0039 §3)

| Patrón | Composición con piezas Moon | Nota |
|---|---|---|
| Bottom navigation / riel | `AqTabBar` / `AqPillTab` | Nivel 3 nativo estilizado (ADR-0029) |
| FAB / acción fija inferior | `AqButton` (xl/lg) + `AqShadow.Md` | — |
| Search bar | `AqTextInput` con icono y borrado | — |
| Celda de lista | `AqMenuItem` + `AqAvatar` / `AqTag` | — |
| Empty state | `AqAvatar`/icono + texto + `AqButton` | `AqEmptyPlaceholder` |
| Skeleton | `AqLinearLoader` y formas Moon | animación Moon |
| Date/time picker | `AqModal` con controles Moon | nativo estilizado |
| Swipe actions | `AqButton` revelados | `AqSwipeActions` |
| Pull-to-refresh | `AqCircularLoader` | `AqPullToRefresh` |
