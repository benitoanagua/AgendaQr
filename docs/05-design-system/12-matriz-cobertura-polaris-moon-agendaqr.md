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
| A1 | Navigation Bar (bottom) | `core/ui/.../AqChrome.kt` → `AqNavBar` | PARTIAL | Sí | NO VERIFICADO | Sí (rol Tab, targets 48 dp) | `RootNavigationChromeTest`, `ScreensA11yCoverageTest` | Sin indicador de desplazamiento para >4 destinos; sin soporte de estado "disabled" por destino | Conservar; añadir pruebas de selección por destino | Alta | `AqIcon`, `AqColor`, `AqMetrics` |
| A2 | Navigation Rail | `core/ui/.../AqChrome.kt` → `AqNavRail` | PARTIAL | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Verificación de layout retirada por ADR-0007 (requiere revisión manual en tablet) | Mantener para expanded/medium; documentar como no verificado en iOS | Media | `AqIcon`, `AqColor` |
| A3 | Modal Navigation Drawer | — | NOT_APPLICABLE | — | — | — | — | UX V1 define navegación inferior (barra) y rail en tablet, no drawer lateral | No implementar; UX V1 lo descarta | — | — |
| A4 | Permanent Navigation Drawer | — | NOT_APPLICABLE | — | — | — | — | UX V1 define navegación inferior (barra) y rail en tablet, no drawer permanente | No implementar; UX V1 lo descarta | — | — |
| A5 | Contextual Bar | `core/ui/.../AqStatus.kt` → `AqStatusBanner` | PARTIAL | Sí | NO VERIFICADO | Sí (liveRegion polite) | `AqStatusBannerLayoutTest` | Sin barra contextual de selección múltiple (no declarada en UX V1) | Conservar como banner de estado; no añadir contextual bar sin justificación de producto | Baja | `AqTone`, `AqIcon` |
| A6 | Top App Bar | `core/ui/.../AqChrome.kt` → `AqTopBar` | PARTIAL | Sí | NO VERIFICADO | Sí (heading, contentDescription) | `ScreensA11yCoverageTest`, `LayoutReferenceTest` | Máximo 2 acciones + overflow; sin soporte de barra de búsqueda integrada (usa `AqSearchTrigger` separado) | Mantener; no añadir toolbar de búsqueda dentro de TopBar sin contrato | Alta | `AqIcon`, `AqActionSheet` |
| A7 | Bottom App Bar | `core/ui/.../AqScaffold.kt` → `AqBottomActionDock` + `AqBottomAction` | PARTIAL | Sí | NO VERIFICADO | Sí | `LayoutReferenceTest` | Solo una primaria fija; sin menú de acciones secundarias en el dock | Conservar; UX V1 permite máximo una primaria por pantalla | Alta | `AqPrimaryButton`, `AqScreenScaffold` |
| A8 | Breadcrumbs | — | NOT_APPLICABLE | — | — | — | — | UX V1 no usa breadcrumbs; navegación por back stack y rutas | No implementar | — | — |
| A9 | Navigation Tabs | `core/ui/.../AqTabs.kt` → `AqTabs` | PARTIAL | Sí | NO VERIFICADO | Sí (Role.Tab, selected, target proporcionado por Tab de Compose) | `AqTabsLayoutTest` | API solo admite títulos de texto, sin slot de icono; el pivot S06 no necesita iconos | Semántica y acceso a última pestaña verificados a escala 100/150/200 %; conservar API acotada al uso actual | Media | `PrimaryScrollableTabRow` (M3 puente) |
| A10 | Tab Bar | `core/ui/.../AqButtons.kt` → `AqSegmentedControl` | PARTIAL | Sí | NO VERIFICADO | Sí (rol RadioButton, selectableGroup) | `ScreensA11yCoverageTest`, `DestinationsScreenA11yTest` | Sin variante de pestañas desplazables con scroll independiente | Mantener para segmentado de filtros (S01) | Alta | `AqIcon`, `AqTone` |
| A11 | Back Navigation | `AqTopBar(onBack=...)`, `PredictiveBackHandler` (host) | PARTIAL | Sí | NO VERIFICADO | Sí | `RootNavigationChromeTest` | Predictive back compilado pero sin verificación en dispositivo (ADR-0007) | Conservar back nativo; documentar iOS como no verificado | Alta | Host de plataforma |
| A12 | Overflow Menu | `AqActionSheet` (interno, vía `LocalAqActionSheetController`) | PARTIAL | Sí | NO VERIFICADO | Sí | `AqActionSheetTest` | Implementado como diálogo de lista, no como menú contextual anclado | Mantener patrón de diálogo (estable bajo Robolectric); no añadir anclaje sin justificación | Media | `AqDialog`, `AqDialogList` |
| A13 | Navigation Badges | — | NOT_APPLICABLE | — | — | — | — | `AqNavBar` y `AqNavRail` no muestran conteos; UX V1 no declara notificaciones pendientes en navegación | Los conteos existentes pertenecen a filtros/segmentos, no a destinos de navegación | — | — |
| A14 | Floating Action Button | — | NOT_APPLICABLE | — | — | — | — | UX V1.9 elimina FAB en S01; acción primaria fija en `AqBottomAction` | No implementar FAB; UX V1 lo descarta | — | — |
| A15 | Extended FAB | `core/ui/.../AqChrome.kt` → `AqExtendedFab` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Reutiliza `AqButton` primaria; sin cambios | Media | `AqButton` |

