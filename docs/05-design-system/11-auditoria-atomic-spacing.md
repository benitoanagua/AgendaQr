# Auditoría de espaciado atómico y densidad de `core/ui`

**Estado:** informe de verificación, anexo al contrato normativo.
**Autoridad:** ADR-0027 (propiedad del espacio), ADR-0025 (adopción de
Polaris), ADR-0026 (cierre de la migración), contrato normativo v8.3 e
instantánea `@shopify/polaris-tokens@9.4.2`.
**Alcance:** composables de `core/ui/src/commonMain` y
`feature/destinations/presentation/src/commonMain` (140 `@Composable` en
total, 112 en la jerarquía de producto), más los hosts de estado de la capa de
presentación.
**Fecha:** 2026-11-18.

## 1. Método

1. **Inventario por nivel.** Se leyeron todos los `@Composable` de
   `commonMain` y se clasificaron como átomo, molécula, organismo, plantilla o
   página. Los que mezclan responsabilidades se registran en §3.6.
2. **Barrido estático.** Búsqueda de literales `.dp`, `.sp`, `Color(...)`,
   `padding(número)`, `PaddingValues(número)` y `spacedBy(número)` en toda la
   producción, excluyendo `AqTokens.kt` (única capa autorizada a declarar
   valores visuales).
3. **Revisión de geometría.** Medición real de límites con Robolectric a
   360 dp, claro/oscuro y escala de fuente 100/150 %, siguiendo ADR-0007
   (aserciones de límites, no capturas versionadas).
4. **Corrección de abajo arriba.** Primero átomos, después moléculas,
   organismos, plantillas y páginas; tras cada nivel se compila y se ejecutan
   las pruebas de `core/ui`.
5. **Blindaje.** Contratos por nivel (`AqAtomicSpacingContractTest`,
   `AqUiPolishContractTest`, `AqContrastPairsTest`), gate estático de
   espaciado y capturas de referencia regeneradas.

Criterios de severidad:

- **alta:** rompe compilación o produce un defecto visual inequívoco.
- **media:** inconsistencia medible, doble margen o responsabilidad de layout
  en el nivel equivocado.
- **baja:** deuda de limpieza o riesgo latente sin impacto visual actual.

## 2. Regla de propiedad del espacio

Cada nivel posee un único tipo de espacio. La regla completa y sus motivos
están en ADR-0027; este es el resumen normativo:

| Nivel | Posee | No posee |
|---|---|---|
| Átomo | Relleno interno mínimo, tamaño intrínseco y altura de control | Margen externo, separación entre hermanos |
| Molécula | Separación y alineación entre sus átomos; altura de fila de campo | Margen de sección, fondo, borde o radio de tarjeta |
| Organismo | Relleno de sección/tarjeta (16 dp), separación interna de moléculas, fondo, borde, radio y sombra | Margen de pantalla, insets del sistema |
| Plantilla | Margen de pantalla (16 dp), separación entre organismos, insets, ancho máximo y slots de navegación | Relleno interno de tarjetas o campos |
| Página | Solo compone plantillas y organismos; aporta copy y estado | Cualquier número de espaciado suelto o `Modifier.padding` numérico |

Reglas derivadas:

- El margen de pantalla se aplica **una sola vez**: o lo pone la plantilla, o
  lo pone el host edge-to-edge. Un componente nunca lo pide por parámetro.
- El relleno de sección es 16 dp (`AqSpacing.Lg`); la densidad vertical de
  fila es 12 dp (`AqSpacing.MdPlus`); la separación mínima entre átomos es
  4 dp (`AqSpacing.Sm`).
- La agrupación explícita de tarjetas usa 16 dp (D-12); cuando la página no
  declara agrupación, `AqScreenScaffold` aplica el mismo paso por defecto de
  16 dp (`AqSpacing.Lg`) entre organismos.
- Los mensajes de ayuda/error pertenecen a la molécula del campo y se
  separan 4 dp del control; el grupo de opciones pinta el mensaje una sola
  vez.
- Ningún valor visual fuera de `AqTokens.kt`, ni siquiera un margen de 0.

## 3. Clasificación por nivel

### 3.1 Átomos (30)

