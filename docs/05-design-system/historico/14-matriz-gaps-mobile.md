> **HISTÓRICO — no normativo.** Este documento fue consolidado en [11-matriz-gaps-mobile.md](../11-matriz-gaps-mobile.md) y [10-auditoria-fidelidad.md](../10-auditoria-fidelidad.md). No es fuente normativa; se conserva solo como registro histórico. La autoridad vigente es el contrato normativo v9.0 y ADR-0029.


> **HISTÓRICO — no normativo.** Este documento fue consolidado en [](../11-matriz-gaps-mobile.md) y [](../10-auditoria-fidelidad.md). No es fuente normativa; se conserva solo como registro histórico. La autoridad vigente es el contrato normativo v9.0 y ADR-0029.


# Matriz de gaps de patrones móviles — cadena de precedencia

**Estado:** Normativo — AUD-0029.
**Autoridad:** UX/UI V1 (contrato funcional congelado), Polaris 9.4.2
(instantánea `09-`), Moon Design `moon_design` 1.1.0 (solo referencia de
patrón, sin dependencia), convención nativa Android/iOS (nivel 3).
**Propósito:** Para cada patrón móvil de las pantallas V1, recorrer la
cadena **Polaris → Moon → nativo** y registrar nivel, implementación `Aq*`
y estado. Complementa `12-matriz-cobertura-polaris-moon-agendaqr.md`
(cobertura por familia) con la **regla de precedencia** de ADR-0029.

## Cadena de precedencia (resumen de ADR-0029)

1. **Polaris** siempre gana; si solo hay guía de escritorio, se anota
   «Polaris sin guía mobile» y se adapta con sus tokens/densidad.
2. **Moon** solo si Polaris no define el componente/patrón: se toma
   anatomía, estados y gestos; el estilo se reemplaza 100 % por tokens
   Polaris. Sin dependencia `moon_design` ni Flutter.
3. **Nativo** (Material 3 / HIG) solo si los niveles 1 y 2 están
   descartados por escrito; se estiliza con tokens Polaris.

## Matriz