### Notas familiares A

- **Moon Design:** Moon ofrece `BottomNavigation`, `NavigationRail`, `Drawer`, `TopAppBar` como patrones móviles. Se estudian para confirmar que la navegación inferior con 4 destinos y el riel en tablet son patrones válidos. No se introduce código Dart ni dependencia Moon.
- **Polaris:** Polaris web no define navegación táctil móvil equivalente; se aplica D-03 (barra inferior y gestos nativos) y D-04 (bottom sheet nativo).
- **Riesgo arquitectónico:** `AqNavRail` no se verifica automáticamente en tablet (ADR-0007). Su estado en iOS es NO VERIFICADO.

---

## Familia B — Botones y acciones

| # | Patrón | Ruta de implementación | Estado real | Android | iOS | Accesibilidad | Pruebas | Carencias | Decisión técnica | Prioridad | Dependencias |
|---|---|---|---|---|---|---|---|---|---|---|---|
| B1 | Primary Button | `AqPrimaryButton` | COMPLETE | Sí | NO VERIFICADO | Sí (Role.Button, 48 dp) | `AqButtonStyleTest`, `ScreensA11yCoverageTest` | — | Relleno `brandFill` corregido (F1-F6) | Alta | `AqButtonImpl` |
| B2 | Secondary Button | `AqSecondaryButton` | COMPLETE | Sí | NO VERIFICADO | Sí | `AqButtonStyleTest` | Variante `solid` mapeada a Primary (documentada en contrato) | Mantener; v8.3 añadió borde fino del secundario | Alta | `AqButtonImpl` |
| B3 | Tertiary Button | `AqTextAction` (Default/Neutral) | PARTIAL | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | No existe componente `AqTertiaryButton` distinto; se resuelve como acción de texto | Evaluar si justifica componente separado; UX V1 usa acciones de texto | Media | `AqTextAction` |
| B4 | Destructive Button | `AqDangerButton` | COMPLETE | Sí | NO VERIFICADO | Sí | `AqButtonStyleTest`, `ActionHierarchyTest` | Requiere confirmación en diálogo (destructive=true en `AqDialog`) | Mantener; confirmación impuesta por `AqDialog` | Alta | `AqButtonImpl`, `AqDialog` |
| B5 | Plain Button | `AqTextAction` (Neutral) | PARTIAL | Sí | NO VERIFICADO | Sí | — | Sin componente plano distinto; `AqTextAction` cubre el patrón | No añadir duplicado; `AqTextAction` es el equivalente | Media | `AqTextAction` |
| B6 | Icon Button | `AqIconButton` | COMPLETE | Sí | NO VERIFICADO | Sí (contentDescription, 48 dp) | `AqAccessibilityContractTest` | — | Impone target 48 dp sobre el llamador (P-04 aceptado) | Alta | `AqIcon` |
| B7 | Circular Icon Button | `AqIconButton` | COMPLETE | Sí | NO VERIFICADO | Sí | `AqAccessibilityContractTest` | El glifo es circular por el contenedor de 48 dp; no hay variante de tamaño visual distinto | Mantener | Alta | `AqIcon` |
| B8 | Split Button | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara split button | No implementar | — | — |
| B9 | Button Group | `AqButtonGroup` | COMPLETE | Sí | NO VERIFICADO | Parcial | `ScreensA11yCoverageTest` | Máximo una acción enfatizada; sin prueba dedicada de grupo | Añadir prueba de énfasis único | Media | `AqPrimaryButton`, `AqSecondaryButton` |
| B10 | Segmented Button | `AqSegmentedControl` | PARTIAL | Sí | NO VERIFICADO | Sí (rol RadioButton) | `ScreensA11yCoverageTest`, `DestinationsScreenA11yTest` | Sin modo múltiple (solo selección única) | UX V1 usa segmentado mutuamente excluyente (Todos/Favoritos) | Alta | `AqSegmentedOption` |
| B11 | Toggle Button | `AqFavoriteToggle` (IconToggleButton) | PARTIAL | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Solo favorito; sin toggle genérico de encendido/apagado con estado persistente | Evaluar si `AqSwitch` cubre el uso de toggle | Media | `AqIcon` |
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
| C1 | Text Field | `AqTextField` | PARTIAL | Sí | NO VERIFICADO | Sí (error(), contentDescription, liveRegion) | `AqAccessibilityContractTest`, `ScreensA11yCoverageTest` | Sin contador de caracteres; el campo de varias líneas tiene la API dedicada `AqMultilineTextField` (C3) | Mantener separación entre campo estándar y multilineal | Alta | `BasicTextField` (puente) |
| C2 | Search Field | `AqSearchField` | PARTIAL | Sí | NO VERIFICADO | Sí (contentDescription, clearLabel) | `ScreensA11yCoverageTest` | Sin sugerencias en línea; `autoFocus` solo al entrar | Mantener; búsqueda global en S04 | Alta | `BasicTextField`, `AqIconButton` |
| C3 | Multiline Text Field | `core/ui/.../AqInputs.kt` → `AqMultilineTextField` | COMPLETE | Sí | NO VERIFICADO | Sí (error(), contentDescription, liveRegion) | `AqMultilineTextFieldTest` | — | Usado en S02 y S07 para notas; crece con contenido y escala | Alta | `BasicTextField` (puente) |
| C4 | Numeric Input | `AqAmountField` | PARTIAL | Sí | NO VERIFICADO | Sí (contentDescription, clearLabel) | `UxAmountValidationTest` | No formatea ni valida (por diseño, dominio lo hace); sin teclado numérico dedicado expuesto | El dominio valida importe; el componente solo transporta texto | Alta | `BasicTextField`, `AqIconButton` |
| C5 | Amount Input | `AqAmountField` | PARTIAL | Sí | NO VERIFICADO | Sí | `UxAmountValidationTest` | Cifras tabulares (`tnum`) en `AqTextStyles.Amount`; símbolo de moneda opcional | Sin cambios | Alta | `AqAmountField` |
| C6 | Password Input | — | NOT_APPLICABLE | — | — | — | — | UX V1 no recoge contraseñas; el PIN local es numérico y no es una contraseña | No implementar; capacidad fuera del producto V1 | — | — |
| C7 | OTP Input | `AqPinEntry.kt` → `AqPinDots` + `AqKeypad` | PARTIAL | Sí | NO VERIFICADO | Sí (contentDescription por tecla) | `LocalAccessGateTest`, `AqAccessibilityContractTest` | Es teclado PIN propio, no OTP genérico; 4-12 dígitos, envío explícito | Mantener como capacidad de acceso local (S00) | Alta | `AqIcon`, `AqText` |
| C8 | PIN Input | `AqPinDots` + `AqKeypad` | COMPLETE | Sí | NO VERIFICADO | Sí | `LocalAccessGateTest`, `AqAccessibilityContractTest` | — | Cifras enmascaradas, teclado 3×4, borrado accesible | Alta | `AqKeypad` |
| C9 | Date Picker | `AqDatePickerDialog` (Material `DatePickerDialog` puente) | PARTIAL | Sí | NO VERIFICADO | Parcial | `AqDateTimePickerTest` | Usa `DatePickerDialog` de M3 como puente; forma no completamente Polaris (radios 16 dp aplicados) | Evaluar reemplazo por diálogo propio de Polaris | Media | `DatePickerDialog` (puente M3) |
| C10 | Time Picker | `AqTimePickerDialog` (Material `AlertDialog`+`TimeInput` puente) | PARTIAL | Sí | NO VERIFICADO | Parcial | `AqDateTimePickerTest` | Mismo puente M3 que Date Picker | Evaluar reemplazo | Media | `AlertDialog`, `TimeInput` (puentes M3) |
| C11 | Date Range Picker | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara rango de fechas | No implementar | — | — |
| C12 | Searchable Select | `AqSelect` (abre `AqDialog` con `AqDialogList` + `AqRadioButton`) | PARTIAL | Sí | NO VERIFICADO | Parcial | `ScreensA11yCoverageTest` | No es searchable (lista de opciones sin filtro de texto); UX V1 selectores sobre pocos elementos | Mantener diálogo de opciones; no añadir searchable sin justificación | Media | `AqDialog`, `AqDialogList`, `AqRadioButton` |
| C13 | Combobox | `AqSelect` | PARTIAL | Sí | NO VERIFICADO | Parcial | — | Sin escritura libre + filtro; solo selección de lista cerrada | No añadir combobox sin requerimiento de producto | Media | `AqSelect` |
| C14 | Autocomplete | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara sugerencias/autocompletado | No implementar sin caso de producto | — | — |
| C15 | Dropdown | `AqSelect` | PARTIAL | Sí | NO VERIFICADO | Parcial | — | `AqSelect` usa diálogo, no desplegable en línea | Mantener patrón de diálogo (estable bajo pruebas) | Media | `AqSelect` |
| C16 | Multi Select | `AqCheckboxGroup` + `AqSelectAllCheckbox` | COMPLETE | Sí | NO VERIFICADO | Sí (rol Checkbox y estado mixto semántico) | `AqSelectAllCheckboxTest`, `ScreensA11yCoverageTest` | — | El estado mixto se deriva con `AqTriState`; el consumidor define la acción al activar | Media | `AqCheckbox`, `AqTriState` |
| C17 | Checkbox | `AqCheckbox` | COMPLETE | Sí | NO VERIFICADO | Sí (Role.Checkbox, 48 dp) | `AqAccessibilityContractTest` | — | Sin margen externo; grupo posee separación | Alta | `Checkbox` (puente M3) |
| C18 | Radio Button | `AqRadioButton` | COMPLETE | Sí | NO VERIFICADO | Sí (Role.RadioButton, 48 dp) | `AqAccessibilityContractTest` | — | `AqChoiceGroup` para selección mutuamente excluyente | Alta | `RadioButton` (puente M3) |
| C19 | Switch | `AqSwitch` | PARTIAL | Sí | NO VERIFICADO | Sí (Role.Switch, 48 dp) | `ScreensA11yCoverageTest` | Solo para ajustes de efecto inmediato (UX V1) | Sin cambios | Media | `Switch` (puente M3) |
| C20 | Slider | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara slider | No implementar | — | — |
| C21 | Stepper | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara entradas numéricas incrementales | No implementar sin caso de producto | — | — |
| C22 | Quantity Selector | — | NOT_APPLICABLE | — | — | — | — | No aplica a Agenda QR (no carrito de compras) | No implementar | — | — |
| C23 | Input Group | `AqChoiceGroup` / `AqCheckboxGroup` | PARTIAL | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Mensaje de grupo pintado una vez; sin contenedor de campo agrupado con borde | Mantener grupos de opciones | Media | `AqRadioButton`/`AqCheckbox` |
| C24 | Fieldset | `AqSection` + `AqSectionHeader` | PARTIAL | Sí | NO VERIFICADO | Parcial | — | Sin fieldset con borde de agrupación semántica | Usar `AqSection`/`AqCard` según contenido | Media | `AqSectionHeader` |
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
| D1 | Filter Chips | `AqFilterChip` | PARTIAL | Sí | NO VERIFICADO | Sí (Role.Button, selected, etiqueta y conteo textual, 48 dp) | `AqFilterChipTest`, `DestinationsScreenA11yTest` | `stateDescription` es opcional y lo aporta el consumidor si hace falta; el conteo visible se expone como texto | Mantener conteo como contenido del chip, sin rol redundante para texto no interactivo | Media | `AqBadge`, `AqTonalTag` |
| D2 | Choice List | `AqChoiceGroup` / `AqDialogList` + `AqRadioButton` | PARTIAL | Sí | NO VERIFICADO | Parcial | `ScreensA11yCoverageTest` | Lista de radio en diálogo, no en línea | Mantener patrón de diálogo (UX V1 S08) | Media | `AqDialog`, `AqDialogList` |
| D3 | Selectable Cards | `AqTappableCard` + `AqListRow(selected=...)` | PARTIAL | Sí | NO VERIFICADO | Sí (selected semántica) | `ScreensA11yCoverageTest` | Sin tarjeta seleccionable con borde de selección dedicado | `AqListRow` ya usa `selectionFill`/`selectionBorder` | Media | `AqListRow`, `AqTappableCard` |
| D4 | Multi Select | `AqCheckboxGroup` + `AqSelectAllCheckbox` | COMPLETE | Sí | NO VERIFICADO | Sí (rol Checkbox y estado mixto semántico) | `AqSelectAllCheckboxTest`, `ScreensA11yCoverageTest` | — | Selección parcial representada con `TriStateCheckbox`; acción definida por el consumidor | Media | `AqCheckbox`, `AqTriState` |
| D5 | Segmented Control | `AqSegmentedControl` | PARTIAL | Sí | NO VERIFICADO | Sí (rol RadioButton) | `DestinationsScreenA11yTest` | Solo selección única; sin modo múltiple | UX V1 usa segmentado mutuamente excluyente | Alta | `AqSegmentedOption` |
| D6 | Filter Bar | `AqSegmentedControl` (S01 Todos/Favoritos) | PARTIAL | Sí | NO VERIFICADO | Sí | `DestinationsScreenA11yTest` | Sin filtro de texto en barra de filtros (UX V1 no lo declara) | Mantener segmento de filtros | Alta | `AqSegmentedControl` |
| D7 | Filter Popover | `AqDialog` + `AqDialogList` | PARTIAL | Sí | NO VERIFICADO | Parcial | — | Popover anclado no implementado; se usa diálogo | Mantener diálogo; no añadir anclaje sin justificación | Media | `AqDialog` |
| D8 | Sort Selector | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara ordenamiento por el usuario | No implementar | — | — |
| D9 | Date Range Filter | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara filtro de rango de fechas | No implementar | — | — |
| D10 | Active Filter Summary | — | NOT_APPLICABLE | — | — | — | — | S01 usa filtro segmentado con conteo y no acumula filtros activos simultáneos | El segmento existente cubre el flujo V1; no añadir resumen separado | — | `AqSegmentedControl` |
| D11 | Clear Filters | `AqTextAction` (Descartar) | PARTIAL | Sí | NO VERIFICADO | Sí | `ActionHierarchyTest` | Acción de texto, no chip de filtro removable | Mantener como acción de texto | Media | `AqTextAction` |
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
| E5 | Alert Dialog | `AqDialog` (confirm/dismiss) | PARTIAL | Sí | NO VERIFICADO | Parcial | `ScreensA11yCoverageTest` | Sin distinción visual propia de "alert" vs "confirmation"; usa mismo `AqDialog` | No añadir variante sin requerimiento de producto | Media | `AqDialog` |
| E6 | Bottom Sheet | `AqBottomSheet` (D-04) | PARTIAL | Sí | NO VERIFICADO | Sí (paneTitle, cierre explícito, target 48 dp) | `AqBottomSheetTest` | Retención/restauración de foco delegada al `Dialog` de Compose; gesto y paneTitle no validados en VoiceOver | Gesto de dismiss implementado; estado `expanded` controlado por el llamador | Media | `AqBottomSheet`, `Dialog` |
| E7 | Modal Bottom Sheet | `AqBottomSheet` | PARTIAL | Sí | NO VERIFICADO | Sí (ver E6) | `AqBottomSheetTest` | Mismo componente que E6; sin variante no modal | No añadir duplicado; `expanded` controla la presentación | Media | `AqBottomSheet` |
| E8 | Side Sheet | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara side sheet; navegación por barra inferior y rail | No implementar | — | — |
| E9 | Drawer | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara drawer de navegación | No implementar | — | — |
| E10 | Popover | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara contenido flotante anclado; los selectores y acciones usan diálogos | Mantener `AqDialog`/`AqActionSheet`; no hace falta overlay anclado | — | `AqDialog` |
| E11 | Tooltip | `core/ui/.../AqTooltip.kt` → `AqTooltip` | PARTIAL | Sí | NO VERIFICADO | Sí (48 dp del llamador, nombre accesible) | `AqTooltipTest` | Tooltip controlado por estado `isVisible`; disparo por pulsación larga delegado al llamador; iOS no verificado | Implementado como bloque tonal con par AA; útil para controles solo-icono | Baja | `AqIcon`, `AqText` |
| E12 | Context Menu | `AqActionSheet` (vía `LocalAqActionSheetController`) | PARTIAL | Sí | NO VERIFICADO | Sí | `AqActionSheetTest` | Implementado como diálogo de lista, no como menú anclado al disparador | Mantener diálogo (estable bajo Robolectric); no añadir anclaje sin justificación | Media | `AqDialog`, `AqDialogList` |
| E13 | Dropdown Menu | `AqActionSheet` | PARTIAL | Sí | NO VERIFICADO | Sí | `AqActionSheetTest` | Misma limitación que E12 | Mantener patrón de diálogo | Media | `AqDialog` |
| E14 | Overflow Menu | `AqActionSheet` | PARTIAL | Sí | NO VERIFICADO | Sí | `AqActionSheetTest` | Máximo acciones en lista; sin submenús | UX V1 usa overflow en TopBar y HeroHeader | Alta | `AqActionSheet`, `AqTopBarAction` |
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
| F1 | Avatar | `AqIconBadge` (glifo tonal sobre bloque) | PARTIAL | Sí | NO VERIFICADO | Parcial | `ScreensA11yCoverageTest` | No hay avatar con iniciales de usuario; `AqContextBadge` da color de contexto | Evaluar si producto requiere avatar con iniciales | Baja | `AqIconBadge`, `accentFor` |
| F2 | Avatar Group | — | NOT_APPLICABLE | — | — | — | — | UX V1 no presenta grupos de usuarios/avatar | No implementar sin caso de producto | — | — |
| F3 | Badge | `AqBadge` (outline), `AqTonalTag` (relleno tonal) | COMPLETE | Sí | NO VERIFICADO | Sí (texto, nunca solo color) | `AqButtonStyleTest`, `AqSchemeTest` | — | Par texto/contenedor garantizado a AA | Alta | `AqTone`, `AqShapes` |
| F4 | Status Badge | `AqTonalTag` + `AqStatusPill` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | `AqStatusPill` inversa para "Modo local" | Sin cambios | Alta | `AqTone`, `AqColor` |
| F5 | Icon Badge | `AqIconBadge` | COMPLETE | Sí | NO VERIFICADO | Parcial | `ScreensA11yCoverageTest` | Glifo decorativo; texto de fila comunica el tipo | Sin cambios | Alta | `AqIcon`, `AqTone` |
| F6 | Banner | `AqStatusBanner` | COMPLETE | Sí | NO VERIFICADO | Sí (liveRegion polite) | `AqStatusBannerLayoutTest` | Acción principal + descarte; tono con par content/container garantizado | Sin cambios | Alta | `AqTone`, `AqButton` |
| F7 | Inline Alert | `AqStatusBanner` (tone=Danger/Warning/Info) | COMPLETE | Sí | NO VERIFICADO | Sí | `AqStatusBannerLayoutTest` | Misma implementación que banner; tono distingue severidad | Sin cambios | Alta | `AqStatusBanner` |
| F8 | Callout | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara callout | No implementar | — | — |
| F9 | Tooltip | `AqTooltip` (ver E11) | PARTIAL | Sí | NO VERIFICADO | Sí (nombre accesible conservado en el control) | `AqTooltipTest` | Estado visible controlado por el llamador; el patrón de disparo largo debe componerse en el consumidor | Reutilizar el componente E11; no duplicar implementación | Baja | `AqTooltip` |
| F10 | Metadata | `AqMetaRow` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Label + valor + acción final opcional | Sin cambios | Alta | `AqText`, `AqIcon` |
| F11 | Description List | `AqIndexTable` | PARTIAL | Sí | NO VERIFICADO | Parcial | `ScreensA11yCoverageTest` | Tabla de índices, no lista de descripciones con términos | `AqIndexTable` cubre tabla de datos; descripción lista pendiente | Media | `AqIndexTable` |
| F12 | Key Value Pair | `AqMetaRow` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Sin cambios | Alta | `AqMetaRow` |
| F13 | User Identity | `AqContextBadge` + `AqMetaRow` | PARTIAL | Sí | NO VERIFICADO | Parcial | — | Sin componente de identidad de usuario dedicado (avatar + nombre + rol) | Evaluar si requiere en cuenta/respaldo | Baja | `AqContextBadge`, `AqMetaRow` |
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
| G4 | Data Table | `AqIndexTable` | PARTIAL | Sí | NO VERIFICADO | Parcial | — | Mismo componente que G3; sin ordenamiento ni selección de filas | UX V1 no declara ordenamiento de tabla | Media | `AqIndexTable` |
| G5 | List Row | `AqListRow` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Sin cambios | Alta | `AqListRow` |
| G6 | Selectable List | `AqListRow(selected=...)` + `AqSegmentedControl` | PARTIAL | Sí | NO VERIFICADO | Sí | `DestinationsScreenA11yTest` | Selección por fila con fondo+borde (no solo color) | Sin cambios | Alta | `AqListRow` |
| G7 | Swipeable List Item | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara acciones por deslizamiento en filas | Mantener acciones visibles/accesibles; no añadir gestos como única vía | — | — |
| G8 | Timeline | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara una vista de línea temporal | No implementar sin caso de producto | — | — |
| G9 | Activity Feed | `AqListRow` en `OperationListScreen` | PARTIAL | Sí | NO VERIFICADO | Sí | `LayoutReferenceTest` | Lista de operaciones con tag de estado; sin feed temporal dedicado | `AqListRow` cubre la lista de actividades | Alta | `AqListRow`, `AqTonalTag` |
| G10 | Pagination | `AqLoadMoreFooter` | PARTIAL | Sí | NO VERIFICADO | Sí (liveRegion polite) | `ScreensA11yCoverageTest` | Footer "Cargar más" / fin alcanzado; sin paginación numérica | UX V1 usa paginación 50 en 50 (U-22), sin página numérica | Media | `AqLoadMoreFooter` |
| G11 | Infinite Scrolling | `AqLoadMoreFooter` + `onLoadMore` | PARTIAL | Sí | NO VERIFICADO | Sí | — | Carga por scroll al llegar al footer; sin scroll infinito automático | Mantener carga bajo demanda | Media | `AqLoadMoreFooter` |
| G12 | Pull to Refresh | — | NOT_APPLICABLE | — | — | — | — | UX V1 no asigna refresco manual por gesto; la carga se solicita explícitamente | No implementar sin caso de producto | — | — |
| G13 | Empty List | `AqEmptyState` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Sin cambios | Alta | `AqEmptyState` |
| G14 | Loading List | `AqSkeleton` / `AqLoading` | PARTIAL | Sí | NO VERIFICADO | Parcial | `ScreensA11yCoverageTest` | Skeleton sin movimiento decorativo; `AqLoading` con mensaje opcional | Sin cambios | Media | `AqSkeleton`, `AqLoading` |
| G15 | Error List | `AqEmptyState` + error en `AqStatusBanner` | PARTIAL | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Estado de error como banner o empty state con acción de recuperación | Sin cambios | Media | `AqEmptyState`, `AqStatusBanner` |
| G16 | Grouped List | `AqSection` + `AqSectionHeader` + `AqListBody` | PARTIAL | Sí | NO VERIFICADO | Parcial | — | Sección con encabezado y filas; sin sticky header de sección | `AqSectionHeader` sin pegajosidad | Media | `AqSection`, `AqSectionHeader` |
| G17 | Sticky Section Header | — | NOT_APPLICABLE | — | — | — | — | Las pantallas V1 no requieren encabezados pegajosos | Usar encabezados normales de `AqSection` | — | `AqSectionHeader` |

