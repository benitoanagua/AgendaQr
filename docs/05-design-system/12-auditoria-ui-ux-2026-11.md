# Auditoría UI/UX — Pantallas y componentes `Aq*`

**Estado:** Informativo (hallazgos verificados en código, 2026-11-20).
**Alcance:** `core/ui` y `feature/destinations/presentation`. No se audita `shared`, `domain`, `data` ni `supabase`.
**Referencia:** `ADR-0038` (Moon = UX/interacción; el sistema visual anterior = UI/apariencia; nativo = lo que ninguno cubre). Cadena de precedencia: `00-contrato-normativo-diseno.md` §6 y `11-matriz-gaps-mobile.md`.

---

## 1. Resumen de hallazgos

| ID | Hallazgo | Archivo | Severidad | Fase |
|---|---|---|---|---|
| A-01 | Glifo "ALT" como placeholder de imagen: `AqIcons.ImageOff` apunta a `MoonIconVectors.ImageAlt` (letras ALT, no imagen rota) | `core/ui/.../components/AqIcon.kt:65` | Crítica (UX) | 1 |
| A-02 | `Loading` y `Failure` de la miniatura muestran el mismo icono (ALT); el usuario no distingue "cargando" de "sin vista previa" | `ComprobanteThumbnail.android.kt:122-151` | Alta (UX) | 1 |
| A-03 | iOS nunca decodifica miniaturas: siempre placeholder (decodificación diferida sin fecha de hardware) | `ComprobanteThumbnail.ios.kt:24-42` | Alta (paridad) | 2 |
| A-04 | Decodificación Android en `RGB_565` (pérdida de calidad), `inSampleSize` sin densidad real, sin caché de memoria | `ComprobanteThumbnail.android.kt:48-81` | Media (rendimiento/calidad) | 2 |
| A-05 | `AqQrPreview` sin QR muestra `ImageOff` (ALT) + mensaje opcional | `AqQrPreview.kt:81-86` | Media (UX) | 1 |
| A-06 | `DestinationDetailScreen` sin contenido QR usa `EmptyState(icon = ImageOff)` | `DestinationDetailScreen.kt:154-161` | Media (UX) | 1 |
| A-07 | `OperationListScreen.kt` (691 líneas) mezcla lista de operaciones, bandeja sin asociar, selección por lote y diálogos en un solo archivo | `OperationListScreen.kt` | Media (mantenibilidad) | 4 |
| A-08 | Fila de la bandeja sin chevron: no comunica que la fila es tocable; CTA "Crear nueva actividad con esto" es píldora tonal de baja jerarquía dentro de la tarjeta | `OperationListScreen.kt:541-597` | Media (UX) | 3 |
| A-09 | Bandeja vacía sin CTA ni explicación de cómo llegan los comprobantes (Compartir / Importar) | `OperationListScreen.kt:525-526` | Media (UX) | 3 (propuesta) |
| A-10 | Selección por lote de la bandeja no tiene entrada por pulsación larga (el modo selección solo se activa programáticamente) | `OperationListScreen.kt:480-484` | Media (UX) | 3 (propuesta) |
| A-11 | Pantallas grandes sin secciones extraídas (`DestinationsScreen` 515, `OperationEditorScreen` 514, `ImportBatchScreen` 421) | varios | Baja (mantenibilidad) | 4 |
| A-12 | Nombres de `AqIcons` cuyo glifo real no coincide con el nombre semántico (ver tabla 2) | `AqIcon.kt` | Baja (consistencia) | 1 |

---

## 2. Iconografía de producto (`AqIcons`) vs. glifo real del sistema visual anterior Icons 9.3.1

Verificado contra `AqIconMappingTest` y `MoonIconVectors.kt`.

