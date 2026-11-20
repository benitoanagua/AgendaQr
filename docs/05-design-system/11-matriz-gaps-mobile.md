# Matriz de gaps de patrones móviles y cadena de precedencia

**Estado:** Normativo — cadena de precedencia UI (ADR-0029).
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
| **App Bar / Top Bar** | `TopBar` web con jerarquía de título | Ausente en catálogo Moon (no define top bar) | TopAppBar M3 | **Nivel 1 (Polaris adaptado)** | `AqTopBar` | Polaris define top bar y jerarquía de acciones; Moon 1.1.0 no incluye top bar en su catálogo. Título adaptado a 18 sp semibold (`light-mobile:515`). Altura y botón back nativos (48 dp). |
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

| Flujo | Superficies | Patrones y componentes | Estado | Evidencia |
|---|---|---|---|---|
| **Guardar** | S07 / S02 / S10 | Inputs de texto, importe, fecha/hora, control segmentado (`AqSegmentedControl`), botón primaria fija (`AqBottomAction`), diálogo de confirmación (`AqDialog` destructive) | COMPLETO | uso en pantalla + pruebas (`UxEditorDiscardTest`, `ActionHierarchyTest`) + capturas |
| **Organizar** | S01 / S06 / S08 | Tabs (`AqTabs`), chips (`AqChipGroup`), tarjetas (`AqCard`, `AqTappableCard`), swipe actions (`AqSwipeActions`), pull-to-refresh (`AqPullToRefresh`), action sheet (`AqActionSheet`) | IMPLEMENTADO EN PANTALLA | `AqSwipeActions` y `AqPullToRefresh` en S01 y S07-list; pruebas Robolectric adaptadas (`DestinationsScreenA11yTest`, `ScreensA11yCoverageTest`); capturas pendientes de regenerar en Fase de galería |
| **Buscar** | S04 | Barra de búsqueda (`AqSearchField`), trigger (`AqSearchTrigger`), chips de filtro, encabezados de sección | COMPLETO | uso en pantalla + pruebas + capturas |
| **Mostrar** | S03 / S09 | QR (`AqQrPreview`), carrusel (`AqCarousel`), metadatos (`AqMetaRow`), badges (`AqEvidenceBadge`) | COMPLETO | uso en pantalla + pruebas + capturas |
| **Compartir / Reutilizar** | — | Hojas de acción e intents nativos (`expect/actual`) | COMPLETO | boundary de plataforma |

Regla de aceptación para marcar **COMPLETO** (ADR-0031): uso verificado en pantalla + prueba que cubre el gesto/estado + captura clara/oscura a 360 dp. Mientras no exista la captura, el flujo se anota como IMPLEMENTADO EN PANTALLA y no como COMPLETO.

---

## 4. Backlog de experiencia — candidatos evaluados

