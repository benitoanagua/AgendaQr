# Contrato de interacción por pantalla

**Estado:** Generado — no editar a mano.

Este documento se renderiza desde `build-logic/src/main/kotlin/ScreenContractGate.kt` con `./gradlew generateScreenContractDoc`. `verifyScreenContract` falla si el archivo versionado difiere del contrato, de modo que la fuente estructurada y su lectura humana no pueden divergir.

La intención funcional y las reglas de producto viven en [`02-especificacion-ux-ui-v1.md`](02-especificacion-ux-ui-v1.md); los valores visuales, en [`00-contrato-normativo-diseno.md`](../05-design-system/00-contrato-normativo-diseno.md).

## Configuraciones de captura requeridas

- `light`
- `dark`

Las capturas se generan bajo demanda y no se versionan (ADR-0007). El esquema de nombre es `<superficie>--<estado>--<tema>.png`; el ancho único (360 dp) vive en el manifiesto y no se repite en el nombre. No se admite zoom, recorte ni viewport falso.

## Superficies

### S01 — Inicio

- superficie: `home`
- tipo: `Root`
- ruta: `AppRoute.Home`
- representa: `AppRoute.Home`, `DestinationRoute.List`
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/HomeSurface.kt`
- entradas: `app-root`
- salidas: `system-exit`
- destinos válidos: `AppRoute.Search`, `DestinationRoute.Add`, `OperationRoute.New`, `DestinationRoute.ImportReview`, `AppRoute.Contexts`
- acción primaria: —
- estados: `loading`, `empty`, `content`, `error`, `offline`
- gestos: `predictive-back`, `horizontal-scroll`, `swipe-actions`, `pull-to-refresh`
- capturas: `loading`, `empty`, `content`, `error`
- componentes: `AqScreen`, `AqAppBar`, `AqTabBar`, `AqSearchInput`, `AqSegmentedControl`, `AqMenuItem`, `AqListSection`, `AqSectionTitle`, `AqEmptyPlaceholder`, `AqAlert`, `AqFavoriteToggle`, `AqBottomSheet`, `AqPullToRefresh`, `AqSwipeActions`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/DestinationsScreenA11yTest.kt`
- pruebas: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/DestinationsScreenA11yTest.kt`, `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/UxHomeHubTest.kt`, `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/UxHomeFavoritesTest.kt`, `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/DestinationsSectionStateTest.kt`, `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/UxFilterReproductionTest.kt`

### S02 — Añadir

- superficie: `add`
- tipo: `Inner`
- ruta: `DestinationRoute.Add`
- representa: `DestinationRoute.Add`
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/AddDestinationScreen.kt`
- entradas: `home-quick-action`
- salidas: `DestinationRoute.List`
- destinos válidos: `DestinationRoute.ImportReview`, `DestinationRoute.List`
- acción primaria: —
- estados: `content`, `permission`
- gestos: —
- capturas: `content`
- componentes: `AqScreen`, `AqTag`, `AqMenuItem`, `AqListSection`, `AqQrPreview`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`, `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/S02UiPolishTest.kt`

### S09 — Revisar QR

- superficie: `importreview`
- tipo: `Inner`
- ruta: `DestinationRoute.ImportReview`
- representa: `DestinationRoute.ImportReview`
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/ImportReviewScreen.kt`
- entradas: `add-single`, `share-intent`
- salidas: `AppRoute.Home`
- destinos válidos: `AppRoute.Home`, `DestinationRoute.List`
- acción primaria: Guardar (`AppStrings.Guardar`)
- estados: `content`, `saving`, `error`
- gestos: —
- capturas: `content`
- componentes: `AqScreen`, `AqAppBar`, `AqQrPreview`, `AqEmptyPlaceholder`, `AqDockAction`, `AqAlert`, `AqProgressBar`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`

### destination-detail — Detalle de destino

- superficie: `destinationdetail`
- tipo: `Inner`
- ruta: `DestinationRoute.Detail`
- representa: `DestinationRoute.Detail`
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/DestinationDetailScreen.kt`
- entradas: `home-row`, `search-result`, `context-content`
- salidas: `DestinationRoute.List`
- destinos válidos: `DestinationRoute.FullscreenQr`, `OperationRoute.New`, `DestinationRoute.Edit`, `DestinationRoute.List`
- acción primaria: Mostrar QR (`AppStrings.MostrarQr`)
- estados: `loading`, `content`, `error`, `confirmation`
- gestos: —
- capturas: `content`, `error`
- componentes: `AqScreen`, `AqQrPreview`, `AqListSection`, `AqMenuItem`, `AqDockAction`, `AqButton`, `AqButton`, `AqBottomSheet`, `AqModal`, `AqBottomSheet`, `AqTooltip`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/QrFullscreenTest.kt`

### destination-edit — Editor de destino

- superficie: `destinationedit`
- tipo: `Inner`
- ruta: `DestinationRoute.Edit`
- representa: `DestinationRoute.Edit`
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/DestinationEditorScreen.kt`
- entradas: `destination-detail-overflow`, `add-destination`
- salidas: `DestinationRoute.Detail`
- destinos válidos: `DestinationRoute.Detail`, `DestinationRoute.List`
- acción primaria: Guardar (`AppStrings.Guardar`)
- estados: `content`, `saving`, `error`
- gestos: —
- capturas: `content`
- componentes: `AqScreen`, `AqTextInput`, `AqButton`, `AqDockAction`, `AqModal`, `AqSwitch`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: 

