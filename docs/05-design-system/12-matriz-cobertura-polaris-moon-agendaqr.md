# Matriz de cobertura Polaris / Moon / Agenda QR

**Estado:** Normativo — auditoría local actualizada el 2026-11-19 contra el árbol de trabajo de `main`.
**Autoridad:** ADR-0025, ADR-0026, contrato normativo v8.3, instantánea `@shopify/polaris-tokens@9.4.2`, iconos `@shopify/polaris-icons@9.3.1`, especificación UX/UI V1 congelada.
**Propósito:** Inventariar cada patrón de Polaris, su equivalente en Moon (referencia de patrón móvil únicamente), su estado real en `core/ui`/`feature/destinations/presentation`, y clasificar como COMPLETE / PARTIAL / MISSING / INCONSISTENT / NOT_APPLICABLE.

## Leyenda de clasificación

| Clasificación | Significado |
|---|---|
| COMPLETE | Implementado, consumiendo solo tokens semánticos, con pruebas y cobertura de accesibilidad. |
| PARTIAL | Existe el componente pero con carencias (faltan estados, tema oscuro, accesibilidad, pruebas, o usa valores fuera de tokens). |
| MISSING | No existe implementación en `core/ui`. |
| INCONSISTENT | Comportamiento o apariencia divergente de Polaris o de la especificación UX/UI V1. |
| NOT_APPLICABLE | No corresponde al producto Agenda QR (justificación de producto, no por falta de implementación). |

## Columna de decisiones técnicas

- `Ruta`: ruta de implementación actual en el repositorio.
- `Polaris`: nombre oficial del componente en Polaris.
- `Moon`: equivalente o patrón móvil en Moon Design (solo referencia de interacción, nunca identidad visual ni dependencia).
- `Android`: cobertura en androidHost (JVM/Robolectric).
- `iOS`: estado de validación en iOS (NO VERIFICADO si no se ha ejecutado en Xcode/simulador).
- `Accesibilidad`: WCAG AA, semántica, targets 48 dp, lectores de pantalla.
- `Pruebas`: pruebas unitarias / Compose UI existentes.
- `Dependencias`: dependencias del componente.

---

## Familia A — Navegación

| # | Patrón | Ruta de implementación | Estado real | Android | iOS | Accesibilidad | Pruebas | Carencias | Decisión técnica | Prioridad | Dependencias |
|---|---|---|---|---|---|---|---|---|---|---|---|
| A1 | Navigation Bar (bottom) | `core/ui/.../AqChrome.kt` → `AqNavBar` | COMPLETE | Sí | NO VERIFICADO | Sí (rol Tab, targets 48 dp) | `RootNavigationChromeTest`, `ScreensA11yCoverageTest` | — | UX V1 define cuatro destinos habilitados; selección y cambio de ruta están probados | Alta | `AqIcon`, `AqColor`, `AqMetrics` |
| A2 | Navigation Rail | `core/ui/.../AqChrome.kt` → `AqNavRail` | COMPLETE | Sí | NO VERIFICADO | Sí (Role.Tab, selección, fuente 200 %) | `AqNavRailLayoutTest`, `ScreensA11yCoverageTest` | — | Layout a 720 dp y etiquetas largas comprobados con escala 200 %; render iOS sigue sin runtime | Media | `AqIcon`, `AqColor` |
| A3 | Modal Navigation Drawer | — | NOT_APPLICABLE | — | — | — | — | UX V1 define navegación inferior (barra) y rail en tablet, no drawer lateral | No implementar; UX V1 lo descarta | — | — |
| A4 | Permanent Navigation Drawer | — | NOT_APPLICABLE | — | — | — | — | UX V1 define navegación inferior (barra) y rail en tablet, no drawer permanente | No implementar; UX V1 lo descarta | — | — |
| A5 | Contextual Bar | — | NOT_APPLICABLE | — | — | — | — | UX V1 no contempla selección múltiple ni acciones contextuales en la barra superior | `AqStatusBanner` cubre feedback; el patrón contextual no corresponde al producto | Baja | `AqTone`, `AqIcon` |
| A6 | Top App Bar | `core/ui/.../AqChrome.kt` → `AqTopBar` | COMPLETE | Sí | NO VERIFICADO | Sí (heading, contentDescription) | `ScreensA11yCoverageTest`, `LayoutReferenceTest` | — | Máximo dos acciones visibles y overflow; disparador de búsqueda separado según contrato | Alta | `AqIcon`, `AqActionSheet` |
| A7 | Bottom App Bar | `core/ui/.../AqScaffold.kt` → `AqBottomActionDock` + `AqBottomAction` | COMPLETE | Sí | NO VERIFICADO | Sí | `LayoutReferenceTest` | — | Una primaria fija por pantalla, como dispone UX V1 | Alta | `AqPrimaryButton`, `AqScreenScaffold` |
| A8 | Breadcrumbs | — | NOT_APPLICABLE | — | — | — | — | UX V1 no usa breadcrumbs; navegación por back stack y rutas | No implementar | — | — |
| A9 | Navigation Tabs | `core/ui/.../AqTabs.kt` → `AqTabs` | COMPLETE | Sí | NO VERIFICADO | Sí (Role.Tab, selected, target proporcionado por Tab de Compose) | `AqTabsLayoutTest` | — | Títulos de texto según S06; scroll, selección y escala 100/150/200 % verificados | Media | `PrimaryScrollableTabRow` (M3 puente) |
| A10 | Tab Bar | `core/ui/.../AqButtons.kt` → `AqSegmentedControl` | COMPLETE | Sí | NO VERIFICADO | Sí (rol RadioButton, selectableGroup) | `ScreensA11yCoverageTest`, `DestinationsScreenA11yTest` | — | Segmentos cortos de selección única según UX V1; pestañas scrollables son otro patrón | Alta | `AqIcon`, `AqTone` |
| A11 | Back Navigation | `AqTopBar(onBack=...)`, `PredictiveBackHandler` (host) | PARTIAL | Sí | NO VERIFICADO | Sí | `RootNavigationChromeTest` | Predictive back compilado pero sin verificación en dispositivo (ADR-0007) | Conservar back nativo; documentar iOS como no verificado | Alta | Host de plataforma |
| A12 | Overflow Menu | `AqActionSheet` (interno, vía `LocalAqActionSheetController`) | COMPLETE | Sí | NO VERIFICADO | Sí | `AqActionSheetTest` | — | Overflow móvil como diálogo de lista; no requiere anclaje para este flujo | Media | `AqDialog`, `AqDialogList` |
| A13 | Navigation Badges | — | NOT_APPLICABLE | — | — | — | — | `AqNavBar` y `AqNavRail` no muestran conteos; UX V1 no declara notificaciones pendientes en navegación | Los conteos existentes pertenecen a filtros/segmentos, no a destinos de navegación | — | — |
| A14 | Floating Action Button | — | NOT_APPLICABLE | — | — | — | — | UX V1.9 elimina FAB en S01; acción primaria fija en `AqBottomAction` | No implementar FAB; UX V1 lo descarta | — | — |
| A15 | Extended FAB | `core/ui/.../AqChrome.kt` → `AqExtendedFab` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Reutiliza `AqButton` primaria; sin cambios | Media | `AqButton` |

### Notas familiares A

