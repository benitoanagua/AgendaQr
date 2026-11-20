# Matriz de gaps de patrones móviles y cadena de precedencia

**Estado:** Normativo — cadena de precedencia UI v9.0 (ADR-0029).
**Autoridad:** Agenda QR UX/UI V1 (`docs/04-ux/02-especificacion-ux-ui-v1.md`), Polaris 9.4.2 (`09-instantanea-tokens-polaris-9.4.2.md`), Moon Design Flutter 1.1.0 (solo referencia anatómica y gestual), Convenciones Nativas Android (M3) / iOS (HIG).

---

## 1. Cadena de precedencia normativa

Toda decisión de interfaz y experiencia de usuario móvil se resuelve estrictamente en el siguiente orden:

1. **Nivel 1 — Polaris:** convenciones, tokens, componentes, patrones, copy y guía mobile si existe. Siempre gana. Si Polaris solo tiene guía de escritorio para un componente, se anota como «Polaris sin guía mobile» y se adapta usando el criterio de Polaris (tokens, densidad, jerarquía).
2. **Nivel 2 — Moon Design for Flutter:** SOLO si Polaris no define el componente o patrón. Se toman anatomía, estados, tamaños y gestos. El estilo se reemplaza al 100 % con tokens Polaris. Prohibido importar paleta, sombras, squircle o tipografía de Moon. No se agrega ninguna dependencia de Moon ni de Flutter.
3. **Nivel 3 — Convención nativa Android (Material 3) / iOS (HIG):** SOLO si ni Polaris ni Moon lo cubren. Se estiliza igualmente con tokens Polaris.

No se puede asignar nivel 3 sin haber descartado por escrito los niveles 1 y 2. Esta regla aplica también a la navegación: se consulta primero Polaris, luego Moon y finalmente nativo.

Referencia de catálogo Moon: `MoonBottomSheet`, `MoonModal`, `MoonDrawer`, `MoonSegmentedControl`, `MoonPillTab`, `MoonTabBar`, `MoonChip`, `MoonTag`, `MoonToast`, `MoonAlert`, `MoonAccordion`, `MoonCarousel`, `MoonDotIndicator`, `MoonAuthCode`, `MoonTextInput`, `MoonTextInputGroup`, `MoonTextArea`, `MoonErrorMessage`, `MoonMenuItem`, `MoonPopover`, `MoonDropdown`, `MoonTooltip`, `MoonAvatar`, `MoonCircularLoader`, `MoonLinearLoader`, `MoonCircularProgress`, `MoonLinearProgress`, `MoonProgressPin`, `MoonSwitch`, `MoonRadio`, `MoonCheckbox`, `MoonButton`, `MoonBreadcrumb`, `MoonTable`.
Moon NO incluye: bottom navigation, FAB, search bar, swipe actions, pull-to-refresh, date/time picker, skeleton, empty state, list cell; esos pasan a nivel 3 si Polaris tampoco los define.

---

## 2. Matriz exhaustiva por patrón