### qr-fullscreen — QR a pantalla completa

- superficie: `qrfullscreen`
- tipo: `Inner`
- ruta: `DestinationRoute.FullscreenQr`
- representa: `DestinationRoute.FullscreenQr`
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/AgendaQrPatterns.kt`
- entradas: `destination-detail-primary`
- salidas: `DestinationRoute.Detail`
- destinos válidos: `DestinationRoute.Detail`
- acción primaria: —
- estados: `content`, `error`
- gestos: —
- capturas: `content`, `error`
- componentes: `AqQrPreview`, `AqAppBar`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/QrFullscreenTest.kt`

### S04 — Buscar y resultados

- superficie: `search`
- tipo: `Root`
- ruta: `AppRoute.Search`
- representa: `AppRoute.Search`
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/GlobalSearchScreen.kt`
- entradas: `home-search-trigger`, `root-nav`
- salidas: `system-exit`
- destinos válidos: `DestinationRoute.Detail`, `OperationRoute.Detail`, `ContextRoute.Detail`
- acción primaria: —
- estados: `loading`, `empty`, `content`, `error`
- gestos: —
- capturas: `empty`, `content`
- componentes: `AqScreen`, `AqSearchInput`, `AqMenuItem`, `AqListSection`, `AqSectionTitle`, `AqEmptyPlaceholder`, `AqTabBar`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/SearchResultSurfacesTest.kt`, `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/GlobalSearchResultSemanticsTest.kt`, `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/RootNavigationChromeTest.kt`

### S06 — Contextos

- superficie: `contexts`
- tipo: `Root`
- ruta: `AppRoute.Contexts`
- representa: `AppRoute.Contexts`, `ContextRoute.List`
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/ContextScreen.kt`
- entradas: `root-nav`, `quick-action`
- salidas: `system-exit`
- destinos válidos: `ContextRoute.Detail`, `DestinationRoute.Detail`
- acción primaria: —
- estados: `loading`, `empty`, `content`, `error`
- gestos: —
- capturas: `loading`, `empty`, `content`
- componentes: `AqScreen`, `AqAppBar`, `AqMenuItem`, `AqListSection`, `AqTabBar`, `AqTag`, `AqEmptyPlaceholder`, `AqCircularLoader`, `AqAlert`, `AqButton`, `AqTabBar`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/ContextsBackStateTest.kt`, `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/ContextContentsRoutingTest.kt`, `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/ContextCreationTest.kt`

### context-detail — Detalle de contexto