- **Moon Design:** Moon ofrece `BottomNavigation`, `NavigationRail`, `Drawer`, `TopAppBar` como patrones móviles. Se estudian para confirmar que la navegación inferior con 4 destinos y el riel en tablet son patrones válidos. No se introduce código Dart ni dependencia Moon.
- **Polaris:** Polaris web no define navegación táctil móvil equivalente; se aplica D-03 (barra inferior y gestos nativos) y D-04 (bottom sheet nativo).
- **Riesgo arquitectónico:** `AqNavRail` tiene verificación Robolectric a 720 dp/200 %; render y accesibilidad VoiceOver en iOS siguen NO VERIFICADOS.

---

## Familia B — Botones y acciones

| # | Patrón | Ruta de implementación | Estado real | Android | iOS | Accesibilidad | Pruebas | Carencias | Decisión técnica | Prioridad | Dependencias |
|---|---|---|---|---|---|---|---|---|---|---|---|
| B1 | Primary Button | `AqPrimaryButton` | COMPLETE | Sí | NO VERIFICADO | Sí (Role.Button, 48 dp) | `AqButtonStyleTest`, `ScreensA11yCoverageTest` | — | Relleno `brandFill` corregido (F1-F6) | Alta | `AqButtonImpl` |
| B2 | Secondary Button | `AqSecondaryButton` | COMPLETE | Sí | NO VERIFICADO | Sí | `AqButtonStyleTest` | Variante `solid` mapeada a Primary (documentada en contrato) | Mantener; v8.3 añadió borde fino del secundario | Alta | `AqButtonImpl` |
| B3 | Tertiary Button | `AqTextAction` (Default/Neutral) | COMPLETE | Sí | NO VERIFICADO | Sí (Role.Button, 48 dp) | `AqTextActionTest`, `ScreensA11yCoverageTest` | — | Acción de texto con prioridad visual terciaria; no requiere clase duplicada | Media | `AqTextAction` |
| B4 | Destructive Button | `AqDangerButton` | COMPLETE | Sí | NO VERIFICADO | Sí | `AqButtonStyleTest`, `ActionHierarchyTest` | Requiere confirmación en diálogo (destructive=true en `AqDialog`) | Mantener; confirmación impuesta por `AqDialog` | Alta | `AqButtonImpl`, `AqDialog` |
| B5 | Plain Button | `AqTextAction` (Neutral) | COMPLETE | Sí | NO VERIFICADO | Sí (Role.Button, 48 dp) | `AqTextActionTest` | — | Variante Neutral reutiliza la API común con estado deshabilitado probado | Media | `AqTextAction` |
| B6 | Icon Button | `AqIconButton` | COMPLETE | Sí | NO VERIFICADO | Sí (contentDescription, 48 dp) | `AqAccessibilityContractTest` | — | Impone target 48 dp sobre el llamador (P-04 aceptado) | Alta | `AqIcon` |
| B7 | Circular Icon Button | `AqIconButton` | COMPLETE | Sí | NO VERIFICADO | Sí | `AqAccessibilityContractTest` | El glifo es circular por el contenedor de 48 dp; no hay variante de tamaño visual distinto | Mantener | Alta | `AqIcon` |
| B8 | Split Button | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara split button | No implementar | — | — |
| B9 | Button Group | `AqButtonGroup` | COMPLETE | Sí | NO VERIFICADO | Parcial | `ScreensA11yCoverageTest` | Máximo una acción enfatizada; sin prueba dedicada de grupo | Añadir prueba de énfasis único | Media | `AqPrimaryButton`, `AqSecondaryButton` |
| B10 | Segmented Button | `AqSegmentedControl` | COMPLETE | Sí | NO VERIFICADO | Sí (rol RadioButton) | `ScreensA11yCoverageTest`, `DestinationsScreenA11yTest` | — | Selección única requerida por el patrón Todos/Favoritos de UX V1 | Alta | `AqSegmentedOption` |
| B11 | Toggle Button | — | NOT_APPLICABLE | — | — | — | — | UX V1 solo usa toggle especializado de favorito y `AqSwitch` para ajustes | No crear toggle genérico sin caso de producto | Media | `AqFavoriteToggle`, `AqSwitch` |
| B12 | Floating Action Button | — | NOT_APPLICABLE | — | — | — | — | UX V1 descarta FAB | No implementar | — | — |
| B13 | Extended FAB | `AqExtendedFab` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Reutiliza `AqButton` primaria | Media | `AqButton` |
| B14 | Link Button | `AqLink` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Delegado a `AqTextAction` | Sin cambios | Media | `AqTextAction` |
| B15 | Loading Button | `AqButton(isLoading=...)` | COMPLETE | Sí | NO VERIFICADO | Sí (stateDescription) | `AqButtonStyleTest` | Indicador circular interno (Material `CircularProgressIndicator` como puente técnico) | Mantener; el indicador es interno, no decorativo | Alta | `AqButtonImpl` |
| B16 | Full Width Button | `AqPrimaryButton(modifier = Modifier.fillMaxWidth())` | COMPLETE | Sí | NO VERIFICADO | Sí | `LayoutReferenceTest` | Logrado vía modifier del llamador, no vía parámetro | No añadir parámetro dedicado | Media | `AqPrimaryButton` |

### Notas familiares B