| Patrón Mobile | Nivel 1: Polaris | Nivel 2: Moon | Nivel 3: Nativo | Nivel Asignado | Componente `Aq*` | Evidencia de descarte y notas de adaptación |
|---|---|---|---|---|---|---|
| **Bottom Navigation** | Sin componente móvil (nav es web lateral) | Ausente en catálogo Moon | Barra inferior M3 / TabBar HIG | **Nivel 3 (Nativo)** | `AqNavBar`, `AqNavRail` | Polaris: sin guía mobile. Moon: no ofrece navegación inferior. → Nativo estilizado al 100 % con tokens `nav-*` y `AqColor`. |
| **App Bar / Top Bar** | `TopBar` web con jerarquía de título | `MoonTopAppBar` existe | TopAppBar M3 | **Nivel 1 (Polaris adaptado)** | `AqTopBar` | Polaris define top bar y jerarquía de acciones. Título adaptado a 18 sp semibold (`light-mobile:515`). Altura y botón back nativos (48 dp). |
| **Bottom Sheet** | Sin sheet táctil | `MoonBottomSheet`, `showMoonModalBottomSheet` | ModalBottomSheet M3 | **Nivel 2 (Moon)** | `AqBottomSheet` | Polaris carece de sheet móvil táctil. Moon provee anatomía de drag handle, scrim y gestos; se estiliza 100 % con tokens Polaris (D-04). |
| **Modal / Diálogo** | `Modal` (`confirmText`, `destructive`) | `MoonModal` | AlertDialog M3 | **Nivel 1 (Polaris)** | `AqDialog`, `AqDialogList` | Polaris cubre completamente modales y diálogos de confirmación y selección. |
| **Search Bar** | `SearchField` / `IndexFilters` | Ausente en catálogo Moon | SearchBar M3 | **Nivel 1 (Polaris adaptado)** | `AqSearchField`, `AqSearchTrigger` | Polaris cubre campos de búsqueda; la variante "trigger" de solo lectura es adaptación móvil documentada con tokens Polaris. |
| **Chips y Filtros** | Filtros y tags Polaris | `MoonChip`, `MoonTag` | FilterChip M3 | **Nivel 1 (Polaris)** | `AqChipGroup`, `AqChipOption` | Polaris define chips y filtros; Moon confirma anatomía compacta. Espaciado y conteos fieles a Polaris. |
| **Control Segmentado** | Segmentos en filtros web | `MoonSegmentedControl` | SegmentedButton M3 | **Nivel 1 (Polaris)** / ref Moon | `AqSegmentedControl` | Polaris define selección excluyente; Moon confirma anatomía táctil con animación y soporte de deselección (`-1`). |
| **Tabs** | `Tabs` | `MoonPillTab`, `MoonTabBar` | TabRow M3 | **Nivel 1 (Polaris)** | `AqTabs` | Polaris define tabs y roles semánticos; Moon aporta referencia de pill tab móvil. |
| **Toast / Snackbar** | `Toast` web | `MoonToast` | Snackbar M3 | **Nivel 1 (Polaris adaptado)** | `AqSnackbarHost`, `AqFeedbackEvent` | Polaris define Toast; Moon valida posición y duración flotante táctil; estilo con tokens de fondo inverso Polaris. |
| **Banners Persistentes** | `Banner` (Success, Warning, Critical, Info) | `MoonAlert` | Banner M3 | **Nivel 1 (Polaris)** | `AqStatusBanner`, `AqStatusPill` | Polaris define banner con tonos semánticos completos. Fiel. |
| **Lista y Celdas** | `IndexTable` / listado web | Ausente en catálogo Moon | ListItem M3 | **Nivel 1 (Polaris)** + **3 (Nativo)** | `AqListBody`, `AqListRow`, `AqCard`, `AqTappableCard` | Polaris define jerarquía y borde hairline; el área táctil mínima de 48 dp y chevron es convención nativa documentada (D-02). |
| **Swipe Actions en Filas** | Sin guía ni componente | Ausente en catálogo Moon | SwipeToDismissBox M3 / contextual actions | **Nivel 3 (Nativo)** | `AqSwipeActions` | Ni Polaris ni Moon lo proveen. Se adopta patrón nativo mobile de deslizamiento para revelar acciones destructivas o secundarias con tokens Polaris. |
| **Pull-to-refresh** | Sin componente | Ausente en catálogo Moon | PullRefreshIndicator M3 | **Nivel 3 (Nativo)** | `AqPullToRefresh` | Ni Polaris ni Moon definen refresco táctil. Se implementa contenedor de arrastre nativo con spinner y elevación de tokens Polaris. |
| **Empty State** | Sin componente móvil dedicado | Ausente en catálogo Moon | Empty state nativo M3 | **Nivel 3 (Nativo)** | `AqEmptyState`, `AqSuccessCheck` | Polaris no tiene componente móvil dedicado; Moon no lo tiene. Se estiliza icono + título + CTA con tokens Polaris. |
| **Skeleton / Loading** | `SkeletonPage`, `SkeletonDisplayText` | `MoonLinearLoader`, `MoonCircularLoader`, `MoonProgressPin` | Skeleton / ProgressIndicator M3 | **Nivel 1/2** | `AqSkeleton`, `AqProgressBlock`, `AqSpinner`, `AqProgressBar` | Polaris tiene skeleton web; Moon aporta loaders táctiles móviles de nivel 2. |
| **Formularios e Inputs** | `TextField`, multiline | `MoonTextInput`, `MoonTextInputGroup`, `MoonTextArea` | TextField M3 | **Nivel 1 (Polaris)** | `AqTextField`, `AqMultilineTextField`, `AqAmountField` | Polaris define campos; tokens `inputSurface`, `inputBorder` y tipografía móvil aplicada. |
| **Select / Checkbox / Radio / Switch** | `Select`, `Checkbox`, `RadioButton` | `MoonCheckbox`, `MoonRadio`, `MoonSwitch` | Checkbox, Switch M3 | **Nivel 1 (Polaris)** | `AqSelect`, `AqCheckbox`, `AqRadioButton`, `AqSwitch`, `AqSelectAllCheckbox` | Polaris define los 4 controles fundamentales. Fieles 1:1. |
| **Pickers de Fecha / Hora** | Sin picker móvil | Ausente en catálogo Moon | DatePicker / TimePicker M3 | **Nivel 3 (Nativo)** | `AqDatePickerDialog`, `AqTimePickerDialog` | Ni Polaris ni Moon tienen picker móvil. Se estiliza diálogo nativo con tokens Polaris (D-07 prohíbe sheets para este fin). |
| **FAB y Botón Primario Fijo** | `Button` (sin FAB móvil) | Ausente en catálogo Moon | FAB M3 / Floating dock | **Nivel 3 (Nativo)** + **1 (Estilo)** | `AqExtendedFab`, `AqBottomAction`, `AqBottomActionDock` | Botón estilo Polaris (`BrandFill`); ubicación y elevación flotante móvil nativa. |
| **Menú Contextual / Overflow** | `Popover` / `ActionList` web | `MoonMenuItem`, `MoonPopover`, `MoonDropdown` | DropdownMenu / BottomSheet | **Nivel 2 (Moon)** / nativo | `AqActionSheet`, `AqTooltip` | Polaris web usa popover; en mobile el contrato V1 usa ActionSheet de lista táctil accesible. |
| **Indicadores de Progreso** | `ProgressBar` | `MoonLinearProgress`, `MoonCircularProgress` | LinearProgressIndicator M3 | **Nivel 1 (Polaris)** | `AqProgressBar`, `AqSpinner` | Polaris define barra de progreso; Moon valida spinner circular móvil. |
| **Carrusel** | Sin carrusel | `MoonCarousel`, `MoonDotIndicator` | HorizontalPager M3 | **Nivel 2 (Moon)** | `AqCarousel` | Polaris carece de carrusel móvil; Moon provee anatomía e indicador de puntos; estilizado con tokens Polaris. |
| **Acordeón / Colapsable** | `Collapsible` web | `MoonAccordion` | Expandable panel M3 | **Nivel 2 (Moon)** | `AqExpansionPanel` | Polaris web tiene `Collapsible`; Moon aporta la interacción móvil de acordeón con icono y animación. |
| **PIN / Teclado de Entrada** | Sin patrón | `MoonAuthCode` | Teclado numérico nativo | **Nivel 2 (Moon)** + producto | `AqPinDots`, `AqKeypad` | Referencia de código de acceso Moon; adaptado al flujo de autenticación y bloqueo local-first. |