`AqText`, `AqHeading`, `AqIcon`, `AqDivider`, `AqVerticalDivider`,
`AqHairline`, `AqBadge`, `AqTonalTag`, `AqIconBadge`, `AqContextBadge`,
`AqStatusPill`, `AqProgressBar`, `AqSpinner`, `AqLink`, `AqButton`,
`AqButtonImpl`, `AqPrimaryButton`, `AqDangerButton`, `AqSecondaryButton`,
`AqLightButton`, `AqOutlineButton`, `AqCompactAction`, `AqTextAction`,
`AqIconButton`, `AqFilterChip`, `AqFavoriteToggle`, `AqExtendedFab`,
`KeypadKey` (privado), `AqSuccessCheck`, `AqPinDots`.

Los átomos solo declaran tamaño intrínseco, tinte y altura de control. El
único relleno admitido es el interno de una píldora o etiqueta.

### 3.2 Moléculas (25)

`AqTextField`, `AqSearchField`, `AqSearchTrigger`, `AqAmountField`,
`AqSelect`, `AqCheckbox`, `AqRadioButton`, `AqSwitch`, `AqChoiceGroup`,
`AqCheckboxGroup`, `AqFieldMessages`, `AqMetaRow`, `AqListRow`,
`AqProgressBlock`, `AqButtonGroup`, `AqBlockStack`, `AqInlineStack`, `AqBox`,
`AqSegmentedControl`, `AqDialogList`, `AqDialogAction`, `AqKeypad`,
`AqEvidenceBadge`, `AqSyncIndicator`, `AqStatusBanner` (límite).

### 3.3 Organismos (29)

`AqCard`, `AqTappableCard`, `AqListBody`, `AqQuickAction`, `AqSection`,
`AqSectionHeader`, `AqBottomActionDock`, `AqBottomAction`, `AqTopBar`,
`AqNavBar`, `AqNavRail`, `AqHeroHeader`, `AqBottomSheet`, `AqModalSheet`,
`AqActionSheet`, `AqDialog`, `AqDatePickerDialog`, `AqTimePickerDialog`,
`AqQrSheet`, `AqQrPreview`, `AqScannerFrame`, `AqMediaFrame`, `AqSkeleton`,
`AqLoadMoreFooter`, `AqEmptyState`, `AqLoading`, `AqSnackbarHost`,
`AqIndexTable`, `AqTabs`.

### 3.4 Plantillas (4)

`AqScreenScaffold`, `AuthenticatedAppRoot`, `AgendaQrApp`,
`AgendaQrLocalApp`.

### 3.5 Páginas (24)

`DestinationsScreen`, `DestinationDetailScreen`,
`DestinationEditorScreen`/`DestinationEditorContent`, `ContextsScreen`,
`ContextList`, `ContextDetail`, `OperationsScreen`, `OperationListScreen`,
`UnassociatedScreen`, `OperationDetailScreen`, `OperationEditorScreen`,
`AddDestinationScreen`, `ImportBatchScreen`, `ImportReviewScreen`,
`GlobalSearchScreen`, `AuthScreen`, `AccountScreen`, `LocalOnboardingScreen`,
`LocalLockScreen`, `HomeSurface`, `DestinationNotFound`,
`QrFullscreenPattern`, `AppStatusHost`.

### 3.6 Mixtos y no clasificables

| Composable | Motivo | Tratamiento |
|---|---|---|
| `AqStatusBanner` | Molécula con relleno de bloque tonal y acciones | Se trata como organismo para el margen: el host lo aplica |
| `AqTabs` | Pestañas (molécula) + contenido de página + divisor | Organismo compuesto; el margen lo pone la plantilla |
| `AqSection` | Agrupa cabecera y contenido sin superficie propia | Organismo de composición |
| `AqExtendedFab` | Átomo flotante posicionado por el scaffold | Átomo; el scaffold le reserva espacio |
| `AqEmptyState`/`AqLoading`/`AqSkeleton`/`AqLoadMoreFooter` | Estado/bloque con relleno externo propio | Organismos de estado; relleno documentado |
| `AqSnackbarHost` | Infraestructura de feedback | Host de organismo |
| `AqTone.content()`/`AqTone.container()` | Resolución de color, sin UI | No clasificable |
| `AqTheme`, `rememberAqInterFontFamily`, `Modifier.aqElevation`, `aqWindowWidthSizeClass`, `rememberAqClipboard`, `ProvideReducedMotion` | Plumbing/provider | No clasificable |
| `AqFeedbackEvent` | Efecto no visual | No clasificable |
| Galerías de `AqPreviews.kt` | Solo preview | Fuera de la jerarquía de producto |
| `AppStatusHost` | Host de estados con margen manual | Organismo con responsabilidad de host |
| `DestinationRow`/`FavoriteDestinationCard` | Filas/tarjetas de feature sobre componentes del DS | Molécula de feature |
| `OperationTypeSelector` | Selector segmentado con copy de producto | Molécula de feature |
| `EditorSection`/`AuthForm`/`BatchResultContent`/`BatchReviewContent`/`PendingItemCard`/`DecodedEvidenceCard` | Agrupaciones locales de feature | Organismos de feature |