- **Polaris:** Botones planos (Primary = `bg-fill-brand` #303030 claro / blanco oscuro), Secondary con borde 1 dp (`shadow-button` de light-mobile), Danger = `dangerFill`. Los estados hover/pressed se resuelven por cambio de color de token, sin ripple.
- **Moon:** Moon ofrece botones con estados pressed/focus/disabled y haptica. Se confirma que el objetivo táctil de 48 dp y el feedback de color activo (sin ripple) son patrones móviles válidos.
- **Riesgo:** `CircularProgressIndicator` y `IconButton`/`Text` de Material 3 se usan como puente técnico internos; todos los valores visuales vienen de tokens Polaris.

---

## Familia C — Formularios y entradas

| # | Patrón | Ruta de implementación | Estado real | Android | iOS | Accesibilidad | Pruebas | Carencias | Decisión técnica | Prioridad | Dependencias |
|---|---|---|---|---|---|---|---|---|---|---|---|
| C1 | Text Field | `AqTextField` | COMPLETE | Sí | NO VERIFICADO | Sí (error(), contentDescription, liveRegion) | `AqAccessibilityContractTest`, `ScreensA11yCoverageTest` | — | UX V1 no establece límite de caracteres; multilineal tiene API propia en C3 | Alta | `BasicTextField` (puente) |
| C2 | Search Field | `AqSearchField` | COMPLETE | Sí | NO VERIFICADO | Sí (contentDescription, clearLabel) | `ScreensA11yCoverageTest` | — | UX V1 no declara sugerencias; `autoFocus` solo al entrar en Buscar | Alta | `BasicTextField`, `AqIconButton` |
| C3 | Multiline Text Field | `core/ui/.../AqInputs.kt` → `AqMultilineTextField` | COMPLETE | Sí | NO VERIFICADO | Sí (error(), contentDescription, liveRegion) | `AqMultilineTextFieldTest` | — | Usado en S02 y S07 para notas; crece con contenido y escala | Alta | `BasicTextField` (puente) |
| C4 | Numeric Input | `AqAmountField` | COMPLETE | Sí | NO VERIFICADO | Sí (contentDescription, clearLabel) | `UxAmountValidationTest` | — | El teclado Decimal se configura en el consumidor; la validación permanece en dominio | Alta | `BasicTextField`, `AqIconButton` |
| C5 | Amount Input | `AqAmountField` | COMPLETE | Sí | NO VERIFICADO | Sí | `UxAmountValidationTest` | — | Cifras tabulares y símbolo monetario opcional están cubiertos por la API | Alta | `AqAmountField` |
| C6 | Password Input | — | NOT_APPLICABLE | — | — | — | — | UX V1 no recoge contraseñas; el PIN local es numérico y no es una contraseña | No implementar; capacidad fuera del producto V1 | — | — |
| C7 | OTP Input | — | NOT_APPLICABLE | — | — | — | — | Agenda QR usa PIN local (C8), no códigos OTP de un solo uso | No duplicar el componente PIN con un patrón sin flujo de producto | Alta | `AqPinDots`, `AqKeypad` |
| C8 | PIN Input | `AqPinDots` + `AqKeypad` | COMPLETE | Sí | NO VERIFICADO | Sí | `LocalAccessGateTest`, `AqAccessibilityContractTest` | — | Cifras enmascaradas, teclado 3×4, borrado accesible | Alta | `AqKeypad` |
| C9 | Date Picker | `AqDatePickerDialog` (`AqDialog` + Material `DatePicker` puente) | PARTIAL | Sí | NO VERIFICADO | Parcial | `AqDateTimePickerTest` | El contenedor, acciones, radio y tokens siguen Polaris; el calendario sigue siendo M3 y no replica su anatomía visual | Mantener puente por ahora; reemplazar el cuerpo solo con especificación de calendario y prueba multiplataforma | Media | `AqDialog`, `DatePicker` (puente M3) |
| C10 | Time Picker | `AqTimePickerDialog` (`AqDialog` + Material `TimeInput` puente) | PARTIAL | Sí | NO VERIFICADO | Parcial | `AqDateTimePickerTest` | Contenedor Polaris; el cuerpo numérico conserva anatomía M3 | Mantener puente por ahora; reemplazar el cuerpo solo con especificación de entrada accesible y prueba multiplataforma | Media | `AqDialog`, `TimeInput` (puente M3) |
| C11 | Date Range Picker | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara rango de fechas | No implementar | — | — |
| C12 | Searchable Select | — | NOT_APPLICABLE | — | — | — | — | Los selectores UX V1 tienen listas pequeñas, sin búsqueda | `AqSelect` cubre selección cerrada; no se requiere búsqueda incrustada | Media | `AqSelect` |
| C13 | Combobox | — | NOT_APPLICABLE | — | — | — | — | UX V1 no permite opciones de texto libre ni sugiere valores | No añadir combobox sin requerimiento de producto | Media | `AqSelect` |
| C14 | Autocomplete | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara sugerencias/autocompletado | No implementar sin caso de producto | — | — |
| C15 | Dropdown | `AqSelect` | COMPLETE | Sí | NO VERIFICADO | Sí (Role.DropdownList, target 48 dp, opciones radio) | `AqSelectTest` | — | Selector móvil modal adecuado para listas cortas y probado en Android | Media | `AqSelect` |
| C16 | Multi Select | `AqCheckboxGroup` + `AqSelectAllCheckbox` | COMPLETE | Sí | NO VERIFICADO | Sí (rol Checkbox y estado mixto semántico) | `AqSelectAllCheckboxTest`, `ScreensA11yCoverageTest` | — | El estado mixto se deriva con `AqTriState`; el consumidor define la acción al activar | Media | `AqCheckbox`, `AqTriState` |
| C17 | Checkbox | `AqCheckbox` | COMPLETE | Sí | NO VERIFICADO | Sí (Role.Checkbox, 48 dp) | `AqAccessibilityContractTest` | — | Sin margen externo; grupo posee separación | Alta | `Checkbox` (puente M3) |
| C18 | Radio Button | `AqRadioButton` | COMPLETE | Sí | NO VERIFICADO | Sí (Role.RadioButton, 48 dp) | `AqAccessibilityContractTest` | — | `AqChoiceGroup` para selección mutuamente excluyente | Alta | `RadioButton` (puente M3) |
| C19 | Switch | `AqSwitch` | COMPLETE | Sí | NO VERIFICADO | Sí (Role.Switch, 48 dp) | `ScreensA11yCoverageTest` | — | Ajustes de efecto inmediato según UX V1 | Media | `Switch` (puente M3) |
| C20 | Slider | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara slider | No implementar | — | — |
| C21 | Stepper | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara entradas numéricas incrementales | No implementar sin caso de producto | — | — |
| C22 | Quantity Selector | — | NOT_APPLICABLE | — | — | — | — | No aplica a Agenda QR (no carrito de compras) | No implementar | — | — |
| C23 | Input Group | `AqChoiceGroup` / `AqCheckboxGroup` | COMPLETE | Sí | NO VERIFICADO | Sí (mensaje único por grupo) | `ScreensA11yCoverageTest` | — | Agrupación y copy de error compartido; sin borde ornamental no exigido por UX V1 | Media | `AqRadioButton`/`AqCheckbox` |
| C24 | Fieldset | `AqChoiceGroup` / `AqCheckboxGroup` | COMPLETE | Sí | NO VERIFICADO | Sí (grupo semántico de selección con etiqueta visible) | `AqAccessibilityContractTest`, `ScreensA11yCoverageTest` | — | Las opciones se agrupan semánticamente; UX V1 no exige borde de fieldset | Media | `AqChoiceGroup`, `AqCheckboxGroup` |
| C25 | Form Section | `AqSection` | COMPLETE | Sí | NO VERIFICADO | Parcial | — | Encabezado + contenido con espaciado del sistema | Sin cambios | Media | `AqSectionHeader` |
| C26 | Validation Message | `AqFieldMessages` + `error()` semántica | COMPLETE | Sí | NO VERIFICADO | Sí (error(), liveRegion polite) | `AqAccessibilityContractTest` | Icono + texto bajo el campo, nunca solo color | Sin cambios | Alta | `AqText`, `AqIcon` |
| C27 | Helper Text | `AqFieldMessages` (helperMessage) | COMPLETE | Sí | NO VERIFICADO | Sí | `AqAccessibilityContractTest` | 4 dp de separación del control | Sin cambios | Alta | `AqText` |

### Notas familiares C

- **Polaris:** Campos con etiqueta persistente, caja `input-bg-surface`, borde 1 dp `functionalBorder`, radio Medium (8 dp, v8.3), error con icono+texto, foco con `border-focus`.
- **Moon:** Moon ofrece inputs con estados focused/error/disabled, clear action, password toggle. Se confirma que error con icono+texto y clear action etiquetada son patrones móviles válidos.
- **Puentes M3:** `BasicTextField`, `Checkbox`, `RadioButton`, `Switch`, `DatePickerDialog`, `AlertDialog`, `TimeInput` se usan como infraestructura técnica; todos los valores visuales (colores, radios, espaciado) vienen de tokens Polaris.
- **Riesgo:** Date/Time picker usan puentes M3 no totalmente Polaris; documentado como deuda controlada.

---

## Familia D — Selección y filtros

| # | Patrón | Ruta de implementación | Estado real | Android | iOS | Accesibilidad | Pruebas | Carencias | Decisión técnica | Prioridad | Dependencias |
|---|---|---|---|---|---|---|---|---|---|---|---|
| D1 | Filter Chips | `AqFilterChip` | COMPLETE | Sí | NO VERIFICADO | Sí (Role.Button, selected, etiqueta y conteo textual, 48 dp) | `AqFilterChipTest`, `DestinationsScreenA11yTest` | — | La selección y los conteos visibles están anunciados sin rol redundante para el conteo | Media | `AqBadge`, `AqTonalTag` |
| D2 | Choice List | `AqChoiceGroup` / `AqDialogList` + `AqRadioButton` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Disponible en línea y en diálogo; S08 usa diálogo para la selección contextual | Media | `AqDialog`, `AqDialogList` |
| D3 | Selectable Cards | `AqTappableCard` + `AqListRow(selected=...)` | COMPLETE | Sí | NO VERIFICADO | Sí (selected semántica, fill y borde) | `ScreensA11yCoverageTest` | — | Estado seleccionado no depende solo del color | Media | `AqListRow`, `AqTappableCard` |
| D4 | Multi Select | `AqCheckboxGroup` + `AqSelectAllCheckbox` | COMPLETE | Sí | NO VERIFICADO | Sí (rol Checkbox y estado mixto semántico) | `AqSelectAllCheckboxTest`, `ScreensA11yCoverageTest` | — | Selección parcial representada con `TriStateCheckbox`; acción definida por el consumidor | Media | `AqCheckbox`, `AqTriState` |
| D5 | Segmented Control | `AqSegmentedControl` | COMPLETE | Sí | NO VERIFICADO | Sí (rol RadioButton) | `DestinationsScreenA11yTest` | — | El filtrado requiere selección única | Alta | `AqSegmentedOption` |
| D6 | Filter Bar | `AqSegmentedControl` (S01 Todos/Favoritos) | COMPLETE | Sí | NO VERIFICADO | Sí | `DestinationsScreenA11yTest` | — | El filtro se segmenta; la búsqueda de texto vive en S04 | Alta | `AqSegmentedControl` |
| D7 | Filter Popover | `AqDialog` + `AqDialogList` | COMPLETE | Sí | NO VERIFICADO | Sí | `AqActionSheetTest` | — | Filtros móviles se presentan como diálogo modal en lugar de popover anclado | Media | `AqDialog` |
| D8 | Sort Selector | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara ordenamiento por el usuario | No implementar | — | — |
| D9 | Date Range Filter | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara filtro de rango de fechas | No implementar | — | — |
| D10 | Active Filter Summary | — | NOT_APPLICABLE | — | — | — | — | S01 usa filtro segmentado con conteo y no acumula filtros activos simultáneos | El segmento existente cubre el flujo V1; no añadir resumen separado | — | `AqSegmentedControl` |
| D11 | Clear Filters | `AqTextAction` (Descartar) | COMPLETE | Sí | NO VERIFICADO | Sí | `ActionHierarchyTest` | — | Acción clara y accesible de texto; no requiere superficie de chip | Media | `AqTextAction` |
| D12 | Search Suggestions | — | NOT_APPLICABLE | — | — | — | — | UX V1 no requiere sugerencias en línea | No implementar sin caso de producto | — | `AqSearchField` |

### Notas familiares D

- **Polaris:** Filtros como chips, segmento mutuamente excluyente, elección mediante diálogo de radio.
- **Moon:** Moon ofrece chips filtráveis, segmented control, select con múltiples selecciones. Se confirma que chips con conteo y segmento son patrones válidos.
- **Riesgo:** No hay popover anclado ni filtros de rango; UX V1 no los requiere.

---

## Familia E — Superficies y overlays

| # | Patrón | Ruta de implementación | Estado real | Android | iOS | Accesibilidad | Pruebas | Carencias | Decisión técnica | Prioridad | Dependencias |
|---|---|---|---|---|---|---|---|---|---|---|---|
| E1 | Card | `AqCard` | COMPLETE | Sí | NO VERIFICADO | Sí | `AqUiPolishContractTest`, `LayoutReferenceTest` | Radio 12 dp (v8.3), sombra anulada en light-mobile (D-10) | Sin cambios | Alta | `AqElevation`, `AqShapes` |
| E2 | Modal | `AqDialog` (shape ExtraLarge 16 dp) | COMPLETE | Sí | NO VERIFICADO | Sí (paneTitle; foco destructivo en Cancelar probado) | `AqDialogAccessibilityTest`, `AccessibilityStatesFormsTest`, `LayoutReferenceTest` | Retención/restauración de foco delegado al `Dialog` de Compose; iOS no verificado | Mantener diálogo nativo con título de panel semántico | Alta | `AqDialog` |
| E3 | Dialog | `AqDialog` | COMPLETE | Sí | NO VERIFICADO | Sí (paneTitle, acciones etiquetadas, contenido largo desplazable) | `AqDialogAccessibilityTest`, `LayoutReferenceTest` | — | Altura acotada a la ventana; contenido y acciones permanecen accesibles mediante scroll | Alta | `AqDialogList` |
| E4 | Confirmation Dialog | `AqDialog(destructive=...)` | COMPLETE | Sí | NO VERIFICADO | Sí (focus inicial en dismiss) | `ContextPickerDialogTest`, `ActionHierarchyTest` | — | Foco inicial en acción conservativa cuando destructive | Alta | `AqDialog` |
| E5 | Alert Dialog | `AqDialog` (confirm/dismiss) | COMPLETE | Sí | NO VERIFICADO | Sí (paneTitle, acciones etiquetadas; destructivo distingue peligro) | `ScreensA11yCoverageTest`, `ActionHierarchyTest` | — | Alerta y confirmación comparten anatomía; el estado destructivo aporta la diferencia funcional | Media | `AqDialog` |
| E6 | Bottom Sheet | `AqBottomSheet` (D-04) | COMPLETE | Sí | NO VERIFICADO | Sí (paneTitle, cierre explícito, gesto y restauración de foco probados en Android) | `AqBottomSheetTest` | — | Modal nativo de Compose; iOS/VoiceOver permanecen sin validación de runtime | Media | `AqBottomSheet`, `Dialog` |
| E7 | Modal Bottom Sheet | `AqBottomSheet` | COMPLETE | Sí | NO VERIFICADO | Sí (paneTitle, acción explícita, gesto probado en Android) | `AqBottomSheetTest` | — | El patrón requerido es modal; `expanded` controla su presentación | Media | `AqBottomSheet` |
| E8 | Side Sheet | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara side sheet; navegación por barra inferior y rail | No implementar | — | — |
| E9 | Drawer | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara drawer de navegación | No implementar | — | — |
| E10 | Popover | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara contenido flotante anclado; los selectores y acciones usan diálogos | Mantener `AqDialog`/`AqActionSheet`; no hace falta overlay anclado | — | `AqDialog` |
| E11 | Tooltip | `core/ui/.../AqTooltip.kt` → `AqTooltip` | COMPLETE | Sí | NO VERIFICADO | Sí (nombre accesible conservado, 48 dp del control) | `AqTooltipTest` | — | Estado controlado por consumidor con callback opcional de pulsación larga | Baja | `AqIcon`, `AqText` |
| E12 | Context Menu | `AqActionSheet` (vía `LocalAqActionSheetController`) | COMPLETE | Sí | NO VERIFICADO | Sí | `AqActionSheetTest` | — | Acciones contextuales como hoja modal en móvil, sin menú anclado | Media | `AqDialog`, `AqDialogList` |
| E13 | Dropdown Menu | `AqActionSheet` | COMPLETE | Sí | NO VERIFICADO | Sí | `AqActionSheetTest` | — | Menú de opciones con presentación modal consistente para interacción táctil | Media | `AqDialog` |
| E14 | Overflow Menu | `AqActionSheet` | COMPLETE | Sí | NO VERIFICADO | Sí | `AqActionSheetTest` | — | Overflow móvil como diálogo de acciones; UX V1 no requiere submenús | Alta | `AqActionSheet`, `AqTopBarAction` |
| E15 | Expandable Panel | `core/ui/.../AqLists.kt` → `AqExpansionPanel` | COMPLETE | Sí | NO VERIFICADO | Sí (Role.Button, expanded semantics) | `AqExpansionPanelTest` | — | Radio Large (12 dp), superficie+borde tokens, chevron arriba/abajo | Media | `AqIcon`, `AqTextStyles` |
| E16 | Accordion | — | NOT_APPLICABLE | — | — | — | — | UX V1 solo necesita expansión de panel individual; ya existe `AqExpansionPanel` (E15) | Reutilizar panel individual; no añadir un acordeón coordinado sin caso de producto | — | `AqExpansionPanel` |

### Notas familiares E

- **Polaris:** Modal (radio 16 dp), dialog (radio 12 dp para tarjeta/popover en web, aquí 16 dp por v8.3), bottom sheet nativo (D-04).
- **Moon:** Moon ofrece bottom sheet con gestos de dismiss, modal, tooltip, popover. Se estudian para interacción táctil; no se copia identidad visual ni código Dart.
- **Riesgo arquitectónico:** `AqBottomSheet` tiene gesto de dismiss por arrastre hacia abajo y estados expanded/collapsed, pero sin trampa de foco explícita. `AqDialog` no tiene trampa de foco explícita (el foco inicial se gestiona con `FocusRequester` en destructive). `AqTooltip` existe como componente controlado por estado (el disparo por pulsación larga lo gestiona el llamador). Tooltip y popover anclado: UX V1 no los declara como requisito.
- **Puentes:** `Dialog` de Compose Multiplatform se usa como infraestructura para modales/hojas.

---

## Familia F — Información e identidad

| # | Patrón | Ruta de implementación | Estado real | Android | iOS | Accesibilidad | Pruebas | Carencias | Decisión técnica | Prioridad | Dependencias |
|---|---|---|---|---|---|---|---|---|---|---|---|
| F1 | Avatar | — | NOT_APPLICABLE | — | — | — | — | UX V1 no muestra personas o cuentas con avatar; contextos se identifican con color de dominio | No añadir avatar de perfil sin identidad de usuario en producto | Baja | `AqContextBadge` |
| F2 | Avatar Group | — | NOT_APPLICABLE | — | — | — | — | UX V1 no presenta grupos de usuarios/avatar | No implementar sin caso de producto | — | — |
| F3 | Badge | `AqBadge` (outline), `AqTonalTag` (relleno tonal) | COMPLETE | Sí | NO VERIFICADO | Sí (texto, nunca solo color) | `AqButtonStyleTest`, `AqSchemeTest` | — | Par texto/contenedor garantizado a AA | Alta | `AqTone`, `AqShapes` |
| F4 | Status Badge | `AqTonalTag` + `AqStatusPill` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | `AqStatusPill` inversa para "Modo local" | Sin cambios | Alta | `AqTone`, `AqColor` |
| F5 | Icon Badge | `AqIconBadge` | COMPLETE | Sí | NO VERIFICADO | Parcial | `ScreensA11yCoverageTest` | Glifo decorativo; texto de fila comunica el tipo | Sin cambios | Alta | `AqIcon`, `AqTone` |
| F6 | Banner | `AqStatusBanner` | COMPLETE | Sí | NO VERIFICADO | Sí (liveRegion polite) | `AqStatusBannerLayoutTest` | Acción principal + descarte; tono con par content/container garantizado | Sin cambios | Alta | `AqTone`, `AqButton` |
| F7 | Inline Alert | `AqStatusBanner` (tone=Danger/Warning/Info) | COMPLETE | Sí | NO VERIFICADO | Sí | `AqStatusBannerLayoutTest` | Misma implementación que banner; tono distingue severidad | Sin cambios | Alta | `AqStatusBanner` |
| F8 | Callout | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara callout | No implementar | — | — |
| F9 | Tooltip | `AqTooltip` (ver E11) | COMPLETE | Sí | NO VERIFICADO | Sí (nombre accesible conservado en el control) | `AqTooltipTest` | — | El llamador controla visibilidad y puede enlazarla al callback de pulsación larga | Baja | `AqTooltip` |
| F10 | Metadata | `AqMetaRow` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Label + valor + acción final opcional | Sin cambios | Alta | `AqText`, `AqIcon` |
| F11 | Description List | `AqMetaRow` en detalles de operación/destino | COMPLETE | Sí | NO VERIFICADO | Sí (etiqueta y valor visibles como textos consecutivos) | `AqMetaRowTest`, `LayoutReferenceTest` | — | Filas compactas de término/valor adaptadas a pantallas pequeñas | Media | `AqMetaRow` |
| F12 | Key Value Pair | `AqMetaRow` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Sin cambios | Alta | `AqMetaRow` |
| F13 | User Identity | — | NOT_APPLICABLE | — | — | — | — | UX V1 no incluye cuentas de usuario, perfiles ni roles visibles | No crear identidad de usuario sin funcionalidad de cuenta | Baja | — |
| F14 | Empty State | `AqEmptyState` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest`, `LayoutReferenceTest` | Glifo decorativo (no anunciado), título, subtítulo, acción primaria opcional | Sin cambios | Alta | `AqIcon`, `AqPrimaryButton` |

### Notas familiares F

- **Polaris:** Badge con borde o relleno tonal, par texto/contenedor AA. Estado vacío con glifo, título, subtítulo, acción.
- **Moon:** Moon ofrece badges, avatars, status indicators, empty states. Se confirma que badge con texto y estado vacío con acción son patrones válidos.
- **Riesgo:** No hay avatar con iniciales de usuario ni avatar group; UX V1 no los requiere en V1.

---

## Familia G — Listas y datos

| # | Patrón | Ruta de implementación | Estado real | Android | iOS | Accesibilidad | Pruebas | Carencias | Decisión técnica | Prioridad | Dependencias |
|---|---|---|---|---|---|---|---|---|---|---|---|
| G1 | Resource List | `AqListBody` + `AqListRow` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest`, `LayoutReferenceTest` | Filas con título, subtítulo, meta, trailing, showDivider | Sin cambios | Alta | `AqListRow`, `AqListBody` |
| G2 | Resource Item | `AqListRow` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Celda con acento de contexto, selección, chevron | Sin cambios | Alta | `AqListRow` |
| G3 | Index Table | `AqIndexTable` | COMPLETE | Sí | NO VERIFICADO | Parcial | `ScreensA11yCoverageTest` | Cabecera con semántica de encabezado, filas con hairlines | Sin cambios | Media | `AqIndexTable` |
| G4 | Data Table | `AqIndexTable` | COMPLETE | Sí | NO VERIFICADO | Sí (encabezados jerárquicos, filas de contenido) | `AqPhase3ContractTest.index_table_renders_headers_and_rows_with_injected_copy` | — | Tabla de solo lectura; UX V1 no requiere ordenamiento ni selección de filas | Media | `AqIndexTable` |
| G5 | List Row | `AqListRow` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Sin cambios | Alta | `AqListRow` |
| G6 | Selectable List | `AqListRow(selected=...)` + `AqSegmentedControl` | COMPLETE | Sí | NO VERIFICADO | Sí (selected semántico, fondo y borde de selección) | `DestinationsScreenA11yTest` | — | Selección por fila comunicada con semántica y forma, no solo color | Alta | `AqListRow` |
| G7 | Swipeable List Item | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara acciones por deslizamiento en filas | Mantener acciones visibles/accesibles; no añadir gestos como única vía | — | — |
| G8 | Timeline | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara una vista de línea temporal | No implementar sin caso de producto | — | — |
| G9 | Activity Feed | `AqListRow` en `OperationListScreen` | COMPLETE | Sí | NO VERIFICADO | Sí | `LayoutReferenceTest` | — | El historial de operaciones es una lista móvil cronológica con estado por fila | Alta | `AqListRow`, `AqTonalTag` |
| G10 | Pagination | `AqLoadMoreFooter` | COMPLETE | Sí | NO VERIFICADO | Sí (liveRegion polite) | `ScreensA11yCoverageTest` | — | Paginación móvil por lotes de 50 con acción explícita; sin controles numéricos | Media | `AqLoadMoreFooter` |
| G11 | Infinite Scrolling | — | NOT_APPLICABLE | — | — | — | — | UX V1 exige carga bajo demanda explícita, no carga infinita automática | `AqLoadMoreFooter` implementa el patrón aprobado sin scroll inesperado | Media | `AqLoadMoreFooter` |
| G12 | Pull to Refresh | — | NOT_APPLICABLE | — | — | — | — | UX V1 no asigna refresco manual por gesto; la carga se solicita explícitamente | No implementar sin caso de producto | — | — |
| G13 | Empty List | `AqEmptyState` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Sin cambios | Alta | `AqEmptyState` |
| G14 | Loading List | `AqSkeleton` / `AqLoading` | COMPLETE | Sí | NO VERIFICADO | Sí (carga comunicada por `AqLoading`; skeleton decorativo) | `ScreensA11yCoverageTest` | — | Skeleton estático evita movimiento decorativo; mensaje opcional anuncia la carga | Media | `AqSkeleton`, `AqLoading` |
| G15 | Error List | `AqEmptyState` + error en `AqStatusBanner` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Estado de error ofrece copy y recuperación contextual | Media | `AqEmptyState`, `AqStatusBanner` |
| G16 | Grouped List | `AqSection` + `AqSectionHeader` + `AqListBody` | COMPLETE | Sí | NO VERIFICADO | Sí (encabezado jerárquico) | `LayoutReferenceTest` | — | Secciones agrupadas sin sticky header, que UX V1 no requiere | Media | `AqSection`, `AqSectionHeader` |
| G17 | Sticky Section Header | — | NOT_APPLICABLE | — | — | — | — | Las pantallas V1 no requieren encabezados pegajosos | Usar encabezados normales de `AqSection` | — | `AqSectionHeader` |

### Notas familiares G

- **Polaris:** Lista de recursos con filas, tabla de índices, lista agrupada con encabezados.
- **Moon:** Moon ofrece listas deslizables, pull-to-refresh, selección múltiple. Se confirma que swipe-to-actions no es necesario si hay alternativas accesibles (UX V1: gestos nunca son la única forma).
- **Riesgo:** No hay swipeable list item, pull-to-refresh, timeline, sticky header. UX V1 no los declara; gestos no son la única forma de acción.

---

## Familia H — Contenido multimedia

| # | Patrón | Ruta de implementación | Estado real | Android | iOS | Accesibilidad | Pruebas | Carencias | Decisión técnica | Prioridad | Dependencias |
|---|---|---|---|---|---|---|---|---|---|---|---|
| H1 | QR Preview | `AqQrPreview` | COMPLETE | Sí | NO VERIFICADO | Sí (descripción acordada P-02; acciones en AqQrSheet) | `QrPreviewContractTest`, `QrFullscreenTest` | — | Decodificación multiplataforma; copia y pantalla completa pertenecen a la hoja QR | Alta | `decodeImageBitmap` (expect/actual) |
| H2 | Image Preview | — | NOT_APPLICABLE | — | — | — | — | UX V1 solo previsualiza QR y comprobantes; no hay flujo de imágenes genéricas | Usar `AqQrPreview` o el visor de comprobante dentro de su feature | Media | `AqQrPreview`, `ComprobanteViewer` |
| H3 | Image Viewer | — | NOT_APPLICABLE | — | — | — | — | No existe navegación entre imágenes genéricas en UX V1 | `AqQrSheet` cubre únicamente el caso de QR a pantalla completa | Media | `AqQrSheet` |
| H4 | Carousel | `core/ui/.../AqLists.kt` → `AqCarousel` | COMPLETE | Sí | NO VERIFICADO | Parcial | `AqCarouselTest` | Usado en S01 carrusel de favoritos; ancho fijo por ítem, siguiente asoma | Sin cambios | Media | `AqIcon`, `AqTextStyles` |
| H5 | Media Attachment | `ComprobantePreview` + `AqMediaFrame` | COMPLETE | Sí | NO VERIFICADO | Sí (vista previa identificada y fallback textual) | `ScreensA11yCoverageTest.s10_receipt_preview_renders_without_crash`, `AqMediaFrame` | — | El comprobante adjunto se muestra dentro del visor; progreso de carga no aplica a la operación local | Media | `AqMediaFrame`, `ComprobantePreview` |
| H6 | Thumbnail | — | NOT_APPLICABLE | — | — | — | — | UX V1 no muestra galerías de miniaturas | `AqQrPreview` y `ComprobanteViewer` presentan recursos individuales | Baja | `AqQrPreview` |
| H7 | Document Preview | `ComprobanteViewer` (feature) | PARTIAL | Sí | NO VERIFICADO | Parcial | `ComprobanteThumbnailTest` (fallback PDF) | Imágenes se muestran; PDF conserva un marcador sin render de páginas en Android/iOS | Mantener en la feature y renderizar PDF con soporte multiplataforma validado antes de declarar completo | Baja | Implementación nativa por plataforma |
| H8 | File Attachment | — | NOT_APPLICABLE | — | — | — | — | UX V1 no ofrece carga/adjunto genérico de archivos; comprobantes usan su flujo propio | No añadir componente genérico sin caso de producto | — | — |
| H9 | Upload Progress | — | NOT_APPLICABLE | — | — | — | — | UX V1 no expone subida de archivos con progreso medible | `AqProgressBar` sigue cubriendo operaciones con progreso determinado | Media | `AqProgressBar` |
| H10 | Media Empty State | `AqEmptyState` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Reutiliza `AqEmptyState` genérico | Sin cambios | Alta | `AqEmptyState` |

### Notas familiares H

- **Polaris:** No hay componente de medios específico en Polaris web; se adapta con tarjetas, marcos y QR.
- **Moon:** Moon ofrece image viewer, carousel, media card. Se estudian para interacción táctil; no se copia identidad visual.
- **Riesgo:** `decodeImageBitmap` es expect/actual (Android BitmapFactory, iOS Skia). iOS runtime no verificado (sin Xcode). Carrusel y visor de imagen están en feature, no en `core/ui`.

---

## Familia I — Feedback y estados

| # | Patrón | Ruta de implementación | Estado real | Android | iOS | Accesibilidad | Pruebas | Carencias | Decisión técnica | Prioridad | Dependencias |
|---|---|---|---|---|---|---|---|---|---|---|---|
| I1 | Snackbar | `AqSnackbarHost` + `AqFeedbackEvent` | COMPLETE | Sí | NO VERIFICADO | Sí (liveRegion polite) | `ScreensA11yCoverageTest` | Toast efímero con acción opcional; contenedor superficie inversa | Sin cambios | Alta | `SnackbarHost` (puente M3) |
| I2 | Toast | `AqFeedbackEvent` (Snackbar transitorio) | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Misma implementación que snackbar; duración corta | Sin cambios | Alta | `AqFeedbackEvent` |
| I3 | Banner | `AqStatusBanner` | COMPLETE | Sí | NO VERIFICADO | Sí | `AqStatusBannerLayoutTest` | Misma implementación que I6; tono distingue severidad | Sin cambios | Alta | `AqStatusBanner` |
| I4 | Inline Error | `AqTextField` error + `AqFieldMessages` | COMPLETE | Sí | NO VERIFICADO | Sí (error(), liveRegion) | `AqAccessibilityContractTest` | Icono + texto debajo del campo, nunca solo color | Sin cambios | Alta | `AqTextField`, `AqIcon` |
| I5 | Success Message | `AqStatusBanner` (tone=Success) / `AqSuccessCheck` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Glifo de confirmación decorativo; texto comunica el éxito | Sin cambios | Alta | `AqStatusBanner`, `AqSuccessCheck` |
| I6 | Warning Message | `AqStatusBanner` (tone=Warning) | COMPLETE | Sí | NO VERIFICADO | Sí | `AqStatusBannerLayoutTest` | Tono warning con par content/container AA | Sin cambios | Alta | `AqStatusBanner` |
| I7 | Loading Indicator | `AqSpinner` / `AqLoading` | COMPLETE | Sí | NO VERIFICADO | Sí (contentDescription, liveRegion) | `ScreensA11yCoverageTest` | Indicador circular indeterminado sin movimiento decorativo | Sin cambios | Alta | `AqSpinner`, `AqLoading` |
| I8 | Circular Progress | `AqProgressBar` (determinado) | COMPLETE | Sí | NO VERIFICADO | Sí (progressBarRangeInfo) | `ScreensA11yCoverageTest` | Progreso determinado 0..1 con anuncios accesibles | Sin cambios | Alta | `AqProgressBar` |
| I9 | Linear Progress | `AqProgressBar` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Misma implementación que I8; barra lineal | Sin cambios | Alta | `AqProgressBar` |
| I10 | Skeleton | `AqSkeleton` | COMPLETE | Sí | NO VERIFICADO | Sí (decorativo, excluido del árbol semántico) | `ScreensA11yCoverageTest` | — | Skeleton estático, sin movimiento decorativo que afecte sensibilidad al movimiento | Media | `AqSkeleton` |
| I11 | Shimmer | — | NOT_APPLICABLE | — | — | — | — | UX V1 prohíbe bucles de animación decorativos (regla V1.3) | No implementar shimmer | — | — |
| I12 | Error State | `AqEmptyState` + `AqStatusBanner` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Copy de error y acción de recuperación contextual | Media | `AqEmptyState`, `AqStatusBanner` |
| I13 | Empty State | `AqEmptyState` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Sin cambios | Alta | `AqEmptyState` |
| I14 | Confirmation State | `AqDialog` (confirmación) / `AqSuccessCheck` | COMPLETE | Sí | NO VERIFICADO | Sí | `ContextPickerDialogTest` | Confirmación destructiva con foco en acción conservativa | Sin cambios | Alta | `AqDialog` |
| I15 | Retry Action | `AqTextAction` / `AqBannerAction` | COMPLETE | Sí | NO VERIFICADO | Sí | `ActionHierarchyTest` | Acción de reintento como botón de texto o acción de banner | Sin cambios | Media | `AqTextAction`, `AqBannerAction` |
| I16 | Offline Indicator | `AqStatusBanner` (Notice-bar) | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Aviso persistente de modo local/sin conexión | Sin cambios | Alta | `AqStatusBanner` |
| I17 | Sync Status | `AqSyncIndicator` (X-02) | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Estados local/pendiente/sincronizado/error/reintento | Sin cambios | Alta | `AqSyncIndicator`, `AqBadge` |
| I18 | Haptic Feedback | `AqFeedbackEvent` / `AqButtonImpl` (hapticFeedback) | COMPLETE | Sí | NO VERIFICADO | Sí | `LocalAccessBiometricTest`, `AqAccessibilityContractTest` | — | Háptica limitada a eventos y acción primaria; el sistema puede desactivarla | Media | `LocalHapticFeedback` |

### Notas familiares I

- **Polaris:** Feedback transitorio y persistente con estados semánticos. Error nunca solo por color.
- **Moon:** Moon ofrece toasts, skeletons, skeletons con shimmer. Se confirma que shimmer decorativo no pertenece al lenguaje de Agenda QR (regla V1.3).
- **Riesgo:** Shimmer no implementado por decisión de producto (sin animación decorativa). Skeleton estático sin movimiento continuo.

---

## Familia J — Interacciones móviles avanzadas

| # | Patrón | Ruta de implementación | Estado real | Android | iOS | Accesibilidad | Pruebas | Carencias | Decisión técnica | Prioridad | Dependencias |
|---|---|---|---|---|---|---|---|---|---|---|---|
| J1 | Swipe to Dismiss | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara swipe to dismiss; gestos nunca son la única forma | No implementar | — | — |
| J2 | Swipe Actions | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara acciones por deslizamiento en filas | No implementar | — | — |
| J3 | Long Press | — | NOT_APPLICABLE | — | — | — | — | No hay interacción por larga pulsación en UX V1 | No implementar | — | — |
| J4 | Contextual Actions | `AqActionSheet` / `AqListRow(trailing=...)` | COMPLETE | Sí | NO VERIFICADO | Sí | `AqActionSheetTest` | — | Acciones contextuales con diálogo o acción explícita en fila; sin gestos destructivos ocultos | Media | `AqActionSheet`, `AqListRow` |
| J5 | Drag and Drop | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara drag and drop | No implementar | — | — |
| J6 | Pull to Refresh | — | NOT_APPLICABLE | — | — | — | — | No hay pull-to-refresh en UX V1 | No implementar | — | — |
| J7 | Expand and Collapse | `AqExpansionPanel` | COMPLETE | Sí | NO VERIFICADO | Sí (estado expandido/colapsado semántico) | `AqExpansionPanelTest` | — | Componente reutilizable; S07 mantiene además su estado de edición propio | Media | `AqSection` |
| J8 | Bottom Sheet Gestures | `AqBottomSheet` | COMPLETE | Sí | NO VERIFICADO | Sí (handle accesible, acción explícita y gesto probado) | `AqBottomSheetTest` | — | Descarta al acumular cuatro áreas táctiles mínimas de arrastre; expansión la controla el llamador | Media | `AqBottomSheet` |
| J9 | Selection Gestures | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara selección por gestos | No implementar | — | — |
| J10 | Haptic Feedback | `AqFeedbackEvent` | COMPLETE | Sí | NO VERIFICADO | Sí | `LocalAccessBiometricTest` | — | Mismo patrón validado en I18 | Media | `LocalHapticFeedback` |
| J11 | Touch Feedback | `AqButtonImpl` (color activo, sin ripple) | COMPLETE | Sí | NO VERIFICADO | Sí | `AqButtonStyleTest` | Presión resuelta por cambio de token de color | Sin cambios | Alta | `AqButtonImpl` |
| J12 | Nested Scrolling | `AqScreenScaffold` (verticalScroll) | COMPLETE | Sí | NO VERIFICADO | Sí | `LayoutReferenceTest` | — | El scroll vertical simple cubre UX V1; no hay caso de anidado complejo | Media | `AqScreenScaffold` |
| J13 | Predictive Back | `PredictiveBackHandler` (host) | PARTIAL | Sí | NO VERIFICADO | Sí | `RootNavigationChromeTest` | Compilado en 3 targets; sin verificación en dispositivo (ADR-0007) | Documentar iOS como no verificado | Alta | Host de plataforma |
| J14 | Edge-to-edge | `AqScreenScaffold` (contentWindowInsets) | COMPLETE | Sí | NO VERIFICADO | Sí | `LayoutReferenceTest` | Insets de contenido manejados por Scaffold | Sin cambios | Alta | `AqScreenScaffold` |
| J15 | Keyboard-aware Layout | `AqBottomActionDock` (imePadding) | COMPLETE | Sí | NO VERIFICADO | Sí | `LayoutReferenceTest` | IME consumido; acción fija no queda bajo teclado | Sin cambios | Alta | `AqBottomActionDock` |
| J16 | One-handed Interaction | `AqBottomAction` (acción alcanzable) | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Acción primaria persistente en zona inferior alcanzable | Media | `AqBottomAction` |
| J17 | Gesture Conflict Resolution | — | NOT_APPLICABLE | — | — | — | — | No hay conflictos de gestos declarados en UX V1 | No implementar | — | — |

### Notas familiares J

- **Moon:** Moon ofrece swipe actions, bottom sheet gestures, pull to refresh. Se estudian como referencia; UX V1 no declara la mayoría, y los gestos nunca son la única forma de acción.
- **Polaris:** Polaris web no define interacciones táctiles móviles; se aplican D-03 (gestos nativos) y D-04 (bottom sheet nativo).
- **Riesgo:** Swipe/drag/long-press/pull-to-refresh no implementados por decisión de producto. Predictive back compilado pero sin verificación en dispositivo.

---

## Resumen de cobertura y prioridades de implementación

### Conteo por clasificación

| Clasificación | Cantidad | Patrón |
|---|---|---|
| COMPLETE | 109 | A1-A2, A6-A7, A9-A10, A12, A15, B1-B7, B9-B10, B13-B16, C1-C5, C8, C15-C19, C23-C27, D1-D7, D11, E1-E7, E11-E15, F3-F7, F9-F12, F14, G1-G4, G5-G6, G9-G10, G13-G16, H1, H4-H5, H10, I1-I10, I12-I18, J4, J7-J8, J10-J12, J14-J16 |
| PARTIAL | 5 | A11, C9-C10, H7, J13 |
| MISSING | 0 | — |
| NOT_APPLICABLE | 48 | A3-A5, A8, A13-A14, B8, B11-B12, C6-C7, C11-C14, C20-C22, D8-D10, D12, E8-E10, E16, F1-F2, F8, F13, G7-G8, G11-G12, G17, H2-H3, H6, H8-H9, I11, J1-J3, J5-J6, J9, J17 |

### Riesgos arquitectónicos principales

1. **iOS no verificado:** Sin macOS/Xcode, todo el render en iOS (Inter empaquetado, tema oscuro, accesibilidad VoiceOver) queda como NO VERIFICADO. Solo compila.
2. **Puentes Material 3:** `BasicTextField`, `Checkbox`, `RadioButton`, `Switch`, `DatePickerDialog`, `AlertDialog`, `TimeInput`, `SnackbarHost`, `CircularProgressIndicator`, `LinearProgressIndicator`, `PrimaryScrollableTabRow`, `IconButton`, `Text` se usan como infraestructura técnica; todos los valores visuales vienen de tokens Polaris, pero el runtime es M3.
3. **Componentes fuera del alcance funcional V1:** Password input, autocomplete, slider, stepper, swipeable list item, pull-to-refresh, timeline, popover anclado, side sheet, drawer, avatar group, file attachment y badges de navegación están clasificados `NOT_APPLICABLE` con su justificación por fila. No queda ningún patrón clasificado como `MISSING`. `AqMultilineTextField` cubre texto multilineal y `AqTooltip` está en E11/F9.
4. **`AqBottomSheet` con gestos:** El cierre por arrastre descendente desde el handle (cuatro targets mínimos de 48 dp) está implementado; la expansión/colapso es controlada por el llamador y no hay estado intermedio. Falta trampa de foco explícita y verificación en iOS.
5. **Gestión de foco en overlays:** `Dialog` de Compose ofrece la modalidad de ventana; el foco inicial conservativo destructivo se prueba en Android. Retención/restauración del foco y VoiceOver quedan sin verificación en dispositivo.

### Verificación residual

| Prioridad | Verificación | Evidencia pendiente |
|---|---|---|
| Alta | Revisar accesibilidad de componentes `PARTIAL` | Las carencias están anotadas por fila; no se declara paridad total |
| Alta | Verificar UI en iOS con dispositivo/simulador y VoiceOver | Compilación Kotlin/Native pasa, pero este entorno no ejecuta Xcode ni simulador iOS |
| Media | Verificar retención/restauración de foco en overlays | La prueba Android cubre el foco inicial destructivo, no la restauración; VoiceOver/iOS no están disponibles aquí |

---

## Actualización del índice documental

Este documento se añade a `docs/README.md` bajo la sección "Sistema de diseño (Polaris reimplementado en Kotlin)":

| Documento | Contenido |
|---|---|
| [`12-matriz-cobertura-polaris-moon-agendaqr.md`](12-matriz-cobertura-polaris-moon-agendaqr.md) | Matriz completa de cobertura Polaris / Moon / Agenda QR: clasificación, estado, carencias, riesgos y prioridades |

Estado final de la auditoría: la matriz permite conocer exactamente qué tiene Agenda QR, qué falta y por qué, respetando la jerarquía de autoridad (Polaris → identidad visual; Agenda QR → comportamiento funcional; Moon → solo patrones móviles).