### Notas familiares G

- **Polaris:** Lista de recursos con filas, tabla de índices, lista agrupada con encabezados.
- **Moon:** Moon ofrece listas deslizables, pull-to-refresh, selección múltiple. Se confirma que swipe-to-actions no es necesario si hay alternativas accesibles (UX V1: gestos nunca son la única forma).
- **Riesgo:** No hay swipeable list item, pull-to-refresh, timeline, sticky header. UX V1 no los declara; gestos no son la única forma de acción.

---

## Familia H — Contenido multimedia

| # | Patrón | Ruta de implementación | Estado real | Android | iOS | Accesibilidad | Pruebas | Carencias | Decisión técnica | Prioridad | Dependencias |
|---|---|---|---|---|---|---|---|---|---|---|---|
| H1 | QR Preview | `AqQrPreview` | PARTIAL | Sí | NO VERIFICADO | Parcial | `QrPreviewContractTest`, `QrFullscreenTest` | contentDescription "QR" fijo (P-02 aceptado); copia al portapapeles no en el componente | `decodeQrImageBitmap` expect/actual; `AqQrSheet` para pantalla completa | Alta | `decodeImageBitmap` (expect/actual) |
| H2 | Image Preview | `AqMediaFrame` + `Image` | PARTIAL | Sí | NO VERIFICADO | Parcial | — | `AqMediaFrame` es contenedor genérico; no hay visor de imagen dedicado | `AqMediaFrame` cubre marco de medios | Media | `AqMediaFrame` |
| H3 | Image Viewer | `AqQrSheet` (QR fullscreen) | PARTIAL | Sí | NO VERIFICADO | Parcial | `QrFullscreenTest` | Solo para QR; no visor genérico de imágenes | `AqQrSheet` es específica de QR (X-03) | Media | `AqQrSheet`, `AqBottomSheet` |
| H4 | Carousel | `core/ui/.../AqLists.kt` → `AqCarousel` | COMPLETE | Sí | NO VERIFICADO | Parcial | `AqCarouselTest` | Usado en S01 carrusel de favoritos; ancho fijo por ítem, siguiente asoma | Sin cambios | Media | `AqIcon`, `AqTextStyles` |
| H5 | Media Attachment | `AqMediaFrame` | PARTIAL | Sí | NO VERIFICADO | Parcial | — | Marco de medios para QR y comprobantes; sin progreso de subida visual | `AqMediaFrame` cubre presentación | Media | `AqMediaFrame` |
| H6 | Thumbnail | `AqQrPreview` + `AqIcon(ImageOff)` | PARTIAL | Sí | NO VERIFICADO | Parcial | — | Sin miniatura dedicada; estado "sin imagen" con glifo | `AqQrPreview` muestra glifo cuando no decodifica | Baja | `AqQrPreview` |
| H7 | Document Preview | `ComprobanteViewer` (feature) | PARTIAL | Sí | NO VERIFICADO | Parcial | — | Visor de comprobante/PDF en feature, no en `core/ui` | No mover a core/ui sin requerimiento | Baja | Implementación de feature |
| H8 | File Attachment | — | NOT_APPLICABLE | — | — | — | — | UX V1 no ofrece carga/adjunto genérico de archivos; comprobantes usan su flujo propio | No añadir componente genérico sin caso de producto | — | — |
| H9 | Upload Progress | `AqProgressBar` / `AqProgressBlock` | PARTIAL | Sí | NO VERIFICADO | Sí (progressBarRangeInfo, liveRegion) | `ScreensA11yCoverageTest` | Barra de progreso determinada; sin subida de archivo real en V1 | `AqProgressBar` cubre progreso determinado | Media | `AqProgressBar` |
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
| I10 | Skeleton | `AqSkeleton` | PARTIAL | Sí | NO VERIFICADO | Parcial | `ScreensA11yCoverageTest` | Bloques estáticos sin movimiento decorativo continuo | Sin cambios | Media | `AqSkeleton` |
| I11 | Shimmer | — | NOT_APPLICABLE | — | — | — | — | UX V1 prohíbe bucles de animación decorativos (regla V1.3) | No implementar shimmer | — | — |
| I12 | Error State | `AqEmptyState` + `AqStatusBanner` | PARTIAL | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Estado de error con acción de recuperación contextual | Sin cambios | Media | `AqEmptyState`, `AqStatusBanner` |
| I13 | Empty State | `AqEmptyState` | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | — | Sin cambios | Alta | `AqEmptyState` |
| I14 | Confirmation State | `AqDialog` (confirmación) / `AqSuccessCheck` | COMPLETE | Sí | NO VERIFICADO | Sí | `ContextPickerDialogTest` | Confirmación destructiva con foco en acción conservativa | Sin cambios | Alta | `AqDialog` |
| I15 | Retry Action | `AqTextAction` / `AqBannerAction` | COMPLETE | Sí | NO VERIFICADO | Sí | `ActionHierarchyTest` | Acción de reintento como botón de texto o acción de banner | Sin cambios | Media | `AqTextAction`, `AqBannerAction` |
| I16 | Offline Indicator | `AqStatusBanner` (Notice-bar) | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Aviso persistente de modo local/sin conexión | Sin cambios | Alta | `AqStatusBanner` |
| I17 | Sync Status | `AqSyncIndicator` (X-02) | COMPLETE | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Estados local/pendiente/sincronizado/error/reintento | Sin cambios | Alta | `AqSyncIndicator`, `AqBadge` |
| I18 | Haptic Feedback | `AqFeedbackEvent` / `AqButtonImpl` (hapticFeedback) | PARTIAL | Sí | NO VERIFICADO | Sí | `LocalAccessBiometricTest`, `AqAccessibilityContractTest` | Háptica ligera en eventos y botones primarios; respetar ajustes del sistema | Sin cambios | Media | `LocalHapticFeedback` |

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
| J4 | Contextual Actions | `AqActionSheet` / `AqListRow(trailing=...)` | PARTIAL | Sí | NO VERIFICADO | Sí | `AqActionSheetTest` | Acciones contextuales como diálogo de lista o trailing en fila | Mantener; no añadir gestos contextuales sin justificación | Media | `AqActionSheet`, `AqListRow` |
| J5 | Drag and Drop | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara drag and drop | No implementar | — | — |
| J6 | Pull to Refresh | — | NOT_APPLICABLE | — | — | — | — | No hay pull-to-refresh en UX V1 | No implementar | — | — |
| J7 | Expand and Collapse | `AqSection` + colapsable local (S07) | PARTIAL | Sí | NO VERIFICADO | Parcial | `UxEditorDiscardTest` | "Más detalles" plegado localmente en feature | Evaluar `AqExpansionPanel` dedicado | Media | `AqSection` |
| J8 | Bottom Sheet Gestures | `AqBottomSheet` | PARTIAL | Sí | NO VERIFICADO | Parcial | `AqBottomSheetTest` | Descarta al acumular cuatro áreas táctiles mínimas de arrastre descendente sobre el handle; expansión controlada por el llamador, sin estado intermedio | Gesto de cierre probado en Android; accesibilidad de foco e iOS pendientes | Media | `AqBottomSheet` |
| J9 | Selection Gestures | — | NOT_APPLICABLE | — | — | — | — | UX V1 no declara selección por gestos | No implementar | — | — |
| J10 | Haptic Feedback | `AqFeedbackEvent` | PARTIAL | Sí | NO VERIFICADO | Sí | `LocalAccessBiometricTest` | Mismo componente que I18 | Sin cambios | Media | `LocalHapticFeedback` |
| J11 | Touch Feedback | `AqButtonImpl` (color activo, sin ripple) | COMPLETE | Sí | NO VERIFICADO | Sí | `AqButtonStyleTest` | Presión resuelta por cambio de token de color | Sin cambios | Alta | `AqButtonImpl` |
| J12 | Nested Scrolling | `AqScreenScaffold` (verticalScroll) | PARTIAL | Sí | NO VERIFICADO | Parcial | `LayoutReferenceTest` | Scroll de contenido en scaffold; sin anidado complejo | `AqScreenScaffold` gestiona scroll simple | Media | `AqScreenScaffold` |
| J13 | Predictive Back | `PredictiveBackHandler` (host) | PARTIAL | Sí | NO VERIFICADO | Sí | `RootNavigationChromeTest` | Compilado en 3 targets; sin verificación en dispositivo (ADR-0007) | Documentar iOS como no verificado | Alta | Host de plataforma |
| J14 | Edge-to-edge | `AqScreenScaffold` (contentWindowInsets) | COMPLETE | Sí | NO VERIFICADO | Sí | `LayoutReferenceTest` | Insets de contenido manejados por Scaffold | Sin cambios | Alta | `AqScreenScaffold` |
| J15 | Keyboard-aware Layout | `AqBottomActionDock` (imePadding) | COMPLETE | Sí | NO VERIFICADO | Sí | `LayoutReferenceTest` | IME consumido; acción fija no queda bajo teclado | Sin cambios | Alta | `AqBottomActionDock` |
| J16 | One-handed Interaction | `AqBottomAction` (acción alcanzable) | PARTIAL | Sí | NO VERIFICADO | Sí | `ScreensA11yCoverageTest` | Acción primaria fija abajo; no hay optimización específica de una mano | UX V1 prioriza acciones alcanzables | Media | `AqBottomAction` |
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
| COMPLETE | 57 | A15, B1-B2, B4, B6-B7, B9, B13-B16, C3, C8, C16-C18, C25-C27, D4, E1-E4, E15, F3-F7, F10, F12, F14, G1-G3, G5, G13, H4, H10, I1-I9, I13-I17, J11, J14-J15 |
| PARTIAL | 69 | A1-A2, A5-A7, A9-A12, B3, B5, B10-B11, C1-C2, C4-C5, C7, C9-C10, C12-C13, C15, C19, C23-C24, D1-D3, D5-D7, D11, E5-E7, E11-E14, F1, F9, F11, F13, G4, G6, G9-G11, G14-G16, H1-H3, H5-H7, H9, I10, I12, I18, J4, J7-J8, J10, J12-J13, J16 |
| MISSING | 0 | — |
| NOT_APPLICABLE | 36 | A3-A4, A8, A13-A14, B8, B12, C6, C11, C14, C20-C22, D8-D10, D12, E8-E10, E16, F2, F8, G7-G8, G12, G17, H8, I11, J1-J3, J5-J6, J9, J17 |

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