## 4. Tabla de auditoría

Leyenda de estados: **N** normal, **F** foco, **E** error, **D**
deshabilitado, **P** presionado, **S** seleccionado. «—» = no aplica.
`file:line` es la línea de `fun` en el estado corregido.

### 4.1 Átomos

| Componente | Archivo:línea | Padding/margen/spacedBy | Altura | Radio | Roles de color | Violación de propiedad | Estados | Tema | Severidad |
|---|---|---|---|---|---|---|---|---|---|
| `AqText` | `AqText.kt:53` | — | línea del rol | — | `TextPrimary`/`TextSecondary`/`TextPlaceholder` | — | N | claro/oscuro | — |
| `AqHeading` | `AqText.kt:26` | — | línea del rol | — | `TextPrimary` | — | N | ambos | — |
| `AqIcon` | `AqIcon.kt:20` | — | 24/20 dp intrínseco | — | `LocalContentColor`/tinte | — | N | ambos | — |
| `AqBadge` | `AqLists.kt:56` | interno `Sm`/`Xs` | intrínseca | `Medium` | tono (`content`/`border`) | — | N | ambos | — |
| `AqTonalTag` | `AqLists.kt:91` | interno `Sm`/`Xs` | intrínseca | `Medium`/`Pill` | tono (`container`/`content`) | — | N | ambos | — |
| `AqIconBadge` | `AqLists.kt:123` | — | 48 dp (`GridIconContainerSize`) | `Large` | tono (`container`/`content`) | — | N | ambos | — |
| `AqContextBadge` | `AqLists.kt:148` | — | 40 dp (`ContextBadgeSize`) | `Pill` | acento de contexto | — | N | ambos | — |
| `AqStatusPill` | `AqStatus.kt:154` | interno `Sm`/`Xs` | intrínseca | `Pill` | tono (`container`/`content`) | — | N | ambos | — |
| `AqButton` | `AqButtons.kt:129` | `contentPadding` de tokens | 48/40/32/24 dp | `Medium` | `brandFill`/`onBrandFill`/`dangerFill`/`surface*` | — | N/F/P/D/carga | ambos | — |
| `AqPrimaryButton` | `AqButtons.kt:290` | hereda | 48 dp | `Medium` | `brandFill`/`onBrandFill` | — | N/P/D | ambos | — |
| `AqDangerButton` | `AqButtons.kt:331` | hereda | 48 dp | `Medium` | `dangerFill`/`onDanger` | — | N/P/D | ambos | — |
| `AqSecondaryButton` | `AqButtons.kt:362` | hereda | 48 dp | `Medium` | `surface`/`textPrimary`/`border` | — | N/P/D | ambos | — |
| `AqOutlineButton` | `AqButtons.kt:412` | hereda | 48 dp | `Medium` | `border`/`textPrimary` | — | N/F/P/D | ambos | — |
| `AqCompactAction` | `AqButtons.kt:439` | interno `Md`/`Sm` | 48 dp | `Medium` | `surfaceContainerLow`/`textPrimary` | — | N/P | ambos | — |
| `AqTextAction` | `AqButtons.kt:499` | `contentPadding` por defecto `Md` horizontal | intrínseca | — | `link`/`onSurface` | Relleno externo no cero por defecto (baja) | N/F/D | ambos | baja |
| `AqIconButton` | `AqButtons.kt:551` | impone `size(ControlMinSize)` al modifier | 48 dp | — | `LocalContentColor` | Fuerza el target sobre el llamador (baja) | N/P/D | ambos | baja |
| `AqFilterChip` | `AqButtons.kt:578` | interno `Md`/`Sm` | 48 dp | `Pill` | `selectionFill`/`emphasisBorder` | — | N/P/S/D | ambos | — |
| `AqFavoriteToggle` | `AqButtons.kt:764` | impone `size(ControlMinSize)` | 48 dp | — | `selectionIcon` | Fuerza el target sobre el llamador (baja) | N/S | ambos | baja |
| `AqProgressBar` | `AqProgress.kt:28` | impone `fillMaxWidth()` | intrínseca | — | `emphasis` | Ancho impuesto al llamador (baja) | N | ambos | baja |
| `AqHairline` | `AqLists.kt:280` | `insetStart` por defecto 0 | 1 dp | — | `Divider` | Orden inset→modifier permite doble inset (baja) | N | ambos | baja |