- superficie: `contextdetail`
- tipo: `Inner`
- ruta: `ContextRoute.Detail`
- representa: `ContextRoute.Detail`
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/ContextScreen.kt`
- entradas: `contexts-row`, `search-result`
- salidas: `ContextRoute.List`
- destinos válidos: `DestinationRoute.Detail`, `OperationRoute.Detail`
- acción primaria: —
- estados: `loading`, `empty`, `content`, `error`
- gestos: —
- capturas: `content`, `empty`
- componentes: `AqScreen`, `AqTabBar`, `AqMenuItem`, `AqListSection`, `AqEmptyPlaceholder`, `AqTag`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/ContextsBackStateTest.kt`, `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/ContextContentsRoutingTest.kt`

### S07-list — Actividades

- superficie: `operations`
- tipo: `Root`
- ruta: `AppRoute.Operations`
- representa: `AppRoute.Operations`, `OperationRoute.List`
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/OperationListScreen.kt`
- entradas: `root-nav`, `home-quick-action`, `share-intent`
- salidas: `system-exit`
- destinos válidos: `OperationRoute.Detail`, `OperationRoute.New`, `OperationRoute.Unassociated`
- acción primaria: Registrar (`AppStrings.RegistrarOperacion`)
- estados: `loading`, `empty`, `content`, `error`
- gestos: `swipe-actions`, `pull-to-refresh`
- capturas: `empty`, `content`
- componentes: `AqScreen`, `AqSegmentedControl`, `AqButton`, `AqMenuItem`, `AqListSection`, `AqEmptyPlaceholder`, `AqModal`, `AqTabBar`, `AqPullToRefresh`, `AqSwipeActions`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/OperationDateTest.kt`

### operation-detail — Detalle de actividad

- superficie: `operationdetail`
- tipo: `Inner`
- ruta: `OperationRoute.Detail`
- representa: `OperationRoute.Detail`
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/OperationDetailScreen.kt`
- entradas: `operations-row`, `search-result`, `context-content`
- salidas: `OperationRoute.List`
- destinos válidos: `OperationRoute.Edit`, `OperationRoute.List`
- acción primaria: —
- estados: `loading`, `content`, `error`, `confirmation`
- gestos: —
- capturas: `content`
- componentes: `AqScreen`, `AqListSection`, `AqMenuItem`, `AqTag`, `AqBottomSheet`, `AqModal`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/OperationEditActionTest.kt`

### S07 — Registrar actividad

- superficie: `operationeditor`
- tipo: `Inner`
- ruta: `OperationRoute.New`
- representa: `OperationRoute.New`, `OperationRoute.Edit`
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/OperationEditorScreen.kt`
- entradas: `home-quick-action`, `operations-primary`, `destination-detail-overflow`
- salidas: `OperationRoute.List`
- destinos válidos: `OperationRoute.List`, `OperationRoute.Edit`, `ContextRoute.List`
- acción primaria: Guardar (`AppStrings.GuardarCambios`)
- estados: `content`, `saving`, `error`, `confirmation`
- gestos: `horizontal-scroll`
- capturas: `content`
- componentes: `AqScreen`, `AqSegmentedControl`, `AqAmountInput`, `AqTextInput`, `AqDockAction`, `AqModal`, `AqSectionTitle`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/UxEditorDiscardTest.kt`, `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/UxAmountValidationTest.kt`, `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/UxDateReproductionTest.kt`, `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/UxDiscardReproductionTest.kt`, `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ActionHierarchyTest.kt`

### inbox — Comprobantes sin asociar

- superficie: `unassociated`
- tipo: `Inner`
- ruta: `OperationRoute.Unassociated`
- representa: `OperationRoute.Unassociated`
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/UnassociatedScreen.kt`
- entradas: `operations-secondary`
- salidas: `OperationRoute.List`
- destinos válidos: `OperationRoute.List`, `OperationRoute.Detail`
- acción primaria: —
- estados: `empty`, `content`, `selection`, `error`
- gestos: `long-press`
- capturas: `empty`, `content`
- componentes: `AqScreen`, `AqMenuItem`, `AqEmptyPlaceholder`, `AqModal`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/UxUnassociatedTest.kt`

### S08 — Seleccionar contexto