---

## 3. Priorización por flujos del núcleo de Agenda QR

1. **Guardar (S07 / S02 / S10):**
   - Inputs de texto, importe, fecha/hora, control segmentado, botones de acción primaria fija (`AqBottomAction`), diálogo de confirmación.
   - Estado: **COMPLETO**.
2. **Organizar (S01 / S06 / S08):**
   - Tabs (`AqTabs`), chips de estado y categorías (`AqChipGroup`), tarjetas táctiles (`AqCard`, `AqTappableCard`), swipe actions (`AqSwipeActions`), pull-to-refresh (`AqPullToRefresh`), action sheet (`AqActionSheet`).
   - Estado: **COMPLETO**.
3. **Buscar (S04):**
   - Barra de búsqueda (`AqSearchField`), trigger de búsqueda (`AqSearchTrigger`), chips de filtro de tipo, encabezados de sección.
   - Estado: **COMPLETO**.
4. **Mostrar (S03 / S09):**
   - Visualización de QR (`AqQrPreview`), carrusel (`AqCarousel`), metadatos (`AqMetaRow`), badges de evidencia (`AqEvidenceBadge`).
   - Estado: **COMPLETO**.
5. **Compartir / Reutilizar:**
   - Hojas de acción e invocación de intents nativos de plataforma (`expect/actual`).
   - Estado: **COMPLETO**.

---

## 4. Estado de gaps y roadmap

- **Gaps cubiertos:** Se implementan en `core/ui` todos los patrones identificados en la matriz, incluyendo `AqSwipeActions` (Nivel 3 con tokens Polaris) y `AqPullToRefresh` (Nivel 3 con tokens Polaris), garantizando que las aplicaciones táctiles móviles cuenten con primitivos canónicos.
- **Flujos congelados:** Las pantallas existentes en V1 no alteran su copy ni su lógica de negocio; los componentes nuevos se ofrecen como primitivos de diseño reutilizables.