### 4.2 Moléculas

| Componente | Archivo:línea | Padding/margen/spacedBy | Altura | Radio | Roles de color | Violación de propiedad | Estados | Tema | Severidad |
|---|---|---|---|---|---|---|---|---|---|
| `AqTextField` | `AqInputs.kt:66` | interno 12 dp horizontal; 4 dp bajo la etiqueta y el mensaje | 48 dp | `Medium` | `InputSurface`/`TextPrimary`/`TextSecondary`/`Danger`/`Focus` | — | N/F/E/D | ambos | — |
| `AqSearchField` | `AqInputs.kt:198` | interno 12 dp horizontal | 48 dp | `Pill` | `SurfaceSecondary`/`TextPlaceholder`/`Focus` | — | N/F/E/D | ambos | — |
| `AqSearchTrigger` | `AqInputs.kt:296` | `spacedBy` de tokens | 48 dp | `Medium` | `SurfaceSecondary`/`TextSecondary` | — | N/P/D | ambos | — |
| `AqAmountField` | `AqInputs.kt:345` | `spacedBy(Sm)` = 4 dp; mensaje propio | 48 dp | — (divisor) | `TextPrimary`/`TextSecondary`/`Danger`/`Divider`/`OnDisabled` | Corregido: el error ya no lo pinta la pantalla | N/E/D | ambos | — |
| `AqSelect` | `AqForms.kt:75` | interno 12/8 dp; `spacedBy(Sm)` | 48 dp | `Medium` | `InputSurface`/`TextPrimary`/`TextPlaceholder`/`FunctionalBorder`/`Danger`/`Disabled` | Corregido: estado deshabilitado completo | N/F/E/D | ambos | — |
| `AqCheckbox` | `AqForms.kt:163` | sin margen externo; 8 dp junto al control | 48 dp | — | `TextPrimary`/`TextSecondary`/`OnDisabled` | Corregido: se retiró 4+4 dp externos | N/P/S/D | ambos | — |
| `AqRadioButton` | `AqForms.kt:211` | sin margen externo; 8 dp junto al control | 48 dp | — | `TextPrimary`/`TextSecondary`/`OnDisabled` | Corregido: se retiró 4+4 dp externos | N/P/S/D | ambos | — |
| `AqSwitch` | `AqForms.kt:259` | sin margen externo | 48 dp | — | `TextPrimary`/`TextSecondary`/`OnDisabled` | Corregido: se retiró 4+4 dp externos | N/P/S/D | ambos | — |
| `AqChoiceGroup` | `AqForms.kt:307` | `spacedBy(Sm)` = 4 dp | intrínseca | — | etiqueta + hijos | Corregido: mensaje único del grupo | N/E/D | ambos | — |
| `AqCheckboxGroup` | `AqForms.kt:343` | `spacedBy(Sm)` = 4 dp | intrínseca | — | etiqueta + hijos | Corregido: mensaje único del grupo | N/E/D | ambos | — |
| `AqMetaRow` | `AqLists.kt:168` | `spacedBy` de tokens | intrínseca | — | `TextSecondary`/`TextPrimary` | — | N | ambos | — |
| `AqListRow` | `AqLists.kt:374` | 16 dp horizontal / 12 dp vertical | ≥ 48 dp | — | `TextPrimary`/`TextSecondary`/`TextTertiary`/acento | — | N/P/S/D | ambos | — |
| `AqSegmentedControl` | `AqButtons.kt:679` | interno de tokens | 48 dp | `Medium` | `SurfaceSecondary`/`SelectionFill` | — | N/S/D | ambos | — |
| `AqDialogList` | `AqDialogs.kt:56` | `spacedBy` de tokens | ≤ 320 dp | — | `TextPrimary`/`Surface` | — | N/S/D | ambos | — |
| `AqStatusBanner` | `AqStatus.kt:55` | interno 16 dp; **sin margen externo** | intrínseca | `Medium` | tono (`container`/`content`) | Corregido: el margen lo pone el host | N/acción/descarte | ambos | — |

