# Catálogo de referencia Moon Design

**Estado:** Referencia normativa — especificación de cada widget de `moon_design` 1.1.0 (Yolo Group, MIT).
**Fuente canónica:** paquete `moon_design` (`lib/src/widgets/**` y `lib/src/theme/**`), `moon_tokens` 0.0.6, `@heathmont/moon-icons` 10.7.4. Copia de trabajo: `/tmp/moonref/design`.
**Uso:** este documento describe lo que Moon define (anatomía, API pública, variantes, tamaños, estados). La correspondencia con `Aq*` está en [13-catalogo-moon.md](13-catalogo-moon.md); la matriz de cumplimiento en [auditoria-fidelidad-moon.md](auditoria-fidelidad-moon.md); los valores de tokens en [09-tokens-moon.md](09-tokens-moon.md).

Convención: los parámetros listados son los del constructor público de Flutter; «tema» indica que el valor efectivo viene de `Moon*Theme`/`theme/tokens` cuando el parámetro es `null`. En Compose el llamador no pasa estos overrides salvo que el `Aq*` los exponga.

## Controles booleanos

### `MoonCheckbox` — `widgets/checkbox/checkbox.dart`

- API: `value`, `onChanged`, `tristate`, `activeColor`, `inactiveColor`, `borderColor`, `checkColor`, `tapAreaSizeValue` (40), `autofocus`, `focusNode`, `semanticLabel`.
- Pintor propio `MoonCheckboxPainter`: caja con borde, check animado; estados checked/unchecked/indeterminate, enabled/disabled, focused, hovered, pressed.
- Tema: `checkbox_theme.dart` + `checkbox_colors.dart` + `checkbox_properties.dart`.

### `MoonRadio` — `widgets/radio/radio.dart`

- API: `value`, `groupValue`, `onChanged`, `toggleable`, `activeColor`, `inactiveColor`, `tapAreaSizeValue` (40), `autofocus`, `focusNode`, `semanticLabel`.
- Círculo con punto interior animado; estados selected/unselected, disabled, focused, hovered, pressed.
- Tema: `radio_theme.dart` + `radio_colors.dart`.

### `MoonSwitch` — `widgets/switch/switch.dart`

- API: `value`, `onChanged`, `switchSize` (`MoonSwitchSize`), `activeTrackColor`, `inactiveTrackColor`, `thumbColor`, `height`, `width`, `thumbSizeValue`, `duration`, `curve`, `padding`, `hasHapticFeedback`, `autofocus`, `activeTrackWidget`, `inactiveTrackWidget`, `activeThumbWidget`, `inactiveThumbWidget`.
- Tamaños `switch_sizes.dart`: x2s, xs, sm (track/thumb por tamaño). Sombras del thumb en `switch_shadows.dart`; colores en `switch_colors.dart`.
- Estados: on/off con animación de track+thumb, disabled, focused, pressed.

## Selección

### `MoonChip` — `widgets/chip/chip.dart`

- API: `isActive`, `showBorder`, `chipSize` (`MoonChipSize`), `activeColor`, `backgroundColor`, `activeBackgroundColor`, `borderColor`, `textColor`, `focusEffectColor`, `borderWidth`, `height`, `width`, `gap`, `padding`, `minTouchTargetSize`, `ensureMinimalTouchTargetSize`, `activeEffectDuration/Curve`, `focusEffectDuration/Curve`, `disabledOpacityValue`, `leading`, `label`, `trailing`, `onTap`, `onLongPress`, `semanticLabel`, `autofocus`, `isFocusable`, `showFocusEffect`.
- Tamaños `chip_sizes.dart`: sm, md (alto, padding, tipografía). Colores en `chip_colors.dart`; propiedades en `chip_theme.dart`.
- Estados: active/inactive, hover, focus (efecto), pressed, disabled (opacidad), long-press.

### `MoonSegmentedControl` — `widgets/segmented_control/segmented_control.dart`