| `AqIcons.*` | Glifo el sistema visual anterior real | ¿Nombre semántico correcto? | Nota |
|---|---|---|---|
| `Search` | `SearchIcon` (lupa) | Sí | — |
| `Add` | `PlusIcon` (+) | Sí | — |
| `Register` | `BillIcon` (recibo/factura con totales) | Sí | Usado para "registrar operación" |
| `Favorite` | `StarIcon` | Sí | — |
| `Context` | `FolderIcon` | Sí | — |
| `Back` | `ArrowLeftIcon` | Sí | — |
| `Cancel` | `XCircleIcon` | Sí | Estado, distinto de `Close` |
| `Close` | `XIcon` (X simple) | Sí | Acción de cierre |
| `More` | `MenuHorizontalIcon` | Sí | — |
| `Home` | `HomeIcon` | Sí | — |
| `Gallery` | `ImageIcon` (cuadro + paisaje) | Sí | Imagen/galería |
| `Camera` | `CameraIcon` | Sí | — |
| `Info` | `InfoIcon` | Sí | — |
| `Warning` | `AlertTriangleIcon` | Sí | Severidad de errores |
| `CircleCheck` | `CheckCircleIcon` | Sí | — |
| `Check` | `CheckIcon` | Sí | Selección de chips |
| `QrCode` | `ShopcodesIcon` (matriz QR) | Sí | Código QR |
| `SearchX` | `AlertCircleIcon` | Sí | Búsqueda sin resultados / error |
| `AlertCircle` | `AlertCircleIcon` | Sí | Alias de severidad |
| `Inbox` | `ArchiveIcon` | Sí | Bandeja / archivo |
| **`ImageOff`** | **`ImageAltIcon` (letras "ALT")** | **NO** | Dibuja "ALT", no imagen rota. **A eliminar.** |
| `ScanLine` | `BarcodeIcon` | Sí | Código de barras |
| `Income` | `ArrowDownIcon` | Sí | — |
| `Expense` | `ArrowUpIcon` | Sí | — |
| `Attachment` | `AttachmentIcon` (grapa) | Sí | Archivo/PDF adjunto |
| `Store` | `StoreIcon` | Sí | — |
| `Calendar` | `CalendarIcon` | Sí | — |
| `Clock` | `ClockIcon` | Sí | — |
| `ChevronRight` | `ChevronRightIcon` | Sí | — |
| `ShieldCheck` | `ShieldCheckMarkIcon` | Sí | — |
| `Wallet` | `WalletIcon` | Sí | — |
| `Lock` | `LockIcon` | Sí | — |
| `Erase` | `BackspaceIcon` | Sí | Borrado de texto, no papelera |
| `NoteEdit` / `Edit` | `EditIcon` (lápiz) | Sí | Alias |
| `Trash` | `DeleteIcon` | Sí | Papelera |
| `Share` | `ShareIcon` | Sí | — |
| `Copy` | `DuplicateIcon` | Sí | — |
| `Maximize` | `MaximizeIcon` | Sí | — |

**Conclusión:** el único nombre cuyo glifo no corresponde a su semántica es `ImageOff` (dibuja "ALT"). No existe en la instantánea de iconos un glifo genérico "Document"/"Receipt" además de `Bill` (ya usado por `Register`) y `Attachment`; por Etapa 1 no se inventa glifo nuevo: el placeholder de comprobante sin vista previa usa `QrCode` (decisión de producto) y PDF/archivo usa `Attachment` (ya existente).

---

## 3. Pantallas y componentes `Aq*` — patrón Moon de referencia y desviación

Pantallas auditadas en `feature/destinations/presentation/src/commonMain/kotlin/.../presentation/`:

| Pantalla (archivo) | Líneas | Componentes `Aq*` verificados | Patrón Moon de referencia (`docs/07-decisiones`, `11-matriz-gaps-mobile.md`) | Desviación detectada | Fase |
|---|---|---|---|---|---|
| `UnassociatedScreen` (en `OperationListScreen.kt`) | 474-691 | `AqScreenScaffold`, `AqTopBar`, `AqListRow`, `AqListBody`, `AqEmptyState`, `AqDialogList`, `AqBottomAction`, `AqSelectionBar`, `ComprobanteThumbnail`, `AqCompactAction` | Fila de lista con avatar (`MoonAvatar`), acción secundaria (`AqCompactAction`), vacío con CTA (`AqEmptyState`), carga (`MoonCircularLoader`/`AqSkeleton`) | A-02, A-08, A-09, A-10; miniatura con ALT en Loading y Failure | 1, 3, 4 |
| `OperationListScreen` (lista operaciones) | 100-471 | `AqScreenScaffold`, `AqTopBar`, `AqSelectionBar`, `AqSearchField`, `AqSegmentedControl`, `AqSecondaryButton`, `AqCard`, `AqSkeleton`, `AqPullToRefresh`, `AqListBody`, `LazyColumn`, `AqListRow`, `AqSwipeActions`, `AqTonalTag`, `AqLoadMoreFooter`, `AqStatusBanner`, `AqExtendedFab` | Lista/celdas, carrusel no aplica, pull-to-refresh nativo | Ninguna de UI visual; archivo monolítico (A-07) | 4 |
| `DestinationDetailScreen.kt` | ~514 | `AqScreenScaffold`, `AqTopBar`, `AqTappableCard`, `AqQrPreview`, `AqEmptyState`, `AqCard`, `AqMetaRow`, `AqStatusPill`, `AqBottomActionDock`, `AqPrimaryButton`, `AqSecondaryButton`, `AqCompactAction`, `AqTextAction`, `AqDialog`, `AqBottomSheet` | Detalle (`MoonBottomSheet` de inspección), QR (`AqQrPreview`) | A-06 (ALT en EmptyState) | 1 |
| `DestinationsScreen.kt` | ~515 | `AqScreenScaffold`, `AqCarousel`, `AqChipGroup`, `AqListRow`, `AqSwipeActions`, `AqPullToRefresh`, `AqSkeleton`, `AqTappableCard`, `AqExtendedFab` | Carrusel (`MoonCarousel`/`MoonDotIndicator`) | Sin secciones extraídas (A-11) | 4 |
| `OperationEditorScreen.kt` | ~514 | `AqScreenScaffold`, `AqSegmentedControl`, `AqAmountField`, `AqTextField`, `AqBottomAction`, `AqDialogList`, `AqDatePickerDialog`, `AqTimePickerDialog`, `AqSectionHeader`, `AqExpansionPanel` | Formulario con label flotante, accordion (`MoonAccordion`) | Sin secciones extraídas (A-11) | 4 |
| `ImportBatchScreen.kt` | ~421 | `AqScreenScaffold`, `AqStatusBanner`, `AqListRow`, `AqListBody`, `AqBottomAction`, `AqEmptyState`, `AqDialogList`, `AqProgressBlock` | Progreso (`MoonLinearProgress`-style) | Sin secciones extraídas (A-11) | 4 |
| `AqQrPreview.kt` (core/ui) | ~97 | `AqMediaFrame`, `AqIcon`, `AqText` | QR (`AqQrPreview` ya existe) | A-05 (ALT) | 1 |
| `ComprobanteViewer.kt` | ~63 | `AqSkeleton`, `AqMediaFrame` | Carga (`AqSkeleton`) | Ninguna nueva | — |

Nota sobre el contrato funcional (`docs/04-ux`, inbox): la bandeja "Comprobantes sin asociar" declara **acción primaria: —**, **gestos: —** y componentes `AqScreenScaffold`, `AqListRow`, `AqEmptyState`, `AqDialogList`. Cualquier cambio de navegación, jerarquía de acciones, gestos o copy requiere decisión de producto documentada (D-17). Véase §5 de esta auditoría.

---

## 4. Índice de usos de iconos y placeholders por archivo

Verificado con `grep -rn "AqIcons\." feature core --include=*.kt` (código de producción + tests):