- superficie: `contextpicker`
- tipo: `Overlay`
- ruta: —
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/ContextPickerDialog.kt`
- entradas: `destination-edit`, `operationeditor`
- salidas: `OperationRoute.New`, `DestinationRoute.Edit`
- destinos válidos: `ContextRoute.List`, `AppRoute.Contexts`
- acción primaria: —
- estados: `selection`, `empty`, `content`
- gestos: —
- capturas: `selection`, `empty`
- componentes: `AqModal`, `AqMenuItem`, `AqTextInput`, `AqButton`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ContextPickerDialogTest.kt`, `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/ContextSelectionTest.kt`, `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/ContextCreationTest.kt`

### S12 — Resultado de importación masiva

- superficie: `importbatch`
- tipo: `Root`
- ruta: `AppRoute.ImportBatch`
- representa: `AppRoute.ImportBatch`
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/ImportBatchScreen.kt`
- entradas: `add-multiple`, `share-intent-multiple`
- salidas: `AppRoute.Home`, `AppRoute.Operations`
- destinos válidos: `AppRoute.Home`, `AppRoute.Operations`
- acción primaria: Guardar reconocidos (`AppStrings.GuardarReconocidos`)
- estados: `review`, `saving`, `saved`, `error`, `content`, `confirmation`
- gestos: —
- capturas: `review`, `error`, `saved`
- componentes: `AqScreen`, `AqAlert`, `AqMenuItem`, `AqListSection`, `AqDockAction`, `AqEmptyPlaceholder`, `AqModal`, `AqProgressBlock`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/ImportBatchReducerTest.kt`, `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/ImportBatchCoordinatorTest.kt`, `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/ImportBatchPendingItemsTest.kt`, `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ActionHierarchyTest.kt`, `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ImportBatchBannerLayoutTest.kt`

### auth — Cuenta: autenticación

- superficie: `authform`
- tipo: `Overlay`
- ruta: —
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/AuthScreen.kt`
- entradas: `account`
- salidas: `AppRoute.Home`
- destinos válidos: `AppRoute.Home`
- acción primaria: Iniciar sesión (`AppStrings.IniciarSesion`)
- estados: `content`, `loading`, `error`, `saving`
- gestos: —
- capturas: `content`, `error`
- componentes: `AqTextInput`, `AqButton`, `AqAlert`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/AccessibilityStatesFormsTest.kt`
- pruebas: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/AccessibilityStatesFormsTest.kt`, `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/StateRestorationTest.kt`

### S00 — Acceso local

- superficie: `localaccess`
- tipo: `Platform`
- ruta: —
- fuente: `feature/destinations/presentation/src/commonMain/kotlin/com/agendaqr/destinations/presentation/LocalOnboardingScreen.kt`
- entradas: `cold-start`, `resume-after-lock`
- salidas: `system-exit`
- destinos válidos: `system-exit`
- acción primaria: —
- estados: `locked`, `error`, `content`
- gestos: —
- capturas: `locked`, `error`
- componentes: `AqScreen`, `AqTag`, `AqPinDots`, `AqKeypad`, `AqDockAction`, `AqButton`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`, `core/ui/src/androidUnitTest/kotlin/com/agendaqr/core/ui/components/AqAccessibilityContractTest.kt`
- pruebas: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/LocalAccessGateTest.kt`, `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/LocalAccessBiometricTest.kt`, `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/AgendaQrLocalStartupTest.kt`, `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/UxLockSearchTest.kt`

### S03 — Cámara

- superficie: `camera`
- tipo: `Platform`
- ruta: —
- fuente: `feature/destinations/presentation/src/androidMain/kotlin/com/agendaqr/destinations/presentation/QrImportControls.android.kt`
- entradas: `add`, `destination-edit`
- salidas: `DestinationRoute.Add`
- destinos válidos: `DestinationRoute.Add`
- acción primaria: —
- estados: `content`, `permission`, `error`
- gestos: —
- capturas: `content`
- componentes: `AqScannerFrame`, `AqAppBar`, `AqButton`
- pruebas a11y: `feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/ScreensA11yCoverageTest.kt`
- pruebas: `feature/destinations/presentation/src/commonTest/kotlin/com/agendaqr/destinations/presentation/CameraPermissionPolicyTest.kt`, `androidApp/src/androidTest/kotlin/com/agendaqr/android/CameraPermissionFlowTest.kt`

