import java.io.File
import java.security.MessageDigest

/**
 * Contrato de interacción por pantalla: fuente estructurada y tipada que un
 * gate ejecutable cruza contra el código real, las pruebas y la documentación
 * generada. La prosa funcional vive en la especificación UX/UI; aquí viven los
 * hechos falsables (ruta, fuente, destinos válidos, estados, gestos, pruebas,
 * componentes y escenarios de captura).
 *
 * Nada de este archivo se evalúa con reglas de estilo: vive en `build-logic`,
 * fuera de `productionKotlinSources()`.
 */
enum class SurfaceKind { Root, Inner, Overlay, Platform }

/**
 * @param covers símbolos de ruta que esta superficie representa (raíz y/o
 *   entradas internas del flujo). La cobertura es exacta: todo destino real
 *   tiene contrato y todo símbolo declarado existe en el código.
 */
data class ScreenContract(
    val id: String,
    val surface: String,
    val title: String,
    val kind: SurfaceKind,
    val route: String?,
    val covers: List<String>,
    val source: String,
    val entryPoints: List<String>,
    val exits: List<String>,
    val destinations: List<String>,
    val primaryAction: String?,
    val primaryActionKey: String?,
    val states: List<String>,
    val gestures: List<String>,
    val captureStates: List<String>,
    val components: List<String>,
    val a11yTests: List<String>,
    val tests: List<String>,
)

/** Vocabulario cerrado de estados observables. */
val screenStateVocabulary: Set<String> = setOf(
    "loading", "empty", "error", "content", "saving", "saved",
    "selection", "review", "confirmation", "recovery", "offline", "permission", "locked",
)

/** Vocabulario cerrado de gestos admitidos (los del sistema o ya existentes). */
val gestureVocabulary: Set<String> = setOf(
    "system-back", "predictive-back", "horizontal-scroll", "vertical-scroll", "tap",
)

/** Configuraciones que la galería de capturas debe cubrir cuando se genera. */
val requiredCaptureConfigs: List<String> = listOf(
    "light-320", "light-360", "dark-320", "dark-360",
    "light-360-font200", "dark-360-font200",
)

private const val PRESENTATION = "feature/destinations/presentation/src"
private const val A11Y = "$PRESENTATION/androidUnitTest/kotlin/com/agendaqr/destinations/presentation"
private const val CORE_A11Y = "core/ui/src/androidUnitTest/kotlin/com/agendaqr/core/ui/components"

