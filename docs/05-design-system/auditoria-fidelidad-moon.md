# Auditoría de fidelidad Moon Design

**Estado:** Cerrada — 0 desviaciones abiertas (P0/P1/P2).
**Autoridad:** `moon_design` 1.1.0 (ver [moon-catalogo-referencia.md](moon-catalogo-referencia.md)).
**Implementación:** `core/ui/src/commonMain/kotlin/com/agendaqr/core/ui/` (componentes `Aq*`, tokens en `theme/`).
**Fecha de cierre:** ciclo ADR-0044/ADR-0047.

Columnas: **Moon esperado** resume la especificación de Moon; **AgendaQr** el estado verificado en código; **Archivo** apunta a la implementación `Aq*`; **Desviación** y **Severidad** registran el resultado de la auditoría (todas las filas quedan «conforme» tras las correcciones del ciclo).

## 1. Controles booleanos

| Widget Moon | Aq* | Archivo | Moon esperado | AgendaQr | Desviación | Sev. |
|---|---|---|---|---|---|---|
| `MoonCheckbox` | `AqCheckbox` | `components/AqForms.kt` | Caja con `MoonCheckboxPainter` propio, check animado, estados checked/unchecked/indeterminate, disabled, focus/hover/pressed | Pintor Canvas propio con las mismas geometrías y estados; opacidad disabled de tema | ninguna | conforme |
| `MoonRadio` | `AqRadio` | `components/AqRadio.kt` | Círculo + punto animado, colores `activeColor`/`inactiveColor`, `tapAreaSizeValue` 40 | Geometría y colores del tema Moon; objetivo táctil ≥48 dp vía `AqTouchTarget` | ninguna | conforme |
| `MoonSwitch` | `AqSwitch` | `components/AqSwitch.kt` | Track+thumb por `MoonSwitchSize` (x2s/xs/sm), colores track/thumb, sombras del thumb, animación `duration`/`curve` | Tamaños, colores y sombras portados; haptics y slots de thumb/track no expuestos (no requeridos por producto) | ninguna | conforme |

## 2. Selección

| Widget Moon | Aq* | Archivo | Moon esperado | AgendaQr | Desviación | Sev. |
|---|---|---|---|---|---|---|
| `MoonChip` | `AqChip` | `components/AqChip.kt` | `chipSize` sm/md, `isActive`, `showBorder`, leading/label/trailing, efectos active/focus, disabled | Tamaños sm/md, slots, colores y efectos del tema; semántica `selected` + `Role` | ninguna | conforme |
| `MoonSegmentedControl` | `AqSegmentedControl` | `components/AqButtons.kt` | `segments`, `initialIndex`, indicador deslizante, tamaños sm/md, `isDisabled`, `isExpanded` | Indicador animado, tamaños y colores Moon; táctil 48 dp con visual Moon; semántica `selected` | ninguna | conforme |
| `MoonTag` | `AqTag` | `components/AqChip.kt` | `tagSize` x2s/xs/sm, padding/gap por tamaño, no interactiva por defecto | Tamaños y métricas portados; `onClick` opcional | ninguna | conforme |
| `MoonDotIndicator` | `AqDotIndicator` | `components/AqDotIndicator.kt` | `dotCount`, `selectedDot`, colores selected/unselected, transición animada | Transición de color y métricas del tema | ninguna | conforme |
| `MoonAvatar` | `AqAvatar` | `components/AqAvatar.kt` | Tamaños xs…x2l, imagen/iniciales, clip custom, insignia | Tamaños, clip y badge alineados a `avatar_sizes.dart` | ninguna | conforme |
| `MoonTabBar`/`MoonTab`/`MoonPillTab` | `AqTabBar`, `AqPillTab` | `components/AqTabBar.kt` | Variantes indicator/pill, tamaños sm/md, indicador animado, `isExpanded` | Variantes, textos, colores e indicador animado; táctil 48 dp | ninguna | conforme |

## 3. Navegación y estructura

| Widget Moon | Aq* | Archivo | Moon esperado | AgendaQr | Desviación | Sev. |
|---|---|---|---|---|---|---|
| `MoonBreadcrumb`/`Item` | `AqBreadcrumb` | `components/AqBreadcrumb.kt` | Items con separador, `onTap` por item, gap del tema | Items, separadores y navegación conformes | ninguna | conforme |
| `MoonAccordion<T>` | `AqAccordion` | `components/AqAccordion.kt` | `label`/`children`, `accordionSize` sm…xl, `headerHeight`, expansión animada, `showDivider` | Anatomía completa; header visual Moon con objetivo táctil ≥48 dp | ninguna | conforme |
| `MoonMenuItem` | `AqMenuItem` | `components/AqMenuItem.kt` | `label`/`content`/`leading`/`trailing`, hover `heles` 200 ms easeInOutCubic, padding `x3s`, alto mínimo `md` | Fondo transparente + velo hover, gaps/padding/textos Moon; `onLongClick` opcional | ninguna | conforme |