| Patrón | Polaris | Moon | Nivel | Implementación `Aq*` | Estado | Evidencia del descarte |
|---|---|---|---|---|---|---|
| Navegación inferior | Sin componente móvil (nav es web) | Moon no trae bottom navigation | **3 nativo** | `AqNavBar`, `AqNavRail` | COMPLETE | Polaris: sin guía mobile (D-03). Moon: ausente de su catálogo. → barra/riel nativos con tokens `nav-*`. |
| Barra superior / app bar | `TopBar` web | Ausente en catálogo Moon (Polaris define el patrón) | **1 Polaris** (adaptado, barra nivel 3) | `AqTopBar` | COMPLETE | Polaris define top bar; Moon 1.1.0 no incluye app bar en su catálogo. La anatomía móvil (back, overflow, 48 dp) se adapta con tokens. Título = `heading-lg` móvil (F-03). |
| Bottom sheet | Sin sheet móvil | `MoonBottomSheet`, `showMoonModalBottomSheet` | **2 Moon** | `AqBottomSheet` | COMPLETE (primitivo; sin consumidor en V1 por D-07) | Polaris sin sheet táctil; Moon sí → anatomía de hoja modal con scrim, handle y gesto de descarte; estilo 100 % tokens Polaris. |
| Modal / diálogo | `Modal` | `MoonModal` | **1 Polaris** | `AqDialog`, `AqDialogList` | COMPLETE | Polaris define modal; variantes destructive/confirm según contrato. |
| Barra de búsqueda | `SearchField`/`IndexFilters` web | Sin buscador en catálogo Moon | **1 Polaris** | `AqSearchField`, `AqSearchTrigger` | COMPLETE | Polaris cubre búsqueda; la variante «trigger» móvil es adaptación documentada. |
| Chips y filtros | `IndexFilters`/chips | `MoonChip`, `MoonTag` | **1 Polaris** | `AqChipGroup`, `AqChipOption` | COMPLETE | Polaris define filtros; Moon solo confirma el patrón de chip compacto. |
| Control segmentado | Segmentos en filtros web | `MoonSegmentedControl` | **1 Polaris** (patrón confirmado por Moon) | `AqSegmentedControl` | COMPLETE | Polaris usa selección excluyente; Moon valida la anatomía móvil; `-1` = sin selección. |
| Tabs | `Tabs` | `MoonPillTab`, `MoonTabBar` | **1 Polaris** | `AqTabs` | COMPLETE | Polaris define tabs; Moon confirma pill tab móvil. |
| Toast / snackbar | `Toast` web | `MoonToast` | **1 Polaris** (patrón) / extensión móvil | `AqSnackbarHost`, `AqFeedbackEvent` | COMPLETE | Polaris tiene Toast; la posición/gesto móvil se adapta (D-04 familia). |
| Banner persistente | `Banner` | `MoonAlert` | **1 Polaris** | `AqStatusBanner`, `AqStatusPill` | COMPLETE | Polaris define banner; Moon solo referencia la alerta móvil. |
| Lista y celdas | `IndexTable`/lista web | Sin list cell en Moon | **1 Polaris** (estructura) + **3** (celda táctil) | `AqListBody`, `AqListRow`, `AqCard`, `AqTappableCard`, `AqIndexTable` | COMPLETE | Polaris define tabla/lista; la celda táctil de 48 dp es adaptación nativa documentada (D-02). |
| Swipe actions en filas | Sin patrón | Moon no trae swipe actions | **3 nativo** | — | **GAP ABIERTO** | Polaris y Moon descartados; `SwipeToDismiss`/acciones reveladas M3 queda como candidato. UX V1 no lo exige → pospuesto, sin implementar. |
| Pull-to-refresh | Sin patrón | Moon no lo trae | **3 nativo** | — | **GAP ABIERTO** | Los tres niveles: solo convención nativa. App local-first con indicador de sync (X-02); UX V1 no declara gesto de refresco → pospuesto. |
| Empty state | Sin componente | Moon no lo trae | **3 nativo** | `AqEmptyState`, `AqSuccessCheck` | COMPLETE | Nivel 3 estilizado con tokens; icono + título + CTA según contrato. |
| Skeleton / loading | `SkeletonPage` web | `MoonCircularLoader`, `MoonLinearLoader`, `MoonProgressPin`, progress | **1/2** | `AqSkeleton`, `AqProgressBlock`, `AqSpinner`, `AqProgressBar`, `AqLoading` | COMPLETE | Polaris tiene skeleton web; Moon aporta loaders móviles como referencia de nivel 2. |
| Inputs / textarea | `TextField`, `TextArea`-equivalente | `MoonTextInput`, `MoonTextInputGroup`, `MoonTextArea` | **1 Polaris** | `AqTextField`, `AqMultilineTextField`, `AqAmountField`, `AqSearchField` | COMPLETE | Polaris define campos; Moon confirma agrupación móvil. |
| Select / checkbox / radio / switch | `Select`, `Checkbox`, `RadioButton`, `Switch`-equivalentes | `MoonRadio`, `MoonCheckbox`, `MoonSwitch` | **1 Polaris** | `AqSelect`, `AqCheckbox`, `AqRadioButton`, `AqSwitch`, `AqChoiceGroup`, `AqSelectAllCheckbox`, `AqTriState` | COMPLETE | Polaris define los cuatro controles. |
| Selects de fecha/hora | Sin picker móvil | Moon no trae pickers | **3 nativo** | `AqDatePickerDialog`, `AqTimePickerDialog` | COMPLETE | Descartados niveles 1 y 2; diálogo nativo estilizado (D-07 prohíbe sheet). |
| FAB / acción primaria fija | `Button` (sin FAB móvil) | Sin FAB en Moon | **3 nativo** (patrón) + **1** (estilo) | `AqExtendedFab`, `AqBottomAction`, `AqBottomActionDock` | COMPLETE | Estilo = botón Polaris; posición/gesto = convención móvil (D-03). UX V1.9 retiró FAB de S01 pero el primitivo sigue para listados. |
| Menú contextual / overflow | `Popover`/`ActionList` web | `MoonMenuItem`, `MoonPopover`, `MoonDropdown`, `MoonTooltip` | **2 Moon** (anclaje) sobre estilo Polaris | `AqActionSheet` (vía `AqTopBar.overflowActions`), `AqTooltip` | COMPLETE | Polaris web usa popover anclado; en móvil el contrato V1 usa action-sheet de lista (registrado en `03-contrato-interaccion-pantallas.md`). |
| Indicadores de progreso | `ProgressBar` | `MoonLinearProgress`, `MoonCircularProgress`, `MoonProgressPin` | **1 Polaris** | `AqProgressBar`, `AqProgressBlock`, `AqSpinner` | COMPLETE | Polaris define progress; Moon confirma variantes móviles. |
| Carrusel | Sin carrusel móvil | `MoonCarousel`, `MoonDotIndicator` | **2 Moon** | `AqCarousel` | COMPLETE | Polaris sin guía móvil; Moon aporta anatomía; estilo Polaris (S01). |
| Acordeón / panel expandible | `Collapsible` web | `MoonAccordion` | **2 Moon** | `AqExpansionPanel` | COMPLETE | Polaris `Collapsible` es web; Moon `MoonAccordion` da la anatomía móvil (ADR-0028). |
| PIN / teclado de acceso | Sin patrón | `MoonAuthCode`, `MoonProgressPin` | **2 Moon** (referencia) + extensión | `AqPinDots`, `AqKeypad` | COMPLETE | Patrón de código de acceso; copy y flujo propios del acceso local. |
| Compartir / acciones externas | Sin patrón | — | **3 nativo** | Intents/ports de plataforma (`expect/actual`) | COMPLETE | Fuera de `core/ui`; boundaries de plataforma. |

