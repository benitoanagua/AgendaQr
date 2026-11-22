# ADR-0042 — Nombres de componentes retirados y sus sustitutos Moon

**Estado:** ACEPTADO — 2026-11-22
**Relaciona:** ADR-0039, ADR-0040, ADR-0041.

## Decisión

Al retirar la lista completa de `legacySymbols` de `core/ui`, cada símbolo legacy fue:
- reemplazado por su equivalente Moon del catálogo (§1 del catálogo Moon) cuando existe;
- compuesto con piezas Moon (patrón §4 del catálogo) cuando no hay equivalente de nombre;
- retirado sin sustituto cuando el producto no lo usa (ver tabla).

La regla aplicada: ningún componente del producto se pierde; se reimplementa con anatomía Moon bajo un nombre que no colisione con `legacySymbols` ni con la lista de nombres reservados.

## Tabla de nombres (legacy → sustituto)

| Legacy retirado | Sustituto Moon / decisión | Nota |
|---|---|---|
| `AqPrimaryButton`, `AqSecondaryButton`, `AqDangerButton`, `AqOutlineButton`, `AqCompactAction` | `AqButton` (variant/tamaño) | Jerarquía de 4 niveles colapsada en un único componente con variantes |
| `AqTextAction` | `AqButton` (variant=Text) | Acción terciaria como botón de texto |
| `AqIconButton` | `AqButton` (iconOnly) o `AqIcon` + área táctil | Botón solo-icono con área táctil 48 dp |
| `AqLink` | `AqNavLink` | Acción de navegación textual (nuevo nombre, no colisiona) |
| `AqButtonGroup` | `AqActionGroup` | Grupo de acciones (nuevo nombre, no colisiona) |
| `AqCheckboxGroup` | `AqCheckboxSet` | Grupo de casillas (nuevo nombre) |
| `AqSelectAllCheckbox` | `AqSelectAll` | Casilla de selección total tri-estado (nuevo nombre) |
| `AqChipGroup` | `AqChipRow` | Grupo de chips (nuevo nombre) |
| `AqSectionHeader` | `AqSectionTitle` | Encabezado de sección (nombre existente en catálogo) |
| `AqEmptyState`, `AqLoading` | `AqEmptyPlaceholder`, `AqCircularLoader` | Nombres Moon del catálogo |
| `AqSelectionBar` | `AqSelectionToolbar` | Barra contextual de selección (nombre existente) |
| `AqStatusBanner`, `AqStatusPill` | `AqAlert`, `AqChip`/`AqTag` | Alerta tonal y etiqueta de estado |
| `AqSpinner` | `AqCircularLoader` | Indicador circular de carga |
| `AqDatePickerDialog`, `AqTimePickerDialog` | Composición con `AqModal` + `AqTextInput` | Patrón §4 del catálogo; no hay componente único |
| `AqDialog`, `AqDialogList` | `AqModal` | Modal sobre `androidx.compose.ui.window.Dialog` |
| `AqSnackbarHost`, `AqFeedbackHost`, `AqFeedbackEvent` | `AqToastHost` + `AqToast` | Flujo de toasts (duración 4000 ms, ADR-0041) |
| `AqActionSheet` | `AqBottomSheet` | Hoja inferior |
| `AqScreenScaffold`, `AqBottomActionDock`, `AqBottomAction` | `AqScreen` (zona de acción inferior) | Pantalla con dock propio |
| `AqTopBar` | `AqAppBar` | Barra superior de la app |
| `AqSearchField`, `AqSearchTrigger` | `AqSearchInput` | Campo de búsqueda |
| `AqTextField`, `AqAmountField` | `AqTextInput`, `AqAmountInput` | Campos de entrada |
| `AqSelect` | `AqDropdown` | Select desplegable |
| `AqRadioButton`, `AqChoiceGroup` | `AqRadio` | Radio button |
| `AqTabs` | `AqTabBar` / `AqPillTab` | Pestañas |
| `AqSection`, `AqSectionHeader` | `AqSectionTitle`, `AqListSection` | Secciones |
| `AqListRow`, `AqListBody`, `AqTappableCard`, `AqMetaRow` | `AqMenuItem` (+ `AqTag`/`AqAvatar` en slots) | Celda de lista |
| `AqTonalTag`, `AqContextBadge`, `AqEvidenceBadge`, `AqBadge`, `AqIconBadge` | `AqTag` / `AqAvatar` / `AqDotIndicator` | Etiquetas e insignias |
| `AqIndexTable` | `AqTable` | Tabla de datos |
| `AqExpansionPanel` | `AqAccordion` | Acordeón |
| `AqFieldMessages`, `AqFormField` | `AqFieldHelp` + `AqTextInput`/`AqTextArea` | Mensajes de campo |
| `AqChipGroup` | `AqChipRow` | Grupo de chips |
| `AqIconButton`, `AqNavBar`, `AqNavRail`, `AqExtendedFab`, `AqLink`, `AqButtonGroup`, `AqHeading`, `AqBlockStack`, `AqInlineStack`, `AqCard`, `AqShapes` | Sin equivalente de nombre evidente | Portados con nombre nuevo (AqNavLink, AqActionGroup, etc.) o patrón nativo; documentados por ficheta |

## Estrategia de selector de fecha/hora

No hay componente único Moon para date/time picker. Se compone con `AqModal` (modal con foco atrapado y scrim clicable) + controles Moon (`AqTextInput` para hora numérica, composición con `AqCircularLoader` para estados de carga). La navegación inferior/rail en tablet se compone con `AqTabBar`/`AqPillTab` sobre `AqScreen` (slot `navDestinations`). El FAB extendido se compone con `AqButton` (variant=Primary, tamaño Large, sombra `AqShadow.Md`).

## Nombres reservados

Cuando `verifyNoLegacyAnywhere` alcanza cero, `legacySymbols` se convierte en lista de **nombres reservados** (prohibidos para siempre) y se documenta en el gate.