- API: `segments` (`List<Segment>`), `customSegments`, `initialIndex`, `tabController`, `onSegmentChanged`, `segmentedControlSize` (`MoonSegmentedControlSize`), `isDisabled`, `isExpanded`, `backgroundColor`, `borderRadius`, `gap`, `height`, `width`, `padding`, `transitionDuration/Curve`, `decoration`.
- Tamaños `segmented_control_sizes.dart`/`_size_properties.dart`; colores en `segmented_control_colors.dart`.
- Indicador deslizante animado entre segmentos; estados disabled, pressed, focus.

### `MoonTag` — `widgets/tag/tag.dart`

- API: `tagSize` (`MoonTagSize`), `backgroundColor`, `height`, `width`, `gap`, `padding`, `borderRadius`, `decoration`, `leading`, `label`, `trailing`?, `onTap`, `onLongPress`, `semanticLabel`.
- Tamaños: x2s, xs, sm (`tag` theme/sizes en `theme/tag` si existe; ver `moon_all_themes.txt`).
- Es una etiqueta no interactiva por defecto (admite `onTap`/`onLongPress` opcionales).

### `MoonDotIndicator` — `widgets/dot_indicator/dot_indicator.dart`

- API: `dotCount`, `selectedDot`, `selectedColor`, `unselectedColor`, `gap`, `size`, `transitionDuration`, `transitionCurve`.
- Puntos con color animado (`ColorTweenWithPremultipliedAlpha`); transición entre selección.

### `MoonAvatar` — `widgets/avatar/avatar.dart` (+ `avatar_clipper.dart`)

- API: `avatarSize` (`MoonAvatarSize`), imagen/iniciales, `badge` con alineación (`avatar_size_properties.dart`, `avatar_sizes.dart`).
- Tamaños xs…x2l; clip custom; estados disabled.

### `MoonTabBar` / `MoonTab` / `MoonPillTab` — `widgets/tab_bar/`

- `MoonTabBar`: constructores `MoonTabBar` (tabs con indicador), `MoonTabBar.pill`, `MoonTabBar.custom`; `initialIndex`, `tabController`, `onTabChanged`, `tabBarSize` (`MoonTabBarSize`), `isExpanded`, `gap`, `height`, `width`, `padding`, `transitionDuration/Curve`, `decoration`.
- `MoonTab`: label + icono opcional con indicador subrayado animado; `MoonPillTab`: píldora de fondo animada.
- Colores en `tab_bar_colors.dart`; tamaños sm, md.

## Navegación y estructura

### `MoonBreadcrumb` / `MoonBreadcrumbItem` — `widgets/breadcrumb/`

- `MoonBreadcrumb`: lista de items con separadores, `gap`, scroll si desborda.
- `MoonBreadcrumbItem`: `label`, `leading`, `trailing`, `onTap`, `gap`, `semanticLabel`.
- Tema: `breadcrumb_theme.dart` + `breadcrumb_colors.dart` + `breadcrumb_properties.dart`.

### `MoonAccordion<T>` — `widgets/accordion/accordion.dart`

- API: `label`, `children`, `leading`, `trailing`, `identityValue`, `groupIdentityValue`, `onExpansionChanged`, `accordionSize` (`MoonAccordionSize`), `initiallyExpanded`, `isDisabled`, `showBorder`, `showDivider`, `hasContentOutside`, `maintainState`, `propagateGesturesToChild`, `expandedAlignment`, `expandedCrossAxisAlignment`, `headerHeight`, `headerPadding`, `childrenPadding`, `gap`, `borderRadius`, `clipBehavior`, `iconColor`, `expandedIconColor`, `textColor`, `expandedTextColor`, `backgroundColor`, `expandedBackgroundColor`, `borderColor`, `dividerColor`, `shadows`, `transitionDuration/Curve`, `autofocus`, `focusNode`, `semanticLabel`.
- Tamaños `accordion_sizes.dart`: sm, md, lg, xl (`headerHeight`, paddings, gaps por tamaño).
- Expansión animada con `transitionDuration`; estados disabled, expanded/collapsed, divider entre items.

### `MoonMenuItem` — `widgets/menu_item/menu_item.dart`