### 4.3 Organismos

| Componente | Archivo:línea | Padding/margen/spacedBy | Altura | Radio | Roles de color | Violación de propiedad | Estados | Tema | Severidad |
|---|---|---|---|---|---|---|---|---|---|
| `AqCard` | `AqLists.kt:330` | 16 dp internos | intrínseca | `Large` | `Surface`/`Border` | — | N | ambos | — |
| `AqTappableCard` | `AqLists.kt:203` | **16 dp internos** (antes 8) | intrínseca | `Large` | `Surface`/`Border` | Corregido: relleno de tarjeta | N/P/D | ambos | — |
| `AqListBody` | `AqLists.kt:301` | 0 internos por defecto | intrínseca | `Large` | `Surface`/`Border` | — | N | ambos | — |
| `AqQuickAction` | `AqLists.kt:237` | interno de tokens | intrínseca | `Large` | `Surface`/`InfoContainer` | — | N/P/D | ambos | — |
| `AqSectionHeader` | `AqTabs.kt:35` | 16 dp interno; `SectionHeaderHeight` | 48 dp | — | `SurfaceSecondary`/`TextSecondary` | — | N | ambos | — |
| `AqBottomActionDock` | `AqScaffold.kt:43` | **16 dp de margen** + 8 dp vertical; contenido a `ContentMaxWidth`; insets | control | — | `Surface`/`Divider` | Corregido: única fuente del dock y ancho alineado al contenido | N | ambos | — |
| `AqTopBar` | `AqChrome.kt:92` | `statusBarsPadding` + `TopBarHeight` | ≥ 48 dp | — | `Surface`/`TextPrimary` | Aplica inset de plantilla (documentado) | N/P | ambos | baja |
| `AqNavBar` | `AqChrome.kt:262` | `navigationBarsPadding`; 64 dp | 64 dp | — | `Surface`/`Divider`/`selectionFill` | Aplica inset de plantilla (documentado) | N/S | ambos | baja |
| `AqNavRail` | `AqChrome.kt:330` | 80 dp de ancho; `spacedBy` | 64 dp por destino | — | `Surface`/`selectionFill` | Ancho fijo de plantilla (documentado) | N/S | ambos | baja |
| `AqHeroHeader` | `AqChrome.kt:416` | 16 dp de margen + `statusBarsPadding` | intrínseca | — | `BrandFill`/`OnBrandFill` | Aplica margen de plantilla (documentado) | N/P | ambos | baja |
| `AqBottomSheet` | `AqSheets.kt:36` | 16 dp internos | intrínseca | `BottomSheet` (16) | `Surface`/`Divider` | — | N | ambos | — |
| `AqDialog` | `AqDialogs.kt:130` | 24/16 dp internos | intrínseca | `ExtraLarge` | `Surface`/`TextPrimary`/`Divider` | — | N/P | ambos | — |
| `AqQrPreview` | `AqQrPreview.kt:56` | 8 dp internos + 16 dp del marco | 240/320 dp | `Large` | `QrCanvas`/`TextSecondary` | Copy `"QR"` fijado (baja) | N | ambos | baja |
| `AqSkeleton` | `AqFeedback.kt:39` | 16 dp externos | intrínseca | `Small` | `SurfaceSecondary` | Relleno externo de bloque (documentado) | N | ambos | baja |
| `AqLoadMoreFooter` | `AqFeedback.kt:69` | 16 dp externos | ≥ 48 dp | — | `Surface`/`TextSecondary` | Relleno externo de bloque (documentado) | N/carga | ambos | baja |
| `AqEmptyState` | `AqStatus.kt:220` | 48 dp externos | intrínseca | — | `TextPrimary`/`TextSecondary` | Relleno externo de estado (documentado) | N | ambos | baja |
| `AqLoading` | `AqStatus.kt:201` | 48 dp externos | intrínseca | — | `TextSecondary` | Relleno externo de estado (documentado) | N | ambos | baja |
| `AqTabs` | `AqTabs.kt:75` | `edgePadding = None`; divisor propio | 48 dp por pestaña | — | `Surface`/`TextPrimary`/`emphasisBorder` | Micronivel: contenido de página dentro de organismo | N/S | ambos | baja |