| # | Problema observado | Pantalla | Mejora propuesta | Convención (nivel / referencia) | Impacto | Esfuerzo | Riesgo | Cumple (a–e) | Decisión |
|---|---|---|---|---|---|---|---|---|---|
| 1 | Filas de destino sin acciones al deslizar; favorito solo visible, no accesible por gesto | S01 | `AqSwipeActions` con favorito + eliminar; `customActions` como alternativa no gestual | Nivel 3 (nativo swipe) + tokens Polaris | 4 | 2 | 2 | Sí | **ADOPTAR** — S01 |
| 2 | Sin gesto de recarga en listas; el usuario no sabe si hay datos frescos | S01, S07-list | `AqPullToRefresh` disparando el intent de búsqueda/sync existente | Nivel 3 (nativo pull-to-refresh) + spinner Polaris | 4 | 2 | 2 | Sí | **ADOPTAR** — S01, S07-list |
| 3 | Acciones secundarias de detalle dispersas; sin hoja de inspección ligera | S03 destino, S09 operación | `AqBottomSheet` para detalle secundario; `AqActionSheet` para overflow | Nivel 2 Moon (`MoonBottomSheet`, `MoonMenuItem`) + tokens Polaris | 3 | 2 | 2 | Sí | **ADOPTAR** — S03 hoja, S09 eliminar |
| 4 | Estados de carga con parpadeo o hueco en carga inicial | ComprobanteViewer, ContextScreen | `AqSkeleton` con líneas reales del contenido | Nivel 1 Polaris (`SkeletonPage`/`SkeletonDisplayText`) | 3 | 1 | 1 | Sí | **ADOPTAR** |
| 5 | Progreso de guardado de lote sin indicador real | S12 ImportBatch | `AqProgressBlock` con fracción real (N de M) | Nivel 1 Polaris (`ProgressBar`) | 3 | 1 | 1 | Sí | **ADOPTAR** |
| 6 | Progreso de revisión de QR sin indicador | S09 ImportReview | `AqProgressBar` durante el guardado | Nivel 1 Polaris | 2 | 1 | 1 | Sí | **ADOPTAR** |
| 7 | Atributo favorito del destino oculto en el editor | S02/S08 editor | `AqSwitch` con estado binario real (favorito) | Nivel 1 Polaris (`Switch`) | 2 | 1 | 1 | Sí | **ADOPTAR** |
| 8 | Copiado de contenido QR sin feedback ni alternativa accesible al icono | S03 detalle | `AqTooltip` sobre icono de copiado + evento de feedback honesto | Nivel 2 Moon (`MoonTooltip`) + tokens Polaris | 2 | 1 | 2 | Sí | **ADOPTAR** |
| 9 | Selección múltiple no existente; cada acción un toque por elemento | S01, S07-list, bandeja | Modo de selección con barra de acciones masiva (favorito/asignar contexto/eliminar por lote con confirmación única) | Nivel 1 Polaris (`IndexTable`/`ResourceList` selección) — por verificar en snapshot 9.4.2 | 4 | 4 | 3 | Pendiente (verificar convención) | **DESCARTAR por ahora** — no hay referencia confirmada en Polaris 9.4.2 ni caso de producto que exceda los límites duros (eliminar por lote requiere confirmación por elemento o ADR de política) |
| 10 | Deshacer tras acciones sin confirmación vigente (toggle favorito, descartar duplicado) | S01, S07-list, S12 | Toast con acción de deshacer; commit diferido en ViewModel | Nivel 1 Polaris (`Toast`) + posición/duración `MoonToast` | 4 | 3 | 2 | Sí | **ADOPTAR** — requiere intent de ViewModel que reutilice lo existente |
| 11 | Badge de navegación sin conteo de pendientes | NavBar | Insignia con conteo de comprobantes sin asociar o pendientes de importación | Nivel 1 Polaris (`Badge`) — por verificar | 3 | 2 | 2 | Pendiente (verificar) | **DESCARTAR por ahora** — `AqNavBar`/`AqNavRail` no muestran conteos; UX V1 no declara notificaciones en navegación |
| 12 | Validación en línea, ayuda y contador en campos | formularios | `InlineError` + ayuda + contador junto al campo; foco al primer error | Nivel 1 Polaris (`TextField` con error) / `MoonErrorMessage` | 3 | 3 | 2 | Sí | **ADOPTAR** — solo si existe punto de entrada de validación en el ViewModel; de lo contrario ADR propuesto |
| 13 | Encabezados de sección fijos (sticky) en listas largas | S07-list actividades | Encabezados agrupados por mes con conteo | Nivel 3 (nativo) si Polaris/Moon no lo definen | 2 | 3 | 3 | Pendiente | **DESCARTAR por ahora** — UX V1 no declara sticky headers; gestos no son la única vía |

Candidatos aceptados se justifican con la heurística que mejoran (visibilidad de estado, retroalimentación, alcance del pulgar, menos toques, consistencia, jerarquía) y la convención citada.

---

## 5. Decisión por los 13 primitivos de `core/ui` sin uso en producción