- API: `label`, `content`, `leading`, `trailing`, `onTap`, `onLongPress`, `menuItemCrossAxisAlignment`, `labelAndContentCrossAxisAlignment`, `absorbGestures`, `backgroundColor`, `hoverEffectColor`, `decoration`, `height`, `width`, `horizontalGap`, `verticalGap`, `hoverEffectDuration/Curve`, `menuItemPadding`, `borderRadius`, `autofocus`, `focusNode`, `semanticLabel`.
- Fondo transparente que funde `hoverEffectColor` en hover; padding/gaps de `menu_item_theme.dart`.

## Formularios

### `MoonFormTextInput` / `MoonTextInput` — `widgets/text_input/`

- API pública completa en `moon_widget_apis.txt` (labels, hint, helper, error, prefix/suffix, `obscureText`, `enabled`, `readOnly`, `isError`, tamaños `MoonTextInputSize` sm…xl, `AuthFieldShape`).
- Estados: normal, hover, focus (borde `active` 1,5 dp), error, disabled, filled; alturas por tamaño en `text_input` theme.

### `MoonTextArea` — `widgets/text_area/text_area.dart`

- API: `controller`, `hintText`, `initialValue`, `minLines`, `maxLength`, `enabled`, `readOnly`, `expands`, `autofocus`, `autocorrect`, `enableSuggestions`, `scrollController`, colores `backgroundColor/activeBorderColor/inactiveBorderColor/errorColor/hoverBorderColor/textColor/hintTextColor`, `helperPadding`, `textPadding`, `transitionDuration/Curve`, `helper`/`error` slots, `semanticLabel`.
- Multilínea con alto `height` o `expands`; mismos estados de borde que text input.

### `MoonTextInputGroup` — `widgets/text_input_group/text_input_group.dart`

- API: `children` (`List<MoonFormTextInput>`), `orientation` (`MoonTextInputGroupOrientation` vertical/horizontal), `enabled`, `helper`, `errorBuilder`, colores de grupo (`backgroundColor`, `borderColor`, `errorColor`, `hoverBorderColor`, `hintTextColor`), `helperPadding`, `textPadding`, `transitionDuration/Curve`, `helperTextStyle`, `semanticLabel`.
- Contenedor con borde y divisores entre campos; helper debajo; error global tiñe el borde.

### `MoonAuthCode` — `widgets/auth_code/auth_code.dart`

- API: `authInputFieldCount`, `controller`, `onChanged`, `authFieldShape` (`AuthFieldShape`), `showAuthFieldCursor`, `selectedBorderColor`, `activeBorderColor`, `inactiveBorderColor`, `errorBorderColor`, `selectedFillColor`, `activeFillColor`, `inactiveFillColor`, `borderWidth`, `gap`, `height`, `width`, `borderRadius`, `animationDuration/Curve`, `errorAnimationDuration/Curve`, `errorAnimationType`, `errorText`, `hintCharacter`, `obscuringCharacter`, `peekWhenObscuring`, `obscureText`, `enableInputFill`, `autoDismissKeyboard`, `autoFocus`, `autoUnfocus`, `useHapticFeedback`, `textInputAction`, `keyboardType`, `inputFormatters`, `enabled`, `disabledOpacityValue`, `boxShadows`, `activeBoxShadows`, `inActiveBoxShadows`, `mainAxisAlignment`, `hintStyle`, `textStyle`, `focusNode`, `semanticLabel`.
- Celdas independientes con cursor propio, colores por estado (selected/active/inactive/error) y animación de error.
- Geometría de referencia: celda 48×56, gap 8, texto 24.

### `MoonErrorMessage` / `MoonErrorMessages` — `widgets/common/error_message_widgets.dart`

- Mensaje de error con icono, `body.text12`, color de error; variante plural para listas.

## Acciones

### `MoonButton` / `MoonFilledButton` / `MoonOutlinedButton` / `MoonTextButton` — `widgets/buttons/`

- API común: `label`, `onPressed`, `isLoading`, `enabled`, `icon`, `size` (`MoonButtonSize` xs…xl), `width`, `height`, `padding`, colores por variante (`button_colors.dart`), `button_theme.dart`, `button_size_properties.dart`, `button_sizes.dart`.
- Cuatro variantes: filled (primario), outlined, text, y compuesta; tamaños xs, sm, md, lg, xl con alturas/paddings/gaps de icono por tamaño.
- Estados: default, hover, pressed (efecto `MoonBaseControl`), focused, disabled, loading.