### 4.4 Plantillas y páginas

| Componente | Archivo:línea | Padding/margen/spacedBy | Altura | Radio | Roles de color | Violación de propiedad | Estados | Tema | Severidad |
|---|---|---|---|---|---|---|---|---|---|
| `AqScreenScaffold` | `AqScaffold.kt:126` | 16 dp de margen; `spacedBy(Lg)`; ancho máximo 720 dp centrado; insets | pantalla | — | `Background`/`Surface` | — | N | ambos | — |
| `AppStatusHost` | `AppStatusHost.kt:41` | 16 dp de margen en el host | intrínseca | — | tono del banner | Corregido: el host aplica el margen | N/E | ambos | — |
| `AgendaQrApp` (demo) | `AgendaQrApp.kt:60` | 16 dp de margen en el host | pantalla | — | `InfoContainer` | Corregido: el host aplica el margen | N | ambos | — |
| `DestinationDetailScreen` | `DestinationDetailScreen.kt:54` | 0 dp propios; dock del DS | pantalla | — | del DS | Corregido: usa `AqBottomActionDock` | N/E/D | ambos | — |
| `OperationDetailScreen` | `OperationDetailScreen.kt:23` | 0 dp propios; tarjetas del DS | pantalla | — | del DS | Corregido: `AqListBody + padding` → `AqCard` | N/E/D | ambos | — |
| `OperationListScreen` | `OperationListScreen.kt:95` | 0 dp propios; tarjetas del DS | pantalla | — | del DS | Corregido: barra de mes y pie sin padding suelto | N/E/D/carga | ambos | — |
| `OperationEditorScreen` | `OperationEditorScreen.kt:73` | 0 dp propios; relleno en `AqListBody`/`AqCard` | pantalla | — | del DS | Corregido: error del importe en el campo; secciones en tarjeta | N/F/E/D | ambos | — |
| `DestinationsScreen` | `DestinationsScreen.kt:62` | 0 dp propios | pantalla | — | del DS | — | N/E/D/S | ambos | — |
| `DestinationEditorContent` | `DestinationEditorScreen.kt:63` | 0 dp propios | pantalla | — | del DS | — | N/F/E/D | ambos | — |
| `ContextList`/`ContextDetail` | `ContextScreen.kt:95,171` | 0 dp propios | pantalla | — | del DS | — | N/E/D | ambos | — |
| `ImportBatchScreen` | `ImportBatchScreen.kt:61` | 0 dp propios; `PaddingValues(None)` explícito | pantalla | — | del DS | — | N/E/D | ambos | — |
| `ImportReviewScreen` | `ImportReviewScreen.kt:32` | 0 dp propios | pantalla | — | del DS | — | N/E/D | ambos | — |
| `AddDestinationScreen` | `AddDestinationScreen.kt:36` | 0 dp propios | pantalla | — | del DS | — | N/E/D | ambos | — |
| `GlobalSearchScreen` | `GlobalSearchScreen.kt:18` | 0 dp propios | pantalla | — | del DS | — | N/E/D | ambos | — |
| `AuthScreen` | `AuthScreen.kt:42` | 0 dp propios | pantalla | — | del DS | — | N/F/E/D | ambos | — |
| `AccountScreen` | `AccountScreen.kt:40` | 0 dp propios | pantalla | — | del DS | — | N/E | ambos | — |
| `LocalOnboardingScreen` | `LocalOnboardingScreen.kt:47` | 0 dp propios | pantalla | — | del DS | — | N/E | ambos | — |
| `LocalLockScreen` | `LocalLockScreen.kt:40` | 0 dp propios | pantalla | — | del DS | — | N/E | ambos | — |

Las páginas restantes (`ContextsScreen`, `OperationsScreen`,
`UnassociatedScreen`, `HomeSurface`, `DestinationNotFound`,
`QrFullscreenPattern`) solo enrutan o componen plantillas y no declaran
espaciado propio.

## 5. Hallazgos corregidos