object ScreenContracts {
    val all: List<ScreenContract> = listOf(
        ScreenContract(
            id = "S01",
            surface = "home",
            title = "Inicio",
            kind = SurfaceKind.Root,
            route = "AppRoute.Home",
            covers = listOf("AppRoute.Home", "DestinationRoute.List"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/HomeSurface.kt",
            entryPoints = listOf("app-root"),
            exits = listOf("system-exit"),
            destinations = listOf(
                "AppRoute.Search", "DestinationRoute.Add", "OperationRoute.New",
                "DestinationRoute.ImportReview", "AppRoute.Contexts",
            ),
            primaryAction = null,
            primaryActionKey = null,
            states = listOf("loading", "empty", "content", "error", "offline"),
            gestures = listOf("predictive-back", "horizontal-scroll"),
            captureStates = listOf("empty", "content", "error"),
            components = listOf(
                "AmScreenScaffold", "AmTopBar", "AmNavBar", "AmSearchTrigger", "AmQuickAction",
                "AmSegmentedControl", "AmListRow", "AmListBody", "AmSectionHeader",
                "AmEmptyState", "AmStatusBanner", "AmFavoriteToggle", "AmActionSheet",
            ),
            a11yTests = listOf("$A11Y/DestinationsScreenA11yTest.kt"),
            tests = listOf(
                "$A11Y/DestinationsScreenA11yTest.kt",
                "$A11Y/UxHomeHubTest.kt",
                "$A11Y/UxHomeFavoritesTest.kt",
                "$A11Y/LayoutReferenceTest.kt",
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/DestinationsSectionStateTest.kt",
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/UxFilterReproductionTest.kt",
            ),
        ),
        ScreenContract(
            id = "S02",
            surface = "add",
            title = "Añadir",
            kind = SurfaceKind.Inner,
            route = "DestinationRoute.Add",
            covers = listOf("DestinationRoute.Add"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/AddDestinationScreen.kt",
            entryPoints = listOf("home-quick-action"),
            exits = listOf("DestinationRoute.List"),
            destinations = listOf("DestinationRoute.ImportReview", "DestinationRoute.List"),
            primaryAction = null,
            primaryActionKey = null,
            states = listOf("content", "permission"),
            gestures = emptyList(),
            captureStates = listOf("content"),
            components = listOf(
                "AmScreenScaffold", "AmIconBadge", "AmListRow", "AmListBody", "AmQrPreview",
            ),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf("$A11Y/ScreensA11yCoverageTest.kt", "$A11Y/S02UiPolishTest.kt"),
        ),
        ScreenContract(
            id = "S09",
            surface = "importreview",
            title = "Revisar QR",
            kind = SurfaceKind.Inner,
            route = "DestinationRoute.ImportReview",
            covers = listOf("DestinationRoute.ImportReview"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/ImportReviewScreen.kt",
            entryPoints = listOf("add-single", "share-intent"),
            exits = listOf("AppRoute.Home"),
            destinations = listOf("AppRoute.Home", "DestinationRoute.List"),
            primaryAction = "Guardar",
            primaryActionKey = "Guardar",
            states = listOf("content", "saving", "error"),
            gestures = emptyList(),
            captureStates = listOf("content"),
            components = listOf(
                "AmScreenScaffold", "AmTopBar", "AmQrPreview", "AmCard",
                "AmEmptyState", "AmBottomAction", "AmStatusBanner",
            ),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf("$A11Y/ScreensA11yCoverageTest.kt", "$A11Y/LayoutReferenceTest.kt"),
        ),
        ScreenContract(
            id = "destination-detail",
            surface = "destinationdetail",
            title = "Detalle de destino",
            kind = SurfaceKind.Inner,
            route = "DestinationRoute.Detail",
            covers = listOf("DestinationRoute.Detail"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/DestinationDetailScreen.kt",
            entryPoints = listOf("home-row", "search-result", "context-content"),
            exits = listOf("DestinationRoute.List"),
            destinations = listOf(
                "DestinationRoute.FullscreenQr", "OperationRoute.New",
                "DestinationRoute.Edit", "DestinationRoute.List",
            ),
            primaryAction = "Mostrar QR",
            primaryActionKey = "MostrarQr",
            states = listOf("loading", "content", "error", "confirmation"),
            gestures = emptyList(),
            captureStates = listOf("content", "error"),
            components = listOf(
                "AmScreenScaffold", "AmQrPreview", "AmListBody", "AmListRow",
                "AmBottomAction", "AmPrimaryButton", "AmSecondaryButton", "AmActionSheet", "AmDialog",
            ),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf("$A11Y/QrFullscreenTest.kt", "$A11Y/LayoutReferenceTest.kt"),
        ),
        ScreenContract(
            id = "destination-edit",
            surface = "destinationedit",
            title = "Editor de destino",
            kind = SurfaceKind.Inner,
            route = "DestinationRoute.Edit",
            covers = listOf("DestinationRoute.Edit"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/DestinationEditorScreen.kt",
            entryPoints = listOf("destination-detail-overflow", "add-destination"),
            exits = listOf("DestinationRoute.Detail"),
            destinations = listOf("DestinationRoute.Detail", "DestinationRoute.List"),
            primaryAction = "Guardar",
            primaryActionKey = "Guardar",
            states = listOf("content", "saving", "error"),
            gestures = emptyList(),
            captureStates = listOf("content"),
            components = listOf(
                "AmScreenScaffold", "AmTextField", "AmPrimaryButton", "AmBottomAction", "AmDialogList",
            ),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf("$A11Y/LayoutReferenceTest.kt"),
        ),
        ScreenContract(
            id = "qr-fullscreen",
            surface = "qrfullscreen",
            title = "QR a pantalla completa",
            kind = SurfaceKind.Inner,
            route = "DestinationRoute.FullscreenQr",
            covers = listOf("DestinationRoute.FullscreenQr"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/AgendaQrPatterns.kt",
            entryPoints = listOf("destination-detail-primary"),
            exits = listOf("DestinationRoute.Detail"),
            destinations = listOf("DestinationRoute.Detail"),
            primaryAction = null,
            primaryActionKey = null,
            states = listOf("content", "error"),
            gestures = emptyList(),
            captureStates = listOf("content"),
            components = listOf("AmQrPreview", "AmTopBar"),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf("$A11Y/QrFullscreenTest.kt"),
        ),
        ScreenContract(
            id = "S04",
            surface = "search",
            title = "Buscar y resultados",
            kind = SurfaceKind.Root,
            route = "AppRoute.Search",
            covers = listOf("AppRoute.Search"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/GlobalSearchScreen.kt",
            entryPoints = listOf("home-search-trigger", "root-nav"),
            exits = listOf("system-exit"),
            destinations = listOf(
                "DestinationRoute.Detail", "OperationRoute.Detail", "ContextRoute.Detail",
            ),
            primaryAction = null,
            primaryActionKey = null,
            states = listOf("loading", "empty", "content", "error"),
            gestures = emptyList(),
            captureStates = listOf("empty", "content"),
            components = listOf(
                "AmScreenScaffold", "AmSearchField", "AmListRow", "AmListBody",
                "AmSectionHeader", "AmEmptyState", "AmNavBar",
            ),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf(
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/SearchResultSurfacesTest.kt",
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/GlobalSearchResultSemanticsTest.kt",
                "$A11Y/RootNavigationChromeTest.kt",
            ),
        ),
        ScreenContract(
            id = "S06",
            surface = "contexts",
            title = "Contextos",
            kind = SurfaceKind.Root,
            route = "AppRoute.Contexts",
            covers = listOf("AppRoute.Contexts", "ContextRoute.List"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/ContextScreen.kt",
            entryPoints = listOf("root-nav", "quick-action"),
            exits = listOf("system-exit"),
            destinations = listOf("ContextRoute.Detail", "DestinationRoute.Detail"),
            primaryAction = null,
            primaryActionKey = null,
            states = listOf("loading", "empty", "content", "error"),
            gestures = emptyList(),
            captureStates = listOf("empty", "content"),
            components = listOf(
                "AmScreenScaffold", "AmTopBar", "AmListRow", "AmListBody",
                "AmTabs", "AmContextBadge", "AmEmptyState", "AmLoading",
                "AmStatusBanner", "AmTextAction", "AmNavBar",
            ),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf(
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/ContextsBackStateTest.kt",
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/ContextContentsRoutingTest.kt",
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/ContextCreationTest.kt",
            ),
        ),
        ScreenContract(
            id = "context-detail",
            surface = "contextdetail",
            title = "Detalle de contexto",
            kind = SurfaceKind.Inner,
            route = "ContextRoute.Detail",
            covers = listOf("ContextRoute.Detail"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/ContextScreen.kt",
            entryPoints = listOf("contexts-row", "search-result"),
            exits = listOf("ContextRoute.List"),
            destinations = listOf("DestinationRoute.Detail", "OperationRoute.Detail"),
            primaryAction = null,
            primaryActionKey = null,
            states = listOf("loading", "empty", "content", "error"),
            gestures = emptyList(),
            captureStates = listOf("content", "empty"),
            components = listOf(
                "AmScreenScaffold", "AmTabs", "AmListRow", "AmListBody",
                "AmEmptyState", "AmContextBadge",
            ),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf(
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/ContextsBackStateTest.kt",
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/ContextContentsRoutingTest.kt",
            ),
        ),
        ScreenContract(
            id = "S07-list",
            surface = "operations",
            title = "Actividades",
            kind = SurfaceKind.Root,
            route = "AppRoute.Operations",
            covers = listOf("AppRoute.Operations", "OperationRoute.List"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/OperationListScreen.kt",
            entryPoints = listOf("root-nav", "home-quick-action", "share-intent"),
            exits = listOf("system-exit"),
            destinations = listOf(
                "OperationRoute.Detail", "OperationRoute.New", "OperationRoute.Unassociated",
            ),
            primaryAction = "Registrar",
            primaryActionKey = "RegistrarOperacion",
            states = listOf("loading", "empty", "content", "error"),
            gestures = emptyList(),
            captureStates = listOf("empty", "content"),
            components = listOf(
                "AmScreenScaffold", "AmSegmentedControl", "AmExtendedFab", "AmListRow",
                "AmListBody", "AmEmptyState", "AmDialogList", "AmNavBar",
            ),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf(
                "$A11Y/LayoutReferenceTest.kt",
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/OperationDateTest.kt",
            ),
        ),
        ScreenContract(
            id = "operation-detail",
            surface = "operationdetail",
            title = "Detalle de actividad",
            kind = SurfaceKind.Inner,
            route = "OperationRoute.Detail",
            covers = listOf("OperationRoute.Detail"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/OperationDetailScreen.kt",
            entryPoints = listOf("operations-row", "search-result", "context-content"),
            exits = listOf("OperationRoute.List"),
            destinations = listOf("OperationRoute.Edit", "OperationRoute.List"),
            primaryAction = null,
            primaryActionKey = null,
            states = listOf("loading", "content", "error", "confirmation"),
            gestures = emptyList(),
            captureStates = listOf("content"),
            components = listOf(
                "AmScreenScaffold", "AmListBody", "AmListRow", "AmTonalTag",
                "AmActionSheet", "AmDialogList",
            ),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf(
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/OperationEditActionTest.kt",
            ),
        ),
        ScreenContract(
            id = "S07",
            surface = "operationeditor",
            title = "Registrar actividad",
            kind = SurfaceKind.Inner,
            route = "OperationRoute.New",
            covers = listOf("OperationRoute.New", "OperationRoute.Edit"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/OperationEditorScreen.kt",
            entryPoints = listOf("home-quick-action", "operations-primary", "destination-detail-overflow"),
            exits = listOf("OperationRoute.List"),
            destinations = listOf("OperationRoute.List", "OperationRoute.Edit", "ContextRoute.List"),
            primaryAction = "Guardar",
            primaryActionKey = "GuardarCambios",
            states = listOf("content", "saving", "error", "confirmation"),
            gestures = listOf("horizontal-scroll"),
            captureStates = listOf("content"),
            components = listOf(
                "AmScreenScaffold", "AmSegmentedControl", "AmAmountField", "AmTextField",
                "AmBottomAction", "AmDialogList", "AmDatePickerDialog", "AmTimePickerDialog",
                "AmSectionHeader",
            ),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf(
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/UxEditorDiscardTest.kt",
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/UxAmountValidationTest.kt",
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/UxDateReproductionTest.kt",
                "$A11Y/UxDiscardReproductionTest.kt",
                "$A11Y/ActionHierarchyTest.kt",
            ),
        ),
        ScreenContract(
            id = "inbox",
            surface = "unassociated",
            title = "Comprobantes sin asociar",
            kind = SurfaceKind.Inner,
            route = "OperationRoute.Unassociated",
            covers = listOf("OperationRoute.Unassociated"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/OperationListScreen.kt",
            entryPoints = listOf("operations-secondary"),
            exits = listOf("OperationRoute.List"),
            destinations = listOf("OperationRoute.List", "OperationRoute.Detail"),
            primaryAction = null,
            primaryActionKey = null,
            states = listOf("empty", "content", "error"),
            gestures = emptyList(),
            captureStates = listOf("empty", "content"),
            components = listOf(
                "AmScreenScaffold", "AmListRow", "AmEmptyState", "AmDialogList",
            ),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf("$A11Y/UxUnassociatedTest.kt"),
        ),
        ScreenContract(
            id = "S08",
            surface = "contextpicker",
            title = "Seleccionar contexto",
            kind = SurfaceKind.Overlay,
            route = null,
            covers = emptyList(),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/ContextPickerDialog.kt",
            entryPoints = listOf("destination-edit", "operationeditor"),
            exits = listOf("OperationRoute.New", "DestinationRoute.Edit"),
            destinations = listOf("ContextRoute.List", "AppRoute.Contexts"),
            primaryAction = null,
            primaryActionKey = null,
            states = listOf("selection", "empty", "content"),
            gestures = emptyList(),
            captureStates = listOf("selection"),
            components = listOf(
                "AmDialog", "AmDialogList", "AmListRow", "AmTextField",
                "AmTextAction",
            ),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf(
                "$A11Y/ContextPickerDialogTest.kt",
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/ContextSelectionTest.kt",
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/ContextCreationTest.kt",
            ),
        ),
        ScreenContract(
            id = "S12",
            surface = "importbatch",
            title = "Resultado de importación masiva",
            kind = SurfaceKind.Root,
            route = "AppRoute.ImportBatch",
            covers = listOf("AppRoute.ImportBatch"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/ImportBatchScreen.kt",
            entryPoints = listOf("add-multiple", "share-intent-multiple"),
            exits = listOf("AppRoute.Home", "AppRoute.Operations"),
            destinations = listOf("AppRoute.Home", "AppRoute.Operations"),
            primaryAction = "Guardar reconocidos",
            primaryActionKey = "GuardarReconocidos",
            states = listOf("review", "saving", "saved", "error", "content", "confirmation"),
            gestures = emptyList(),
            captureStates = listOf("review", "error", "saved"),
            components = listOf(
                "AmScreenScaffold", "AmStatusBanner", "AmListRow", "AmListBody",
                "AmBottomAction", "AmEmptyState", "AmDialogList",
            ),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf(
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/ImportBatchReducerTest.kt",
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/ImportBatchCoordinatorTest.kt",
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/ImportBatchPendingItemsTest.kt",
                "$A11Y/ActionHierarchyTest.kt",
            ),
        ),
        ScreenContract(
            id = "account",
            surface = "account",
            title = "Cuenta y respaldo",
            kind = SurfaceKind.Root,
            route = "AppRoute.Account",
            covers = listOf("AppRoute.Account"),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/AccountScreen.kt",
            entryPoints = listOf("home-overflow"),
            exits = listOf("AppRoute.Home"),
            destinations = listOf("AppRoute.Home"),
            primaryAction = null,
            primaryActionKey = null,
            states = listOf("loading", "content", "error", "saving"),
            gestures = emptyList(),
            captureStates = listOf("content", "error"),
            components = listOf(
                "AmScreenScaffold", "AmListBody", "AmListRow", "AmStatusBanner", "AmLoading",
            ),
            a11yTests = listOf("$A11Y/AccessibilityStatesFormsTest.kt"),
            tests = listOf(
                "$A11Y/AuthFontScaleTest.kt",
                "$A11Y/AccessibilityStatesFormsTest.kt",
            ),
        ),
        ScreenContract(
            id = "auth",
            surface = "authform",
            title = "Cuenta: autenticación",
            kind = SurfaceKind.Overlay,
            route = null,
            covers = emptyList(),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/AuthScreen.kt",
            entryPoints = listOf("account"),
            exits = listOf("AppRoute.Account"),
            destinations = listOf("AppRoute.Account"),
            primaryAction = "Iniciar sesión",
            primaryActionKey = "IniciarSesion",
            states = listOf("content", "loading", "error", "saving"),
            gestures = emptyList(),
            captureStates = listOf("content", "error"),
            components = listOf("AmTextField", "AmPrimaryButton", "AmStatusBanner"),
            a11yTests = listOf("$A11Y/AccessibilityStatesFormsTest.kt"),
            tests = listOf(
                "$A11Y/AccessibilityStatesFormsTest.kt",
                "$A11Y/AuthFontScaleTest.kt",
                "$A11Y/StateRestorationTest.kt",
            ),
        ),
        ScreenContract(
            id = "S00",
            surface = "localaccess",
            title = "Acceso local",
            kind = SurfaceKind.Platform,
            route = null,
            covers = emptyList(),
            source = "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/LocalLockScreen.kt",
            entryPoints = listOf("cold-start", "resume-after-lock"),
            exits = listOf("system-exit"),
            destinations = listOf("system-exit"),
            primaryAction = null,
            primaryActionKey = null,
            states = listOf("locked", "error", "content"),
            gestures = emptyList(),
            captureStates = listOf("locked", "error"),
            components = listOf(
                "AmScreenScaffold", "AmIconBadge", "AmPinDots", "AmKeypad",
                "AmBottomAction", "AmSecondaryButton",
            ),
            a11yTests = listOf(
                "$A11Y/ScreensA11yCoverageTest.kt",
                "$CORE_A11Y/AmAccessibilityContractTest.kt",
            ),
            tests = listOf(
                "$A11Y/LocalAccessGateTest.kt",
                "$A11Y/LocalAccessBiometricTest.kt",
                "$A11Y/AgendaQrLocalStartupTest.kt",
                "$A11Y/UxLockSearchTest.kt",
            ),
        ),
        ScreenContract(
            id = "S03",
            surface = "camera",
            title = "Cámara",
            kind = SurfaceKind.Platform,
            route = null,
            covers = emptyList(),
            source = "$PRESENTATION/androidMain/kotlin/com/agendaqr/destinations/presentation/QrImportControls.android.kt",
            entryPoints = listOf("add", "destination-edit"),
            exits = listOf("DestinationRoute.Add"),
            destinations = listOf("DestinationRoute.Add"),
            primaryAction = null,
            primaryActionKey = null,
            states = listOf("content", "permission", "error"),
            gestures = emptyList(),
            captureStates = listOf("content"),
            components = listOf("AmScannerFrame", "AmTopBar", "AmPrimaryButton"),
            a11yTests = listOf("$A11Y/ScreensA11yCoverageTest.kt"),
            tests = listOf(
                "$PRESENTATION/commonTest/kotlin/com/agendaqr/destinations/presentation/CameraPermissionPolicyTest.kt",
                "androidApp/src/androidTest/kotlin/com/agendaqr/android/CameraPermissionFlowTest.kt",
            ),
        ),
    )
}

/** Símbolos de ruta raíz declarados en `AppRoute.kt`. */
private val rootRouteDecl = Regex("data object (\\w+) : AppRoute")
private val sealedDecl = Regex("sealed interface (\\w+)")

private fun appRouteFile(): String =
    "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/AppRoute.kt"

private val flowRouteFiles = listOf(
    "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/DestinationsState.kt",
    "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/OperationContracts.kt",
    "$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/ContextState.kt",
)

/** Interfaces de ruta de flujo cuya cobertura exige el contrato. */
private val routeSealedNames = setOf("DestinationRoute", "OperationRoute", "ContextRoute")

/**
 * Cruza el contrato contra el código y la documentación. Función pura: recibe
 * el contenido real (o sintético en las pruebas del propio validador) y
 * devuelve la lista de violaciones; vacía significa consistente.
 */
fun screenContractViolations(
    screens: List<ScreenContract>,
    read: (String) -> String?,
    productionKotlin: List<Pair<String, String>>,
): List<String> = buildList {
    // 1. Identidad estable.
    screens.groupBy { it.id }.filterValues { it.size > 1 }.keys.forEach {
        add("id-duplicado: '$it' aparece en más de un contrato")
    }
    screens.groupBy { it.surface }.filterValues { it.size > 1 }.keys.forEach {
        add("superficie-duplicada: '$it' aparece en más de un contrato")
    }

    // 2. Rutas reales.
    val appRouteText = read(appRouteFile())
    if (appRouteText == null) {
        add("ruta-fuente: no se encuentra ${appRouteFile()}")
    }
    val rootNames = appRouteText?.let { text ->
        rootRouteDecl.findAll(text).map { it.groupValues[1] }.toSet()
    } ?: emptySet()

    val sealedEntries = mutableMapOf<String, Triple<String, String, Set<String>>>()
    flowRouteFiles.forEach { path ->
        val text = read(path) ?: return@forEach
        sealedDecl.findAll(text).forEach { match ->
            val name = match.groupValues[1]
            if (name !in routeSealedNames) return@forEach
            val entries = Regex("data (?:object|class) (\\w+)[^\\n]*: *$name\\b")
                .findAll(text).map { it.groupValues[1] }.toSet()
            sealedEntries[name] = Triple(path, text, entries)
        }
    }

    val declaredTargets = buildSet {
        rootNames.forEach { add("AppRoute.$it"); add(it) }
        sealedEntries.forEach { (sealed, triple) ->
            triple.third.forEach { add("$sealed.$it") }
        }
        add("system-exit")
    }

    // 3. Cobertura exacta: todo símbolo real tiene contrato y viceversa.
    val coveredSymbols = screens.flatMap { it.covers }.toSet()
    rootNames.forEach { name ->
        if ("AppRoute.$name" !in coveredSymbols) {
            add("pantalla-sin-contrato: AppRoute.$name no está cubierto por ninguna superficie")
        }
    }
    sealedEntries.forEach { (sealed, triple) ->
        triple.third.forEach { entry ->
            if ("$sealed.$entry" !in coveredSymbols) {
                add("pantalla-sin-contrato: $sealed.$entry no está cubierto por ninguna superficie")
            }
        }
    }
    screens.forEach { screen ->
        screen.covers.forEach { symbol ->
            if (symbol !in declaredTargets) {
                add("contrato-obsoleto: ${screen.id} cubre '$symbol', que no existe en el código")
            }
        }
        val route = screen.route
        if (route != null && route !in declaredTargets) {
            add("ruta-invalida: ${screen.id} declara route='$route', que no existe")
        }
        if (screen.kind == SurfaceKind.Root && route?.startsWith("AppRoute.") != true) {
            add("ruta-invalida: ${screen.id} es Root y debe declarar route='AppRoute.<Nombre>'")
        }
    }
    sealedEntries.forEach { (sealed, triple) ->
        triple.third.forEach { entry ->
            if ("$sealed.$entry" in coveredSymbols) {
                val owners = screens.filter { "$sealed.$entry" in it.covers }
                if (owners.size > 1) {
                    add("cobertura-ambigua: $sealed.$entry cubierto por ${owners.map { it.id }}")
                }
            }
        }
    }

    // 4. Fuente real.
    screens.forEach { screen ->
        if (read(screen.source) == null) {
            add("fuente-ausente: ${screen.id} declara un archivo que no existe: ${screen.source}")
        }
    }

    // 5. Destinos válidos (grafo de navegación sin aristas colgantes).
    screens.forEach { screen ->
        screen.destinations.forEach { destination ->
            if (destination !in declaredTargets) {
                add("destino-inexistente: ${screen.id} navega a '$destination', que no existe")
            }
        }
        screen.states.forEach { state ->
            if (state !in screenStateVocabulary) {
                add("estado-desconocido: ${screen.id} declara '$state' fuera del vocabulario")
            }
        }
        screen.gestures.forEach { gesture ->
            if (gesture !in gestureVocabulary) {
                add("gesto-desconocido: ${screen.id} declara '$gesture' fuera del vocabulario")
            }
        }
        screen.captureStates.forEach { state ->
            if (state !in screenStateVocabulary) {
                add("captura-estado-desconocido: ${screen.id} captura '$state' fuera del vocabulario")
            }
        }
    }

    // 6. Copy de la acción primaria existe y sale de AppStrings.
    val appStrings = read("$PRESENTATION/commonMain/kotlin/com/agendaqr/destinations/presentation/AppStrings.kt")
    screens.mapNotNull { it.primaryActionKey }.distinct().forEach { key ->
        val exists = appStrings?.let { Regex("\\bval $key\\b").containsMatchIn(it) } ?: false
        if (!exists) {
            add("copy-ausente: la clave de acción primaria '$key' no existe en AppStrings")
        }
    }

    // 7. Pruebas reales y cobertura de accesibilidad.
    val a11ySignal = Regex(
        "contentDescription|SemanticsMatcher|SemanticsProperties|fetchSemanticsNode|useUnmergedTree|Role\\.|assertIsEnabled|LiveRegion|fontScale",
    )
    screens.forEach { screen ->
        screen.tests.forEach { path ->
            if (read(path) == null) {
                add("prueba-ausente: ${screen.id} declara una prueba que no existe: $path")
            }
        }
        if (screen.a11yTests.isEmpty()) {
            add("a11y-ausente: ${screen.id} no declara ninguna prueba de semántica/accesibilidad")
        }
        screen.a11yTests.forEach { path ->
            val content = read(path)
            when {
                content == null -> add("a11y-ausente: ${screen.id} declara una prueba a11y que no existe: $path")
                !a11ySignal.containsMatchIn(content) ->
                    add("a11y-sin-señal: ${screen.id} apunta a $path, que no verifica semántica/accesibilidad")
            }
        }
    }

    // 8. Componentes del sistema reales (no inventados).
    if (productionKotlin.isEmpty()) {
        add("componentes: no se recibió el código de core/ui para verificar los componentes")
    } else {
        val coreText = productionKotlin.joinToString("\n") { it.second }
        screens.flatMap { it.components }.distinct().sorted().forEach { component ->
            if (!Regex("\\b$component\\b").containsMatchIn(coreText)) {
                add("componente-inexistente: '$component' no existe en core/ui")
            }
        }
    }
}

/** Excepciones documentadas que ya no se usan en el código (registro zombie). */
fun exceptionUsageViolations(
    registry: Map<String, List<String>>,
    productionKotlin: List<Pair<String, String>>,
): List<String> = buildList {
    registry.forEach { (id, rules) ->
        rules.forEach { rule ->
            val (kind, file) = rule.split(":", limit = 2).let { it[0] to it.getOrElse(1) { "" } }
            val source = productionKotlin.firstOrNull { it.first.endsWith("/$file") }?.second
            if (source == null) {
                add("excepción-$id: el archivo registrado no existe: $file")
                return@forEach
            }
            val used = when (kind) {
                "border" -> source.contains(".border(") || Regex("\\bBorderStroke\\s*\\(").containsMatchIn(source)
                "shadow" -> source.contains("shadow(") || source.contains(".shadow(") ||
                    source.contains("amElevation(")
                else -> true
            }
            if (!used) {
                add("excepción-$id: documentada como '$rule' pero '$file' ya no la usa (registro obsoleto)")
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Capturas: verificación de escenario (sin zoom/crop/upscale) y redundancia.
// ---------------------------------------------------------------------------

data class CaptureName(
    val surface: String,
    val state: String,
    val widthDp: Int,
    val theme: String,
    val fontScale: Int,
)

data class CaptureArtifact(
    val relPath: String,
    val name: CaptureName,
    val widthPx: Int,
    val heightPx: Int,
    val sha256: String,
)

private val captureNamePattern =
    Regex("([a-z0-9-]+)--([a-z0-9-]+)--(\\d+)dp--(light|dark)--font(100|150|200)\\.png")

val captureWidths: Set<Int> = setOf(320, 360, 720)
private val captureDensities = setOf(1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0)

fun parseCaptureName(fileName: String): CaptureName? {
    val match = captureNamePattern.matchEntire(fileName) ?: return null
    return CaptureName(
        surface = match.groupValues[1],
        state = match.groupValues[2],
        widthDp = match.groupValues[3].toInt(),
        theme = match.groupValues[4],
        fontScale = match.groupValues[5].toInt(),
    )
}

/** Dimensiones reales del bitmap leyendo la cabecera PNG (IHDR). Null si no es PNG válido. */
fun pngDimensions(bytes: ByteArray): Pair<Int, Int>? {
    val signature = intArrayOf(137, 80, 78, 71, 13, 10, 26, 10)
    if (bytes.size < 24) return null
    signature.forEachIndexed { index, value -> if (bytes[index].toInt() and 0xFF != value) return null }
    fun beInt(offset: Int): Int =
        ((bytes[offset].toInt() and 0xFF) shl 24) or
            ((bytes[offset + 1].toInt() and 0xFF) shl 16) or
            ((bytes[offset + 2].toInt() and 0xFF) shl 8) or
            (bytes[offset + 3].toInt() and 0xFF)
    if (String(bytes, 12, 4) != "IHDR") return null
    return beInt(16) to beInt(20)
}

fun sha256(bytes: ByteArray): String =
    MessageDigest.getInstance("SHA-256").digest(bytes)
        .joinToString("") { "%02x".format(it) }

/**
 * Reglas de la galería: escenario fiel (sin viewport falso), sin duplicados
 * y sin artefactos huérfanos. Solo se ejecuta cuando existen artefactos.
 */
fun captureViolations(
    artifacts: List<CaptureArtifact>,
    screens: List<ScreenContract>,
): List<String> = buildList {
    val bySurface = screens.associateBy { it.surface }
    val seenKeys = mutableMapOf<String, String>()
    val seenHashes = mutableMapOf<String, String>()

    artifacts.forEach { artifact ->
        val name = artifact.name
        val screen = bySurface[name.surface]
        if (screen == null) {
            add("captura-orbita: ${artifact.relPath} no corresponde a ninguna superficie del contrato")
        } else if (name.state !in screen.captureStates) {
            add("captura-estado: ${artifact.relPath} usa '${name.state}', no declarado para ${screen.id}")
        }
        if (name.widthDp !in captureWidths) {
            add("captura-viewport: ${artifact.relPath} declara ${name.widthDp}dp fuera de ${captureWidths.sorted()}")
        }
        val density = artifact.widthPx.toDouble() / name.widthDp
        if (density !in captureDensities) {
            add(
                "captura-zoom: ${artifact.relPath} mide ${artifact.widthPx}px para ${name.widthDp}dp " +
                    "(densidad $density); posible zoom/upscale/crop",
            )
        }
        val key = "${name.surface}|${name.state}|${name.widthDp}|${name.theme}|font${name.fontScale}"
        seenKeys.put(key, artifact.relPath)?.let { previous ->
            add("captura-repetida: '$key' aparece en $previous y ${artifact.relPath}")
        }
        seenHashes.put(artifact.sha256, artifact.relPath)?.let { previous ->
            add("captura-duplicada: ${artifact.relPath} es byte-exacta a $previous")
        }
    }

    if (artifacts.isNotEmpty()) {
        screens.filter { it.captureStates.isNotEmpty() }.forEach { screen ->
            if (artifacts.none { it.name.surface == screen.surface }) {
                add("galeria-incompleta: ${screen.id} (${screen.surface}) no tiene ninguna captura")
            }
        }
        val present = artifacts.map { it.name }
        if (present.none { it.theme == "light" && it.widthDp == 320 }) add("galeria-config: falta light-320")
        if (present.none { it.theme == "dark" && it.widthDp == 320 }) add("galeria-config: falta dark-320")
        if (present.none { it.theme == "light" && it.widthDp == 360 }) add("galeria-config: falta light-360")
        if (present.none { it.theme == "dark" && it.widthDp == 360 }) add("galeria-config: falta dark-360")
        if (present.none { it.fontScale == 200 && it.theme == "light" }) add("galeria-config: falta light-360-font200")
        if (present.none { it.fontScale == 200 && it.theme == "dark" }) add("galeria-config: falta dark-360-font200")
    }
}

/** Ubicación del manifiesto de capturas (nunca versionado; vive en `build/`). */
const val captureManifestPath = "build/captures/manifest.json"

private fun jsonString(value: String): String =
    "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

/**
 * Manifiesto determinista de la galería: metadatos por artefacto (pantalla,
 * estado, tema, ancho, densidad, escala de fuente, tamaño real del bitmap,
 * hash, si es obligatorio y por qué se incluye).
 */
fun renderCaptureManifest(artifacts: List<CaptureArtifact>, screens: List<ScreenContract>): String {
    val bySurface = screens.associateBy { it.surface }
    val entries = artifacts.sortedBy { it.relPath }.map { artifact ->
        val name = artifact.name
        val screen = bySurface[name.surface]
        val required = screen != null && name.state in screen.captureStates
        val density = "%.2f".format(artifact.widthPx.toDouble() / name.widthDp)
        val scenarioId = artifact.relPath.substringAfterLast('/').removeSuffix(".png")
        val inclusion = if (required) "estado declarado en el contrato" else "revision adicional"
        val fields = listOf(
            "scenarioId" to jsonString(scenarioId),
            "screen" to jsonString(screen?.id ?: ""),
            "surface" to jsonString(name.surface),
            "state" to jsonString(name.state),
            "theme" to jsonString(name.theme),
            "widthDp" to name.widthDp.toString(),
            "density" to density,
            "fontScale" to name.fontScale.toString(),
            "widthPx" to artifact.widthPx.toString(),
            "heightPx" to artifact.heightPx.toString(),
            "sha256" to jsonString(artifact.sha256),
            "required" to required.toString(),
            "inclusionReason" to jsonString(inclusion),
            "sourceTest" to jsonString(captureSourceTest),
        )
        val body = fields.joinToString(",\n") { (key, value) -> "      \"$key\": $value" }
        "    {\n$body\n    }"
    }
    val configs = requiredCaptureConfigs.joinToString(", ") { jsonString(it) }
    return buildString {
        appendLine("{")
        appendLine("  \"schema\": \"agendaqr.captures/2\",")
        appendLine("  \"requiredConfigs\": [$configs],")
        appendLine("  \"entryCount\": ${artifacts.size},")
        appendLine("  \"entries\": [")
        appendLine(entries.joinToString(",\n"))
        appendLine("  ]")
        appendLine("}")
    }
}

/** Único test que produce la galería estricta por superficie (ADR-0007). */
const val captureSourceTest =
    "feature/destinations/presentation/src/androidUnitTest/kotlin/com/agendaqr/destinations/presentation/LayoutReferenceTest.kt"

/**
 * Reglas del manifiesto: cuando hay capturas, deben tener metadatos completos
 * y sincronizados con la fuente determinista.
 */
fun captureManifestViolations(
    artifacts: List<CaptureArtifact>,
    screens: List<ScreenContract>,
    committed: String?,
): List<String> = buildList {
    if (artifacts.isEmpty()) return@buildList
    if (committed == null) {
        add(
            "manifiesto-ausente: hay ${artifacts.size} capturas sin manifiesto; " +
                "ejecuta ./gradlew generateCaptureManifest",
        )
        return@buildList
    }
    if (committed.replace("\r\n", "\n") != renderCaptureManifest(artifacts, screens)) {
        add(
            "manifiesto-desactualizado: $captureManifestPath no corresponde a las capturas " +
                "presentes; ejecuta ./gradlew generateCaptureManifest",
        )
    }
}

// ---------------------------------------------------------------------------
// Documentación generada: la prosa por pantalla se renderiza desde el contrato.
// ---------------------------------------------------------------------------

fun renderScreenContractDoc(screens: List<ScreenContract>): String = buildString {
    appendLine("# Contrato de interacción por pantalla")
    appendLine()
    appendLine("**Estado:** Generado — no editar a mano.")
    appendLine()
    appendLine(
        "Este documento se renderiza desde `build-logic/src/main/kotlin/ScreenContractGate.kt` " +
            "con `./gradlew generateScreenContractDoc`. `verifyScreenContract` falla si el archivo " +
            "versionado difiere del contrato, de modo que la fuente estructurada y su lectura humana " +
            "no pueden divergir.",
    )
    appendLine()
    appendLine("La intención funcional y las reglas de producto viven en " +
        "[`02-especificacion-ux-ui-v1.md`](02-especificacion-ux-ui-v1.md); " +
        "los valores visuales, en " +
        "[`00-contrato-normativo-diseno.md`](../05-design-system/00-contrato-normativo-diseno.md).")
    appendLine()
    appendLine("## Configuraciones de captura requeridas")
    appendLine()
    requiredCaptureConfigs.forEach { appendLine("- `$it`") }
    appendLine()
    appendLine(
        "Las capturas se generan bajo demanda y no se versionan (ADR-0007). El esquema de nombre es " +
            "`<superficie>--<estado>--<ancho>dp--<tema>--font<escala>.png`; no se admite zoom, " +
            "recorte ni viewport falso.",
    )
    appendLine()
    appendLine("## Superficies")
    appendLine()
    screens.forEach { screen ->
        appendLine("### ${screen.id} — ${screen.title}")
        appendLine()
        appendLine("- superficie: `${screen.surface}`")
        appendLine("- tipo: `${screen.kind}`")
        appendLine("- ruta: ${screen.route?.let { "`$it`" } ?: "—"}")
        if (screen.covers.isNotEmpty()) {
            appendLine("- representa: ${screen.covers.joinToString { "`$it`" }}")
        }
        appendLine("- fuente: `${screen.source}`")
        appendLine("- entradas: ${screen.entryPoints.joinToString { "`$it`" }}")
        appendLine("- salidas: ${screen.exits.joinToString { "`$it`" }}")
        appendLine("- destinos válidos: ${screen.destinations.joinToString { "`$it`" }}")
        appendLine("- acción primaria: ${screen.primaryAction ?: "—"}" +
            (screen.primaryActionKey?.let { " (`AppStrings.$it`)" } ?: ""))
        appendLine("- estados: ${screen.states.joinToString { "`$it`" }}")
        appendLine("- gestos: ${screen.gestures.joinToString { "`$it`" }.ifEmpty { "—" }}")
        appendLine("- capturas: ${screen.captureStates.joinToString { "`$it`" }}")
        appendLine("- componentes: ${screen.components.joinToString { "`$it`" }}")
        appendLine("- pruebas a11y: ${screen.a11yTests.joinToString { "`$it`" }}")
        appendLine("- pruebas: ${screen.tests.joinToString { "`$it`" }}")
        appendLine()
    }
}

fun screenContractDocIsStale(committed: String?, rendered: String): Boolean =
    committed?.replace("\r\n", "\n") != rendered