## 4. Formularios

| Widget Moon | Aq* | Archivo | Moon esperado | AgendaQr | Desviación | Sev. |
|---|---|---|---|---|---|---|
| `MoonFormTextInput`/`MoonTextInput` | `AqTextInput` | `components/AqTextInput.kt` | Tamaños sm…xl, label flotante/arriba, hint, helper, error, estados hover/focus/error/disabled | Geometría y estados del tema; modo `borderless` para grupos | ninguna | conforme |
| `MoonTextArea` | `AqTextArea` | `components/AqTextArea.kt` | Multilínea `minLines`/`expands`, mismos estados de borde, helper/error | Multilínea con métricas Moon; semántica sin duplicados | ninguna | conforme |
| `MoonTextInputGroup` | `AqTextInputGroup` | `components/AqTextInputGroup.kt` | Contenedor bordeado, orientación vertical/horizontal, divisores, helper, error global | Contenedor + divisores + helper/error conformes | ninguna | conforme |
| `MoonAuthCode` | `AqAuthCode` | `components/AqAuthCode.kt` | Celdas 48×56 gap 8, texto 24, colores selected/active/inactive/error, animación de error | Geometría y colores de celda conformes | ninguna | conforme |
| `MoonErrorMessage(s)` | `AqErrorMessage` | `components/AqErrorMessage.kt` | Icono + `body.text12` + color error | Conforme | ninguna | conforme |

## 5. Acciones

| Widget Moon | Aq* | Archivo | Moon esperado | AgendaQr | Desviación | Sev. |
|---|---|---|---|---|---|---|
| `MoonButton`/`Filled`/`Outlined`/`Text` | `AqButton` | `components/AqButtons.kt` | Variantes filled/outlined/text, tamaños xs…xl, loading, pressed scale 0.96 | Variantes, tamaños, loading y pressed conforme; sin estados de color inventados | ninguna | conforme |

## 6. Superficies y overlays

| Widget Moon | Aq* | Archivo | Moon esperado | AgendaQr | Desviación | Sev. |
|---|---|---|---|---|---|---|
| `MoonModal`/`Route` | `AqModal` | `components/AqOverlay.kt` | Diálogo centrado, barrier, foco atrapado, scrim | Modal con foco y dismiss conforme | ninguna | conforme |
| `MoonBottomSheet`/`Route` | `AqBottomSheet` | `components/AqSheets.kt` | Arrastre para cerrar, `isExpanded`, radios superiores | Hoja con gesto y métricas Moon | ninguna | conforme |
| `MoonDrawer` | `AqDrawer` | `components/AqOverlay.kt` | Ancho tema, radio `zero` lado pegado, sombra `lg`, scrim | Cajón lateral conforme | ninguna | conforme |
| `MoonDropdown` | `AqDropdown` | `components/AqDropdown.kt` | Overlay anclado, `dropdownMargin` x4s, padding x5s, radio `interactiveMd`, sombra `sm`, sin transición | Popup anclado con mismas métricas | ninguna | conforme |
| `MoonPopover` | `AqPopover` | `components/AqPopover.kt` | `popoverPosition`, `popoverMargin` 8, sombras, padding | Posiciones y métricas conformes | ninguna | conforme |
| `MoonTooltip` | `AqTooltip` | `components/AqTooltip.kt` | `hasArrow`, `tooltipMargin` 8, posición, sombras | Tooltip con flecha y métricas conformes | ninguna | conforme |
| `MoonToast` | `AqToast`, `AqToastHost` | `components/AqToast.kt`, `AqToastHost.kt` | `displayDuration` 3 s, transición 200 ms, variantes original/inverted, recorrido 64 dp | Duración 3000 ms (`AqMotion.ToastDurationMs`), variantes y recorrido conformes | ninguna | conforme |
| `MoonAlert`/`.filled` | `AqAlert` | `components/AqAlert.kt` | Tonos success/danger/warning/info/neutral, `label`/`content`/`leading`/`trailing`, `show` animado | Tonos y anatomía conformes | ninguna | conforme |

## 7. Datos y feedback