| ID | Hallazgo | Corrección |
|---|---|---|
| A-01 | `AqStatusBanner` aceptaba `screenMargin` y aplicaba el margen de pantalla dentro del componente | Se retiró el parámetro; los hosts (`AppStatusHost`, demo) aplican `AqSpacing.ScreenMargin` una vez |
| A-02 | `AqTappableCard` usaba 8 dp internos, menos que el relleno de sección | Pasa a 16 dp (`AqSpacing.Lg`) |
| A-03 | `AqSearchField` usaba 8 dp horizontal, fuera del ritmo de campo | Pasa a 12 dp (`AqSpacing.MdPlus`) |
| A-04 | El error del importe se pintaba en la página como fila suelta | `AqAmountField` acepta `AqFieldState`, pinta el mensaje y su separación de 4 dp; la página ya no compone la fila |
| A-05 | `AqSelect` y los controles booleanos no tenían estado deshabilitado completo | Etiqueta, valor, icono y borde usan `OnDisabled`/`Disabled` cuando corresponde |
| A-06 | `AqCheckbox`/`AqRadioButton`/`AqSwitch` añadían 4+4 dp externos y el grupo otros 4 dp | Se retiró el margen externo; el grupo posee la separación con `spacedBy` |
| A-07 | Los grupos repetían el mensaje de error en cada opción | El grupo pinta el mensaje una vez; los hijos reciben el estado sin copy |
| A-08 | El dock inferior duplicaba superficie, divisor, insets y margen en cada pantalla | Se creó `AqBottomActionDock`; `AqBottomAction` y `DestinationDetailScreen` lo consumen |
| A-09 | `AqPageBlock`/`AqContentWidth` eran código muerto que duplicaba el margen del scaffold | Se eliminaron |
| A-10 | `OperationDetailScreen`/`OperationListScreen`/`OperationEditorScreen` compensaban con `Modifier.padding` de página | El relleno pasa a `AqCard`/`AqListBody(contentPadding=...)` |
| A-11 | Imports `dp` muertos en `AqTabs.kt` y `CameraQrCapture.android.kt` | Se retiraron |
| A-12 | El gate no cubría `padding(número)`, `PaddingValues(número)` ni `spacedBy(número)` | Se añadieron reglas y fixtures positivos/negativos |
| A-13 | Botones, etiquetas y control segmentado usaban radios 4/2 dp, más rectos que los publicados | Botones y etiquetas fuera de filtro pasan a `radius-200` (8 dp); los contadores dentro de filtro, a `radius-100` (4 dp); el segmentado, a 8 dp |
| A-14 | `AqCard`/`AqTappableCard` usaban radio 8 y `AqListBody` no tenía radio ni borde, aunque su KDoc declaraba el radio 12 | Tarjetas y cuerpos de lista pasan a `radius-300` (12 dp) con borde hairline y recorte de esquinas |
| A-15 | `AqDialog` usaba radio 8 frente al `radius-400` (16 dp) del modal de la referencia | Diálogo y selectores de fecha/hora pasan a 16 dp; sus acciones de texto, a 8 dp |
| A-16 | En ventana expandida el contenido no quedaba centrado: el `fillMaxWidth` iba antes del `widthIn` y anulaba el ancho máximo | `AqScreenScaffold` aplica `widthIn(ContentMaxWidth)` antes de `fillMaxWidth`; el contenido queda a 688 dp centrado en 840 dp |
| A-17 | El botón secundario era un relleno gris sin borde y el paso por defecto del scaffold era 8 dp | El secundario usa `surface` + borde de 1 dp (`shadow-button` de `light-mobile`); el paso por defecto entre organismos pasa a 16 dp |
| A-18 | El dock de acciones expandía sus botones a todo el ancho de la ventana y separaba acciones apiladas a 4 dp | El contenido del dock se limita a `ContentMaxWidth` y se centra; las acciones apiladas se separan 8 dp |

## 6. Hallazgos aceptados o pendientes