| Primitivo | Decisión | Pantallas y acciones donde quedó visible | Prueba | Captura |
|---|---|---|---|---|
| `AqSwipeActions` | **ADOPTAR** | S01 filas (favorito, eliminar con confirmación); S07-list filas (editar, eliminar) | `DestinationsScreenA11yTest` (customAction), `ScreensA11yCoverageTest` | S01 con acciones reveladas; S07-list con acciones |
| `AqPullToRefresh` | **ADOPTAR** | S01 (recarga/sync por arrastre); S07-list (recarga de actividades) | Robolectric de presentación (invoca intent de recarga) | S01 refrescando; S07-list refrescando |
| `AqBottomSheet` | **ADOPTAR** | S03 detalle de destino (inspección sin abandonar flujo) | Robolectric de presentación | detalle con hoja de opciones |
| `AqActionSheet` | **MANTENER** (ya en uso) | overflow de detalles (destino, operación) | `AqActionSheetTest` | detalle con overflow |
| `AqTooltip` | **ADOPTAR** | S03 detalle (alternativa accesible al icono de copiado) | Robolectric de presentación | tooltip de copiado |
| `AqSpinner` | **MANTENER** (en `AqLoading`) | estados de carga estandarizados | `AqAccessibilityContractTest` | carga con spinner |
| `AqProgressBar` | **ADOPTAR** | S09 ImportReview (guardado) | Robolectric de presentación | progreso de importación |
| `AqProgressBlock` | **ADOPTAR** | S12 ImportBatch (progreso real N de M) | Robolectric de presentación | importación con progreso real |
| `AqSkeleton` | **ADOPTAR** | ComprobanteViewer, ContextScreen (carga inicial) | Robolectric de presentación | carga con skeleton |
| `AqSwitch` | **ADOPTAR** | S02/S08 editor de destino (favorito) | Robolectric de presentación | editor con interruptor |
| `AqSelect` | **MANTENER** | sin consumo en pantallas de producción, pero control de formulario base de Polaris (nivel 1) con pruebas dedicadas y uso en galería de previews | `AqSelectTest` | galería de previews |
| `AqCheckbox` | **MANTENER** | sin consumo en pantallas de producción, pero control de formulario base de Polaris (nivel 1) con pruebas dedicadas y uso en galería de previews | pruebas de `core/ui` | galería de previews |
| `AqRadioButton` | **MANTENER** | sin consumo en pantallas de producción, pero control de formulario base de Polaris (nivel 1) con pruebas dedicadas y uso en galería de previews | pruebas de `core/ui` | galería de previews |
| `AqSelectAllCheckbox` | **MANTENER** | sin consumo en pantallas de producción, pero control de formulario base de Polaris (nivel 1) con pruebas dedicadas y uso en galería de previews | `AqSelectAllCheckboxTest` | galería de previews |

Regla: un componente sin consumo real en pantalla se adopta o se retira. Los retirados se eliminan junto con su ficha, pruebas, entrada de galería y referencias en docs/contrato (criterio aplicado ya a `AqModalSheet`, `AqQrSheet`, `AqHeroHeader`, `AqDivider`, `AqVerticalDivider`).

---

## 6. Estado de gaps y roadmap

- **Gaps cubiertos:** Se implementan en `core/ui` todos los patrones identificados en la matriz, incluyendo `AqSwipeActions` (Nivel 3 con tokens Polaris) y `AqPullToRefresh` (Nivel 3 con tokens Polaris), garantizando que las aplicaciones táctiles móviles cuenten con primitivos canónicos.
- **Flujos congelados:** Las pantallas existentes en V1 no alteran su copy ni su lógica de negocio; los componentes nuevos se ofrecen como primitivos de diseño reutilizables.
- **Gaps abiertos pendientes:** selección múltiple por lote (pende de referencia Polaris 9.4.2 y caso de producto), badge de navegación (UX V1 no lo declara), validación en línea con foco al primer error (pende de punto de entrada en ViewModel), encabezados sticky (UX V1 no los declara). Cada uno registrado como candidato sin implementar hasta que haya evidencia de convención y caso de producto.