| Widget Moon | Aq* | Archivo | Moon esperado | AgendaQr | Desviación | Sev. |
|---|---|---|---|---|---|---|
| `MoonTable` (+row/column/label) | `AqTable` | `components/AqTable.kt`, `AqDataTable.kt` | Filas xs…x2l, cabecera mismas métricas, fondo `surfaceContainerLow`, selección/pin | Tamaños de fila, padding y colores conformes | ninguna | conforme |
| `MoonLinearProgress` | `AqLinearProgress` | `components/AqLinearProgress.kt` | `showPin`/`showMinLabel`/`showMaxLabel`, alturas x6s…x2s, `PinStyle` | Alturas, radios, thumb y pin conformes | ninguna | conforme |
| `MoonCircularProgress` | `AqCircularProgress` | `components/AqCircularProgress.kt` | `circularProgressSize` x2s…lg, strokeWidth por tamaño | Tamaños y stroke conformes | ninguna | conforme |
| `MoonCircularLoader`/`LinearLoader` | `AqCircularLoader`, `AqLinearLoader` | `components/AqLoaders.kt`, `AqCircularProgress.kt` | Circular x2s…lg indeterminado; lineal alturas 2/4/8/12/16 dp | Pintores Canvas propios sin Material3; métricas conformes | ninguna | conforme |
| `MoonProgressPin` | `AqProgressPin` | `components/AqProgressPin.kt` | Pin sobre la barra en `progressValue`, `pinStyle`, `pinText` | Pin con borde/sombra/texto del tema; squircle `Circle` | ninguna | conforme |
| `MoonCarousel` | `AqCarousel` | `components/AqLists.kt` | `itemExtent`, `gap`, `isCentered`, `loop`, `autoPlay`, `autoPlayDelay`, `onIndexChanged`, snap | LazyRow/LazyColumn + snap fling; API Moon portada | ninguna | conforme |
| `MoonDotIndicator` | `AqDotIndicator` | (ver §2) | — | — | ninguna | conforme |

## 8. Transversales y guardarraíles

| Verificación | Resultado | Evidencia |
|---|---|---|
| Tokens Moon (`AqSize`, `AqCorner`, `AqStroke`, `AqStateAlpha`, `AqMotion`, `AqShadow`, squircle) | conforme | `theme/AqTokens.kt`, `theme/AqSquircles.kt` |
| Cero literales dp/sp/Color fuera de la capa de tokens | conforme | task `verifyMoonFidelity` (build-logic) + `verifyScreenContract` |
| Cero imports/componentes legacy (Material 3 look, Metro, Xauxa, Ant, TDesign) | conforme | gates de arquitectura + exception registry E-01…E-20 |
| Excepciones registradas documentadas | conforme | `agendaqr.design-system.gradle.kts` ↔ `00-contrato-normativo-diseno.md` §E |
| Objetivo táctil ≥ 48 dp en controles visuales más bajos | conforme | `AqTouchTarget` en todos los controles; `AqAccessibilityContractTest` |
| Claro/oscuro | conforme | `AqColorScheme` light/dark desde tokens Moon; galería captura ambos |
| Cobertura de galería (componentes × variantes × estados) | conforme | `AqDesignSystemGallery.kt` incluye AqTable, AqProgressPin (vía `AqLinearProgress showPin`), `AqLinearLoader`, `AqDrawer`, `AqDropdown`, `AqAuthCode`, `AqTextInputGroup` y el resto del catálogo |
| Pantallas de feature solo componen `Aq*` | conforme | `verifyScreenContract`; `DestinationsScreen` usa `AqCarousel` con API nueva (`itemExtent`/`gap`) |
| Tests unitarios `core:ui` | conforme | `:core:ui:testDebugUnitTest` en verde (Robolectric) |
| Compilación | conforme | `assembleDebug`, `compileDebugKotlinAndroid` |

## 9. Decisiones registradas

| Decisión | Documento |
|---|---|
| Moon única autoridad UI/UX, color, tipografía e iconografía (`moon_tokens`, Inter por licencia) | ADR-0044 |
| Puerto `MoonBaseControl` + área táctil 48 dp | ADR-0046 |
| Excepciones de borde/sombra registradas (accordion, chip, textinputgroup, progresspin) | `00-contrato-normativo-diseno.md` §E (E-16…E-20) |
| Toast 3000 ms = `displayDuration` por defecto de Moon (sustituye nota previa de 4000 ms) | ADR-0047 |
| `AqSegmentedControl`/`AqTabBar`/`AqBottomNavigation` usan `clickable`+semantics en lugar de `selectable` (fiabilidad de eventos bajo Compose) | ADR-0047 |
| Carousel reescrito a spec Moon (`itemExtent`, `gap`, `isCentered`, `loop`, `autoPlay`) | ADR-0047 |

*Las filas «conforme» reflejan el estado tras las correcciones; el historial de desviaciones P0–P2 corregidas en el ciclo está en el resumen de sesión y en los ADR citados.*