## Priorización (frecuencia en flujos núcleo)

1. **Guardar** (S07/S02/S12): campos, segmented, bottom action, dialog de
   confirmación — todos COMPLETE.
2. **Organizar** (S01/S06/S08): tabs, chips, listas, action sheet — COMPLETE.
3. **Buscar** (S04): search field, chips de tipo, section headers — COMPLETE.
4. **Mostrar** (S03/detalle): QR preview, carousel, meta rows — COMPLETE.
5. **Compartir**: intents nativos — COMPLETE (boundary).

## Gaps abiertos

| Gap | Nivel | Motivo | Recomendación |
|---|---|---|---|
| Swipe actions en filas de lista | 3 nativo | Ni Polaris ni Moon lo definen; UX V1 no declara gesto de fila | Si se adopta, crear `AqSwipeActions` nivel 3 con tokens Polaris y semántica `CustomActions`; requiere caso de producto primero. |
| Pull-to-refresh | 3 nativo | Solo convención nativa; la app es local-first con sync push (X-02) | No implementar sin requisito de UX; el indicador de sincronización ya comunica el estado. |
| Menú anclado (popover) junto al elemento | 2 Moon (`MoonPopover`/`MoonDropdown`) | El contrato V1 resuelve el overflow con action-sheet de lista | Solo si aparece un caso de menú contextual anclado en una pantalla futura. |

## Decisiones registradas

- `AqBottomSheet` se conserva como primitivo del nivel 2 aunque V1 no lo
  consuma: D-07 prohíbe sheets en flujos congelados, pero el componente es
  la respuesta nivel-2 documentada para futuros patrones de hoja.
- «Polaris sin guía mobile» se anota en cada fila donde Polaris solo
  define escritorio (top bar, banner, collapsible, toast): la anatomía
  táctil se toma de Moon/nativo y el estilo es 100 % tokens Polaris.