- `core/ui/.../components/AqIcon.kt`: definición de los 41 nombres de producto (incluye `ImageOff` → `ImageAlt`, el glifo ALT).
- `core/ui/.../components/AqQrPreview.kt:82`: `AqIcons.ImageOff` (estado no disponible).
- `feature/.../ComprobanteThumbnail.android.kt:130,151`: `AqIcons.ImageOff` en Loading y Failure; `AqIcons.Attachment` en PDF.
- `feature/.../ComprobanteThumbnail.ios.kt:37`: `AqIcons.ImageOff` (estado único, siempre).
- `feature/.../DestinationDetailScreen.kt:155`: `AqIcons.ImageOff` en EmptyState sin QR.
- `feature/.../OperationListScreen.kt`: navegación (Home, Search, Register, Context), fila (Expense, Income, Calendar, Clock, Trash, Edit, ChevronRight), CTA de bandeja (Add), vacío bandeja (Inbox).
- `feature/.../QrImportControls.android.kt/.ios.kt`: Gallery, Attachment, Camera.
- `feature/.../CameraQrCapture.android.kt`: Camera.
- `feature/.../AddDestinationScreen.kt`: QrCode.
- `feature/.../ImportReviewScreen.kt`: ScanLine.
- `core/ui/.../theme/AqDesignSystemGallery.kt`: más de 20 usos (More, Add, Share, Search, QrCode, Attachment, SearchX, Favorite, Trash…).
- Tests: `AqIconMappingTest`, `LayoutReferenceTest`, `RootNavigationChromeTest`, `AqNavRailLayoutTest`, `AqScaffoldOverlapContractTest`, `AqTooltipTest`, `AqSegmentedControlTest`, `AqSwipeActionsTest`, `AqAtomicSpacingContractTest`, `AqAccessibilityContractTest`, `AqUiPolishContractTest`, `AqPhase3ContractTest`.

---

## 5. Reglas del contrato congelado que limitan esta auditoría

- `docs/04-ux/02-especificacion-ux-ui-v1.md` (§12 dirección visual descongelada; contrato funcional congelado por `D-17`).
- Pantalla `inbox` (Comprobantes sin asociar): acción primaria `—`, gestos `—`, componentes `AqScreenScaffold`, `AqListRow`, `AqEmptyState`, `AqDialogList`.
- Regla de producto: «comprobante recibido ≠ dinero verificado» (no etiquetas «Verificado»/`«Pagado»`).
- Objetivos táctiles ≥ 48 dp; reducción de movimiento; QR negro sobre blanco con zona de silencio; «Mostrar QR» en ≤ 2 toques.

---

## 6. Correlación con el plan de ejecución

- **Etapa 1** corrige A-01, A-02, A-05, A-06, A-12 (iconografía semántica, placeholder QR, separación Loading/Failure, contenedor común `AqThumbnailFrame`).
- **Etapa 2** corrige A-03, A-04 (miniaturas reales en iOS con ImageIO; decodificación ARGB_8888 + caché Android).
- **Etapa 3** revisa A-08 (chevron en fila de bandeja, compatible con `onClick` existente); A-09 y A-10 requieren nuevo copy/gesto no contemplado en el contrato de inbox → se registran como propuestas en el reporte final, no se implementan sin decisión de producto.
- **Etapa 4** corrige A-07 y A-11 (extracción de archivos y secciones repetidas sin cambio de comportamiento).

---

<sup>1</sup> Líneas aproximadas de archivo; verificadas por lectura del árbol fuente en esta sesión.

---

## 7. Actualización 2026-11-21 — fidelidad a la spec UX V1 (§12)

Estado verificado en código: A-01…A-06 y A-12 (iconografía/miniaturas) ya están resueltos; A-08 (chevron de bandeja) también. Cambios de esta pasada:

| Hallazgo | Archivo | Corrección |
|---|---|---|
| Bandeja: una tarjeta por comprobante y botón tonal por fila (la spec §12 prohíbe envolver cada fila en tarjeta y duplicar acciones) | `UnassociatedScreen.kt` | Un único Cell-group; «Crear nueva actividad con esto» como acción de texto de la celda |
| S12: pendientes como tarjetas independientes con botón por elemento (la spec pide Cell-group con Tag textual) | `ImportBatchScreen.kt` | Un único Cell-group (`PendingItemCell`); acciones de texto en la celda; Tag textual conservado |
| Diálogos de eliminación por lote abiertos al componer (`Boolean?.let`) en Inicio, Actividades y Bandeja | `DestinationsScreen.kt`, `OperationListContent.kt`, `UnassociatedScreen.kt` | `if (…)`; tests de regresión `*_has_no_destructive_dialog_by_default` |
| Copy: eliminar comprobantes por lote decía «actividades» | `UnassociatedScreen.kt`, `AppStrings.kt` | `ComprobanteSingular` / `ComprobantesPlural` |
| Galería sin cobertura estricta de estados declarados | `ScreenContractGate.kt`, `agendaqr.design-system.gradle.kts` | Ver ADR-0007 (enmienda 2026-11-21) |