## Superficies y overlays

### `MoonModal` / `MoonModalRoute` — `widgets/modal/`

- `MoonModal`: contenedor centrado con `borderRadius`, `backgroundColor`, `decoration`, `semanticLabel`, `child`.
- `modal_theme.dart`/`modal_colors.dart`/`modal_properties.dart`: barrier, transición, padding.

### `MoonBottomSheet` / `MoonModalBottomSheetRoute` — `widgets/bottom_sheet/`

- API: `enableDrag`, `isExpanded`, `borderRadius`, `backgroundColor`, `height`, `minFlingVelocity`, `closeProgressThreshold`, `transitionDuration/Curve`, `onClosing`, `shouldClose`, `scrollController`, `child`.
- Arrastre para cerrar con velocidad umbral; estados expanded/collapsed.

### `MoonDrawer` — `widgets/drawer/drawer.dart`

- API: `width`, `borderRadius`, `backgroundColor`, `drawerShadows`, `decoration`, `semanticLabel`, `child`.
- `drawer_theme.dart`: ancho por defecto, radio `zero` en el lado pegado, sombra `lg`.

### `MoonDropdown` — `widgets/dropdown/dropdown.dart`

- API: `show`, `child` (ancla), `content`, `followerAnchor`, `targetAnchor`, `constrainWidthToChild`, `distanceToTarget`, `min/maxHeight`, `min/maxWidth`, `borderRadius`, `backgroundColor`, `borderColor`, `borderWidth`, `contentPadding`, `dropdownMargin`, `dropdownShadows`, `dropdownAnchorPosition` (`MoonDropdownAnchorPosition`), `offset`, `transitionDuration/Curve` (`Duration.zero` por defecto), `routeObserver`, `groupId`, `semanticLabel`, `onTapOutside`.
- Overlay anclado (no es un campo select); opciones típicas `MoonMenuItem`.

### `MoonPopover` — `widgets/popover/popover.dart`

- API: `show`, `child`, `content`, `popoverPosition` (`MoonPopoverPosition`), `distanceToTarget`, `popoverMargin` (8), `min/max` tamaños, `contentPadding`, `popoverShadows`, `borderRadius`, `backgroundColor`, `borderColor`, `borderWidth`, `transitionDuration/Curve`, `onTapOutside`, `semanticLabel`, `routeObserver`.

### `MoonTooltip` — `widgets/tooltip/tooltip.dart`

- API: `show`, `child`, `content`, `tooltipPosition`, `hasArrow` (por defecto true), `hideOnTap` (true), `arrowBaseWidth`, `arrowLength`, `arrowOffsetValue`, `arrowTipDistance`, `tooltipMargin` (8), `contentPadding`, `tooltipShadows`, `borderRadius`, `backgroundColor`, `borderColor`, `borderWidth`, `min/max` tamaños, `transitionDuration/Curve`, `onTap`, `semanticLabel`.

### `MoonToast` — `widgets/toast/`

- API: `message`, `action`, variantes original/inverted, `displayDuration` (por defecto **3 s**), `transitionDuration` (`defaultTransitionDuration` 200 ms), `timeBetweenToasts`, `horizontalGap`/`verticalGap`, recorrido de 64 dp, posición inferior.
- Tema `toast_theme.dart`: colores por variante, paddings, radios.

### `MoonAlert` / `MoonAlert.filled` — `widgets/alert/alert.dart`

- API: `show`, `label`, `content`, `leading`, `trailing`, `backgroundColor`, `borderColor`, `color`, `borderWidth`, `showBorder`, `borderRadius`, `horizontalGap`, `verticalGap`, `minimumHeight`, `padding`, `transitionDuration/Curve`, `semanticLabel`.
- Constructor `.filled` para variante rellena con `color` de acento.
- Tonos: success, danger, warning, info, neutral (colores en `alert_colors.dart`).

## Datos y feedback

### `MoonTable` (+ `MoonTableRow`, `MoonTableRowLabel`, `MoonTableColumn`) — `widgets/table/`