| ID | Hallazgo | Severidad | Decisión |
|---|---|---|---|
| P-01 | `DemoQr.android.kt:18` usa ARGB crudos `0xFF000000`/`0xFFFFFFFF` para el mapa de bits del QR | media | Aceptado: el QR debe ser negro/blanco puro e independiente del tema (X-03); el gate no inspecciona enteros ARGB. Se documenta aquí y queda candidato a constante nombrada si el gate amplía su alcance |
| P-02 | `AqQrPreview` fija `contentDescription = "QR"` | baja | Aceptado con seguimiento: el copy debería llegar del llamador (ADR-0008); el contrato actual `QrPreviewContractTest` lo fija. Se mantiene para no romper la API en esta entrega |
| P-03 | `AqEmptyState`/`AqLoading` aplican 48 dp externos; `AqSkeleton`/`AqLoadMoreFooter`, 16 dp | baja | Aceptado: son bloques de estado que se componen como sección completa; el relleno se documenta como parte del organismo |
| P-04 | `AqIconButton`/`AqFavoriteToggle` imponen 48 dp y `AqProgressBar` impone `fillMaxWidth()` | baja | Aceptado: el target táctil y el ancho de la barra son contractuales; el llamador no debe reducirlos |
| P-05 | `AqTextAction` trae `contentPadding` horizontal por defecto | baja | Aceptado: la KDoc indica pasar `PaddingValues(None)` para alinear al borde; se mantiene por compatibilidad |
| P-06 | `AqTopBar`/`AqNavBar`/`AqNavRail`/`AqHeroHeader` aplican insets o anchos de plantilla | baja | Aceptado: son el borde de la plantilla; se documenta la responsabilidad compartida |

## 7. Contraste y escalado

- **AA de formularios.** `AqContrastPairsTest` añade la comprobación de
  etiqueta/ayuda (`textSecondary`) y error (`danger`) sobre `surface` e
  `inputSurface` en ambos temas: todos los pares alcanzan 4.5:1.
- **Elementos no textuales.** `focus` y `functionalBorder` mantienen 3:1
  sobre las superficies y sobre `inputSurface`.
- **Estados de color documentados.** Los controles deshabilitados usan
  `OnDisabled`/`Disabled`; el error usa `Danger`; el foco, `Focus`; la
  selección, `SelectionFill`/`emphasis`. No se añaden roles nuevos.
- **Escalado de fuente.** Las pruebas de geometría se ejecutan a 100 % y
  150 %; las alturas son mínimas (`defaultMinSize`), no fijas, por lo que el
  contenido crece sin recorte. El 200 % sigue siendo revisión manual
  (D-06, ADR-0007).
- **Alineación izquierda.** Etiqueta, valor, ayuda y error comparten el borde
  izquierdo del control; las pruebas de `AqUiPolishContractTest` lo miden con
  tolerancia de 0.5 dp.

## 8. Blindaje automático

| Guarda | Qué cubre |
|---|---|
| `verifyDesignSystemCompliance` | Literales `.dp`/`.sp`/`Color(...)`/hex fuera de `AqTokens.kt`; ahora también `padding(número)`, `PaddingValues(número)` y `spacedBy(número)` |
| `verifyDesignSystemFixtures` | Casos que deben fallar y casos con tokens que deben pasar, incluidos los nuevos de espaciado |
| `AqAtomicSpacingContractTest` | Banner sin margen propio, margen del host, tarjeta 16 dp, fila 12 dp, dock con margen de pantalla, error del importe dentro del campo, pasos de la escala y contenido expandido limitado y centrado |
| `AqUiPolishContractTest` | Alineación de etiqueta/valor/ayuda/error, 48 dp de campo, 16 dp de tarjeta y banner, fila 12 dp, opciones sin margen externo y relleno/borde del botón secundario |
| `AqButtonStyleTest` | Resolución de color de todas las variantes en ambos temas, incluido el secundario con borde fino |
| `AqContrastPairsTest` | Pares AA de formulario, foco, borde funcional, tonos y acentos |
| `LayoutReferenceTest` + `captureGallery` | Geometría de las 19 superficies y capturas claras/oscuras a 360 dp |
| `verifyScreenContract` | 62 capturas verificadas contra el manifiesto |

## 9. Verificación ejecutada

```text
./gradlew :core:ui:testDebugUnitTest :feature:destinations:presentation:testDebugUnitTest \
  -PallowDebugSigningForRc=true   -> BUILD SUCCESSFUL
./gradlew captureGallery -Pagendaqr.capture=true -PallowDebugSigningForRc=true
  -> 62 capturas estrictas (31 claras, 31 oscuras)
./gradlew verifyScreenContract verifyScreenContractFixtures \
  -PallowDebugSigningForRc=true   -> PASS (19 superficies; 62 capturas)
```

Los gates estáticos de la entrega (`verifyDesignSystemCompliance`,
`verifyDesignSystemFixtures`, `verifyControlMinSize`, `verifyDocs`) pasan.
iOS y el checklist manual en dispositivo siguen **no verificados** en este
entorno (ADR-0007, ADR-0026).