A-09/A-10 cerrados por ADR-0045 (copy de vacío + pulsación larga).

---

## 8. Actualización 2026-11-22 — corrección de geometría Moon (Síntomas S1–S10)

Corrección de causas raíz en `core/ui` (commit `bbabcc9`) y composición de pantallas en `feature/` (commit `921f0b6`). Todas las puertas de su respectiva suite están en verde salvo lo anotado como NO VERIFICADO.

| Síntoma | Causa confirmada | Archivo | Commit | Puerta de cobertura | Estado |
|---|---|---|---|---|---|
| S1 | Etiquetas de acciones rápidas y disparador de búsqueda, una letra por línea | `AqQuickAction.kt` (ancho fijo 48 dp) | `bbabcc9` | `AqLayoutGeometryTest` (quick_action_label_width_and_touch_target) | Corregido |
| S2 | Filtros y pestañas truncados; `weight(1f)` dentro de `horizontalScroll` | `AqTabBar.kt`, `AqPillTab.kt` | `bbabcc9` | `AqLayoutGeometryTest` (tab_bar / pill_tab_long_label_width) | Corregido |
| S3 | Bloques apilados a 0 dp; Column de contenido sin `verticalArrangement` | `AqScreen.kt` | `bbabcc9` | `AqLayoutGeometryTest` (screen_children_do_not_collapse_to_zero) | Corregido |
| S4 | Etiqueta lejos de su caja; `heightIn(48)` en la etiqueta | `AqTextInput.kt` | `bbabcc9` | `AqLayoutGeometryTest` (text_input_label_and_touch_target) | Corregido |
| S5 | Filas sin texto (ancho de título colapsado) | `AqMenuItem.kt` (Spacer de peso) | `bbabcc9` | `AqListRowTest` (menu_item_title_has_positive_width) | Corregido |
| S6 | Alerta con mensaje en columna estrecha (botones comparten la fila) | `AqAlert.kt` | `bbabcc9` | `AqLayoutGeometryTest` (alert_message_and_two_actions) | Corregido |
| S7 | Títulos de pantallas raíz pegados a x = 0 | `AqAppBar.kt` (sin `ScreenMargin`) | `bbabcc9` | `AqLayoutGeometryTest` (root_app_bar_title_exists) | Corregido |
| S8 | Puntos del PIN tapados por la primera fila del teclado | `AqPinEntry.kt` / `AqKeypad.kt` (sin separación) | `bbabcc9` | `AqLayoutGeometryTest` (pin_dots_and_keypad_compose_without_overlap) | Corregido |
| S9 | Subtítulo de «Muéstralo» = cabecera base64 de PNG | Causa de producción: mapeo del modelo de destino QR en `QrDisplayEffects` / detalle de destino; no se inventó texto | `921f0b6` | `AqLayoutGeometryTest` (no aplica a este síntoma de contenido) | Documentado (D6) |
| S10 | Dos botones primarios apilados (Añadir destino); título de sección repetido como label del campo (Editar destino); filtro CONTEXT en mayúsculas (Buscar) | `AddDestinationScreen.kt`, `DestinationEditorScreen.kt`, `GlobalSearchScreen.kt` | `921f0b6` | `feature/destinations/presentation:testDebugUnitTest` + `LayoutReferenceTest` | Corregido (D1, D4, D7) |

Decisiones de producto aplicadas (D1–D7): véase `ADR-0043`.


## Cierre A-07 / A-11 (extracción sin cambio de comportamiento)

- A-07: la bandeja vive en `UnassociatedScreen.kt`; `OperationListScreen.kt` es un envoltorio de 20 líneas.
- A-11: `DestinationsScreen.kt` (499→365; tarjeta y fila en `DestinationRow.kt`), `ImportBatchScreen.kt` (483→241; secciones en `ImportBatchSections.kt`), `OperationEditorScreen.kt` (484→381; `OperationCurrencySelector`, `OperationDateTimeSection` y `OperationDetailsSection` en `OperationEditorSections.kt`, sin estado propio).
- Pendiente (requiere emulador): regenerar capturas de la galería (`AqDesignSystemGallery`), por el cambio de icono de «sin resultados» y el copy de la bandeja vacía.