- `MoonTable`: `columns`, `rows`, `rowSize`/altura por fila (`table` theme sizes xs…x2l), fondo de fila `surfaceContainerLow`, cabecera con mismas métricas.
- `MoonTableRow`: `cells`, `label` (`MoonTableRowLabel` pinned), `selected`, `onSelectChanged`, `onTap`, `height`, `decoration`.
- `MoonTableColumn`: `cell`, `label`, `width`, `onSort`, `showSortingIcon`, `sortingIconGap`, `sortingIconAlignment`, `onTap`, `sortingIcon`.

### `MoonLinearProgress` — `widgets/progress/linear_progress.dart`

- API: `value`, `linearProgressSize` (`MoonLinearProgressSize`), `showPin`, `showMinLabel`, `showMaxLabel`, `pinAffectsHeight`, `pinStyle` (`PinStyle`: `pinColor`, `pinBorderColor`, `thumbColor`, `shadowColor`, `textStyle`, `pinWidth`, `pinBorderWidth`, `pinDistance`, `arrowHeight`, `arrowWidth`, `shadowElevation`), `color`, `backgroundColor`, `textColor`, `height`, `verticalGap`, `borderRadius`, `minLabel`, `maxLabel`, `semanticLabel`.
- Alturas x6s…x2s con radios por tamaño (`linear_progress` theme/sizes); thumb cuadrado en el borde de progreso; pin con flecha.

### `MoonCircularProgress` — `widgets/progress/circular_progress.dart`

- API: `value`, `circularProgressSize`, `sizeValue`, `strokeWidth`, `color`, `backgroundColor`, `strokeCap` (round), `semanticLabel`.
- Tamaños x2s…lg con strokeWidth por tamaño.

### `MoonCircularLoader` / `MoonLinearLoader` — `widgets/loaders/`

- `MoonCircularLoader`: indeterminado, `circularLoaderSize` (x2s…lg), `sizeValue`, `strokeWidth`, `color`, `backgroundColor`, `strokeCap`.
- `MoonLinearLoader`: `linearLoaderSize` (x6s…x2s → alturas 2/4/8/12/16 dp), `height`, `borderRadius`, `color` (`piccolo`→emphasis), `backgroundColor` (transparente).

### `MoonProgressPin` — `widgets/progress_pin/progress_pin.dart`

- API: `progressValue`, `pinText`, `child` (la barra), `pinStyle` (mismos campos de pin que linear progress).
- Superpone una etiqueta-pin sobre la barra en la posición del progreso; `pinAffectsHeight` reserva espacio.

### `MoonCarousel` — `widgets/carousel/carousel.dart`

- API: `itemCount`, `itemBuilder(context, itemIndex, realIndex)`, `itemExtent` (obligatorio), `gap`, `isCentered` (true), `loop`, `autoPlay`, `autoPlayDelay`, `transitionDuration/Curve`, `axisDirection`, `clampMaxExtent`, `anchor`, `velocityFactor` (0.5), `controller`, `physics`, `scrollBehavior`, `onIndexChanged`.
- Snap physics propia; auto-play con delay; contenido centrado con padding simétrico.

### `MoonDotIndicator` — ver Selección.

## Base y transversales

- `MoonBaseControl` (`widgets/common/base_control.dart`): envoltura de foco/hover/pressed/long-press con efectos `MoonFocusEffect`, `MoonHoverEffect`, `MoonControlEffect` (escala 0.96, pulse), `touch_target_padding.dart` (área táctil mínima), `effects_theme.dart`.
- `MoonBorderContainer`, `MoonAnimatedIconTheme`, `MoonDefaultAnimatedTextStyle`: utilidades internas de tema/animación.
- `MoonErrorMessage(s)` en `widgets/common/error_message_widgets.dart`.

## Notas de port a Compose

- Flutter usa `Widget` slots (`label`, `content`, `leading`, `trailing`, `child`); Compose usa `@Composable` slots equivalentes.
- `TabController`/`ScrollController` no existen en Compose: el estado lo gobierna `rememberSaveable`/`LazyListState`; los `Aq*` exponen el índice y callbacks.
- `Duration.zero` en dropdown equivale a sin animación; Compose `Popup` no anima por defecto.
- `semanticLabel` → `contentDescription`/`semantics` en Compose.
