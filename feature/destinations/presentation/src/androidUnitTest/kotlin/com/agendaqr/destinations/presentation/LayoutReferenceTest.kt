package com.agendaqr.destinations.presentation

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertTrue
import kotlin.math.abs
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIconBadge
import com.agendaqr.core.ui.components.AqListRow
import com.agendaqr.core.ui.components.AqSegmentedControl
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTonalTag
import com.agendaqr.core.ui.components.AqTappableCard
import com.agendaqr.core.ui.components.AqFilterChip
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.components.AqExtendedFab
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqSearchField
import com.agendaqr.core.ui.components.AqListBody
import com.agendaqr.core.ui.components.AqStatusPill
import com.agendaqr.core.ui.components.AqStatusBanner
import com.agendaqr.core.ui.components.AqEmptyState
import com.agendaqr.core.ui.components.AqLoadMoreFooter
import com.agendaqr.core.ui.components.AqSecondaryButton
import com.agendaqr.core.ui.components.AqCompactAction
import com.agendaqr.core.ui.components.AqHairline
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.components.content
import com.agendaqr.core.ui.theme.AqTheme
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationType
import com.agendaqr.destinations.domain.BiometricAvailability
import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind
import com.agendaqr.destinations.domain.AssociateComprobanteToOperationUseCase
import com.agendaqr.destinations.domain.DeleteComprobanteUseCase
import com.agendaqr.destinations.domain.DeleteOperationWithHistoryUseCase
import com.agendaqr.destinations.domain.FindDuplicateComprobantesUseCase
import com.agendaqr.destinations.domain.GetOperationUseCase
import com.agendaqr.destinations.domain.ObserveContextsUseCase
import com.agendaqr.destinations.domain.ObserveOperationComprobantesUseCase
import com.agendaqr.destinations.domain.ObserveOperationsUseCase
import com.agendaqr.destinations.domain.ObserveUnassociatedComprobantesUseCase
import com.agendaqr.destinations.domain.SaveComprobanteUseCase
import com.agendaqr.destinations.domain.SaveOperationUseCase
import com.agendaqr.destinations.domain.SuggestReceiptAssociationUseCase
import com.agendaqr.destinations.domain.UnassociateComprobanteUseCase
import com.agendaqr.destinations.domain.UpdateOperationUseCase
import com.agendaqr.destinations.presentation.AppStrings
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Verificación de layout de las pantallas críticas en claro y oscuro
 * (ADR-0007). Sin capturas ni hashes versionados: cada escenario se
 * compone y se comprueba por aserciones de layout (raíz visible, árbol
 * semántico no vacío, texto visible y ausencia de recortes laterales).
 */
@RunWith(RobolectricTestRunner::class)
// NATIVE: render real de píxeles (requisito de Roborazzi; es el modo por
// defecto de Robolectric 4.14, se declara explícitamente por claridad).
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h740dp")
open class LayoutReferenceTest {

    @get:Rule
    val compose = createComposeRule()

    private fun verifyLayout(name: String, dark: Boolean, content: @androidx.compose.runtime.Composable () -> Unit) {
        compose.setContent { AqTheme(darkTheme = dark) { content() } }
        compose.waitForIdle()
        assertLayoutHolds(name)
        captureStrict(name, dark)
    }

    /**
     * Escenario → (superficie, estado) de la galería estricta por superficie
     * (ADR-0007): un escenario canónico por par declarado en el
     * contrato. Clave = nombre base, sin sufijo de tema.
     * `verifyScreenContract` valida cada archivo producido cuando la galería
     * existe (esquema, dimensiones reales, duplicados y cobertura).
     */
    private val strictScenarioMap: Map<String, Pair<String, String>> = mapOf(
        "s01" to ("home" to "content"),
        "s01-empty" to ("home" to "empty"),
        "s01-error" to ("home" to "error"),
        "s02-add" to ("add" to "content"),
        "s09-review" to ("importreview" to "content"),
        "destination-detail" to ("destinationdetail" to "content"),
        "destination-detail-error" to ("destinationdetail" to "error"),
        "edit-destination" to ("destinationedit" to "content"),
        "qr-fullscreen" to ("qrfullscreen" to "content"),
        "s04-search" to ("search" to "content"),
        "s04-search-empty" to ("search" to "empty"),
        "s06-contexts" to ("contexts" to "content"),
        "s06-contexts-empty" to ("contexts" to "empty"),
        "context-detail" to ("contextdetail" to "content"),
        "context-detail-empty" to ("contextdetail" to "empty"),
        "s07-list" to ("operations" to "content"),
        "s07-list-empty" to ("operations" to "empty"),
        "operation-detail" to ("operationdetail" to "content"),
        "operation-editor" to ("operationeditor" to "content"),
        "unassociated" to ("unassociated" to "content"),
        // Hallazgo del gate: el estado vacío de la bandeja de comprobantes
        // sin asociar es byte-idéntico al vacío de Actividades — ambas
        // superficies comparten la misma composición de vacío, así que la
        // segunda captura no aporta cobertura (el gate la rechaza como
        // duplicada y el test sigue verificándola por aserciones). Pendiente
        // de decisión de producto: copy propio para el vacío de la bandeja.
        "context-picker" to ("contextpicker" to "selection"),
        "import-batch-review" to ("importbatch" to "review"),
        "import-batch-error" to ("importbatch" to "error"),
        "import-batch-saved" to ("importbatch" to "saved"),
        "account" to ("account" to "content"),
        "account-error" to ("account" to "error"),
        "login" to ("authform" to "content"),
        "login-error" to ("authform" to "error"),
        "local-lock" to ("localaccess" to "locked"),
        "local-lock-incorrect" to ("localaccess" to "error"),
        "camera-capture" to ("camera" to "content"),
    )

    /** Superficies de diálogo (Overlay): la captura es la ventana del
     * diálogo, completa y sin recortes, al tamaño real del componente. */
    private val dialogScenarios = setOf("context-picker", "camera-capture")

    private val dialogRootMatcher = SemanticsMatcher("isDialog") {
        it.config.contains(SemanticsProperties.IsDialog)
    }

    /**
     * Escribe la captura de la galería estricta si (y solo si) el escenario
     * está mapeado, la galería se pidió con `-Pagendaqr.capture=true` y el
     * ancho medido es el único viewport declarado (360 dp). El nombre y el
     * tema salen del propio test: nada de metadatos falsos (ADR-0007).
     */
    private fun captureStrict(name: String, dark: Boolean) {
        if (System.getProperty("agendaqr.capture") != "true") return
        val base = name.removeSuffix("-light").removeSuffix("-dark")
        val (surface, state) = strictScenarioMap[base] ?: return
        val theme = if (dark) "dark" else "light"
        if (base in dialogScenarios) {
            // Overlay: se mide y captura la VENTANA del diálogo completa
            // (su raíz semántica), no la raíz de la pantalla que queda
            // detrás. El test fija la ventana del host para que el diálogo
            // mide el viewport declarado (AqDialog: 0.9 × 400 = 360 dp).
            val dialog = compose.onNode(dialogRootMatcher)
            val bounds = dialog.getUnclippedBoundsInRoot()
            if (Math.round((bounds.right - bounds.left).value) != 360) return
            dialog.captureRoboImage(
                "build/captures/gallery/$surface--$state--360dp--$theme.png",
            )
            return
        }
        val width = compose.onRoot().getUnclippedBoundsInRoot().let { it.right - it.left }.value
        if (Math.round(width) != 360) return
        compose.onRoot().captureRoboImage(
            "build/captures/gallery/$surface--$state--360dp--$theme.png",
        )
    }

    /**
     * Aserciones que sustituyen a la captura: la raíz se compone y se
     * muestra, el árbol semántico no está vacío, hay texto visible y
     * ningún nodo sobresale lateralmente del root (sin recortes
     * laterales). El contenido dentro de un contenedor con scroll
     * horizontal queda exento porque su desborde es intencional.
     */
    private fun assertLayoutHolds(label: String) {
        val root = compose.onRoot().getUnclippedBoundsInRoot()
        compose.onRoot().assertIsDisplayed()
        val rootWidth = (root.right - root.left).value
        val rootHeight = (root.bottom - root.top).value
        assertTrue(
            "$label: el layout no tiene tamaño (root=$root)",
            rootWidth > 200f && rootHeight > 320f,
        )
        val nodes = compose.onAllNodes(AnyNode, useUnmergedTree = true).fetchSemanticsNodes()
        assertTrue("$label: la pantalla no compone nodos", nodes.isNotEmpty())
        assertTrue(
            "$label: no hay texto visible en la pantalla",
            nodes.any { node ->
                node.config.contains(SemanticsProperties.Text) && node.boundsInRoot.width > 0f
            },
        )
        nodes.forEach { node ->
            if (node.insideHorizontalScroll()) return@forEach
            val bounds = node.boundsInRoot
            if (bounds.width <= 0f) return@forEach
            assertTrue(
                "$label: nodo fuera del root horizontalmente ($bounds, root=$root)",
                bounds.left >= root.left.value - 1f && bounds.right <= root.right.value + 1f,
            )
        }
    }

    private fun SemanticsNode.insideHorizontalScroll(): Boolean {
        var current: SemanticsNode? = parent
        while (current != null) {
            if (current.config.contains(SemanticsProperties.HorizontalScrollAxisRange)) return true
            current = current.parent
        }
        return false
    }

    companion object {
        /** Matcher que selecciona todos los nodos del árbol semántico. */
        val AnyNode = SemanticsMatcher("[any]") { true }
        // Timestamp realista para pruebas de layout: 15 ene 2024 10:30:00 (milisegundos)
        const val TEST_TIMESTAMP = 1705314600000L
        // QR REAL para las capturas: PNG legible generado con ZXing (ver
        // QrTestFixture). Nunca una imagen ficticia (el fixture anterior era
        // un píxel verde 1×1 que se veía como un bloque verde uniforme).
        val TEST_QR_BASE64: String = QrTestFixture.base64Png
    }

    @Test fun s01_light() = verifyLayout("s01-light", dark = false) {
        DestinationsScreen(
            state = DestinationsUiState(
                destinations = listOf(
                    com.agendaqr.destinations.domain.Destination(
                        id = "d-1", name = "Carniceria Don Bife",
                        qr = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                        createdAt = LayoutReferenceTest.TEST_TIMESTAMP, updatedAt = LayoutReferenceTest.TEST_TIMESTAMP,
                    ),
                ),
                isLoading = false,
            ),
            onAction = {},
            // La captura de referencia incluye el chrome de producción: la
            // barra inferior aparece en las superficies raíz autenticadas.
            navChrome = com.agendaqr.core.ui.components.AqNavChromeState(
                destinations = listOf(
                    com.agendaqr.core.ui.components.AqNavDestination(
                        appRouteNameOf(AppRoute.Home), AppStrings.Inicio,
                        com.agendaqr.core.ui.components.AqIcons.Home,
                    ),
                    com.agendaqr.core.ui.components.AqNavDestination(
                        appRouteNameOf(AppRoute.Search), AppStrings.Buscar,
                        com.agendaqr.core.ui.components.AqIcons.Search,
                    ),
                    com.agendaqr.core.ui.components.AqNavDestination(
                        appRouteNameOf(AppRoute.Operations), AppStrings.Operaciones,
                        com.agendaqr.core.ui.components.AqIcons.Register,
                    ),
                    com.agendaqr.core.ui.components.AqNavDestination(
                        appRouteNameOf(AppRoute.Contexts), AppStrings.Contextos,
                        com.agendaqr.core.ui.components.AqIcons.Context,
                    ),
                ),
                selectedId = appRouteNameOf(AppRoute.Home),
                onSelect = {},
            ),
        )
    }

    @Test fun s01_dark() = verifyLayout("s01-dark", dark = true) {
        DestinationsScreen(
            state = DestinationsUiState(
                destinations = listOf(
                    com.agendaqr.destinations.domain.Destination(
                        id = "d-1", name = "Carniceria Don Bife",
                        qr = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                        createdAt = LayoutReferenceTest.TEST_TIMESTAMP, updatedAt = LayoutReferenceTest.TEST_TIMESTAMP,
                    ),
                ),
                isLoading = false,
            ),
            onAction = {},
            navChrome = com.agendaqr.core.ui.components.AqNavChromeState(
                destinations = listOf(
                    com.agendaqr.core.ui.components.AqNavDestination(
                        appRouteNameOf(AppRoute.Home), AppStrings.Inicio,
                        com.agendaqr.core.ui.components.AqIcons.Home,
                    ),
                    com.agendaqr.core.ui.components.AqNavDestination(
                        appRouteNameOf(AppRoute.Search), AppStrings.Buscar,
                        com.agendaqr.core.ui.components.AqIcons.Search,
                    ),
                    com.agendaqr.core.ui.components.AqNavDestination(
                        appRouteNameOf(AppRoute.Operations), AppStrings.Operaciones,
                        com.agendaqr.core.ui.components.AqIcons.Register,
                    ),
                    com.agendaqr.core.ui.components.AqNavDestination(
                        appRouteNameOf(AppRoute.Contexts), AppStrings.Contextos,
                        com.agendaqr.core.ui.components.AqIcons.Context,
                    ),
                ),
                selectedId = appRouteNameOf(AppRoute.Home),
                onSelect = {},
            ),
        )
    }

    @Test fun login_light() = verifyLayout("login-light", dark = false) {
        AuthScreen(
            state = AuthUiState(email = "", password = "", isSubmitting = false),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignIn = {},
            onSignUp = {},
        )
    }

    @Test fun login_dark() = verifyLayout("login-dark", dark = true) {
        AuthScreen(
            state = AuthUiState(email = "", password = "", isSubmitting = false),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignIn = {},
            onSignUp = {},
        )
    }

    @Test fun import_batch_light() = verifyLayout("import-batch-light", dark = false) {
        ImportBatchScreen(state = ImportBatchUiState.Idle, onAction = {})
    }

    @Test fun import_batch_dark() = verifyLayout("import-batch-dark", dark = true) {
        ImportBatchScreen(state = ImportBatchUiState.Idle, onAction = {})
    }

    @Test fun s02_add_light() = verifyLayout("s02-add-light", dark = false) {
        AddDestinationScreen(onImport = {}, onBack = {})
    }

    @Test fun s02_add_dark() = verifyLayout("s02-add-dark", dark = true) {
        AddDestinationScreen(onImport = {}, onBack = {})
    }

    // Fallo de decodificación del QR: estado explícito (glifo sobre el
    // marco tonal), nunca un rectángulo blanco sin contexto (§9).
    @Test fun qr_preview_fallback_light() = verifyLayout("qr-preview-fallback-light", dark = false) {
        com.agendaqr.core.ui.components.AqQrPreview(
            encodedQr = "!!",
            unavailableMessage = AppStrings.QrNoSePudoMostrar,
        )
    }

    @Test fun qr_preview_fallback_dark() = verifyLayout("qr-preview-fallback-dark", dark = true) {
        com.agendaqr.core.ui.components.AqQrPreview(
            encodedQr = "!!",
            unavailableMessage = AppStrings.QrNoSePudoMostrar,
        )
    }

    private fun searchFixture() = GlobalSearchUiState(
        query = "mercado",
        results = listOf(
            com.agendaqr.destinations.domain.AgendaSearchResult(
                type = com.agendaqr.destinations.domain.AgendaSearchResultType.QR,
                id = "d-1", title = "Mercado Central", subtitle = "Frutas y verduras",
                destinationId = "d-1",
            ),
            com.agendaqr.destinations.domain.AgendaSearchResult(
                type = com.agendaqr.destinations.domain.AgendaSearchResultType.ACTIVITY,
                id = "op-1", title = "Pago · 120",
                operationId = "op-1",
                dateMillis = 1760000000000,
            ),
            com.agendaqr.destinations.domain.AgendaSearchResult(
                type = com.agendaqr.destinations.domain.AgendaSearchResultType.CONTEXT,
                id = "ctx-1", title = "Mercado", contextId = "ctx-1",
            ),
        ),
    )

    @Test fun s04_search_light() = verifyLayout("s04-search-light", dark = false) {
        GlobalSearchScreen(
            state = searchFixture(),
            navChrome = rootChrome(appRouteNameOf(AppRoute.Search)),
            onAction = {},
            onSelect = {},
        )
    }

    @Test fun s04_search_dark() = verifyLayout("s04-search-dark", dark = true) {
        GlobalSearchScreen(
            state = searchFixture(),
            navChrome = rootChrome(appRouteNameOf(AppRoute.Search)),
            onAction = {},
            onSelect = {},
        )
    }

    private fun contextsFixture() = ContextsUiState(
        contexts = listOf(
            com.agendaqr.destinations.domain.Context(
                id = "ctx-1", name = "Mercado", note = "Compras semanales",
                createdAt = TEST_TIMESTAMP, updatedAt = TEST_TIMESTAMP,
            ),
            com.agendaqr.destinations.domain.Context(
                id = "ctx-2", name = "Ferretería", createdAt = TEST_TIMESTAMP, updatedAt = TEST_TIMESTAMP,
            ),
        ),
        isLoading = false,
    )

    @Test fun s06_contexts_light() = verifyLayout("s06-contexts-light", dark = false) {
        ContextsScreen(
            state = contextsFixture(),
            onAction = {},
            onBack = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Contexts)),
        )
    }

    @Test fun s06_contexts_dark() = verifyLayout("s06-contexts-dark", dark = true) {
        ContextsScreen(
            state = contextsFixture(),
            onAction = {},
            onBack = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Contexts)),
        )
    }

    // El diálogo se abre en su propia ventana (una segunda raíz): su
    // contenido se verifica por semántica (ContextPickerDialogTest,
    // AqDialogProminenceTest), no desde la raíz de la pantalla.

    @Test fun s09_review_light() = verifyLayout("s09-review-light", dark = false) {
        ImportReviewScreen(
            assets = listOf(
                com.agendaqr.destinations.domain.QrAsset(
                    encoded = TEST_QR_BASE64,
                    content = "Mensualidad octubre 2026",
                ),
            ),
            onSaveAll = {},
            onBack = {},
        )
    }

    @Test fun s09_review_dark() = verifyLayout("s09-review-dark", dark = true) {
        ImportReviewScreen(
            assets = listOf(
                com.agendaqr.destinations.domain.QrAsset(
                    encoded = TEST_QR_BASE64,
                    content = "Mensualidad octubre 2026",
                ),
            ),
            onSaveAll = {},
            onBack = {},
        )
    }

    private fun editorFixture() = com.agendaqr.destinations.domain.Destination(
        id = "d-1", name = "Carniceria Don Bife",
        qr = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
        createdAt = TEST_TIMESTAMP, updatedAt = TEST_TIMESTAMP,
    )

    @Test fun edit_destination_light() = verifyLayout("edit-destination-light", dark = false) {
        DestinationEditorScreen(
            existing = editorFixture(),
            onSave = {},
            onImportMany = {},
            onBack = {},
        )
    }

    @Test fun edit_destination_dark() = verifyLayout("edit-destination-dark", dark = true) {
        DestinationEditorScreen(
            existing = editorFixture(),
            onSave = {},
            onImportMany = {},
            onBack = {},
        )
    }

    // Detalle y exhibición QR (referencia de layout UX/UI): hero con
    // metadatos en tarjeta compacta.
    private fun detailFixture() = com.agendaqr.destinations.domain.Destination(
        id = "d-1", name = "Carniceria Don Bife",
        category = "Almacén",
        note = "Compra semanal de la fiambrería.",
        qr = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
        createdAt = TEST_TIMESTAMP, updatedAt = TEST_TIMESTAMP,
    )

    @Test fun destination_detail_light() = verifyLayout("destination-detail-light", dark = false) {
        DestinationDetailScreen(
            destination = detailFixture(),
            onShowQr = {},
            onEdit = {},
            onDelete = {},
            onShare = {},
            onBack = {},
        )
    }

    @Test fun destination_detail_dark() = verifyLayout("destination-detail-dark", dark = true) {
        DestinationDetailScreen(
            destination = detailFixture(),
            onShowQr = {},
            onEdit = {},
            onDelete = {},
            onShare = {},
            onBack = {},
        )
    }

    // Anatomía de fila de actividad (referencia de layout UX/UI): insignia
    // direccional + contraparte + tipo/fecha + monto con signo. El
    // OperationsScreen completo requiere OperationsViewModel (precedente
    // S10: no se compone en Robolectric); aquí se captura el patrón
    // visual con los mismos helpers internos que usa la pantalla.
    @Test fun s07_operation_row_light() = verifyLayout("s07-operation-row-light", dark = false) {
        operationListScreenPattern(selected = 0)
    }

    @Test fun s07_operation_row_dark() = verifyLayout("s07-operation-row-dark", dark = true) {
        operationListScreenPattern(selected = 2)
    }

    // Patrón visual de la pantalla de operaciones (referencia de layout UX/UI):
    // Usa la estructura real de OperationListScreen: AqScreenScaffold con
    // márgenes, búsqueda, filtros, AqListBody con AqListRow.
    // OperationsScreen completo requiere OperationsViewModel (S10: no en Robolectric);
    // se compone el patrón visual con estado y callbacks explícitos.
    @androidx.compose.runtime.Composable
    private fun operationListScreenPattern(selected: Int) {
        val operations = listOf(
            com.agendaqr.destinations.domain.Operation(
                "op-1", com.agendaqr.destinations.domain.OperationType.PAGO,
                occurredAt = 1760000000000, createdAt = 1760000040000,
                amount = "4850", currency = "ARS", personOrEntity = "Cafetería Central",
                concept = "Desayuno de trabajo",
            ),
            com.agendaqr.destinations.domain.Operation(
                "op-2", com.agendaqr.destinations.domain.OperationType.COBRO,
                occurredAt = 1760000000000, createdAt = 1760000040000,
                amount = "25000", currency = "ARS", personOrEntity = "Verdulería Mercedes",
            ),
        )
        val filtered = when (selected) {
            1 -> operations.filter { it.type == OperationType.PAGO }
            2 -> operations.filter { it.type == OperationType.COBRO }
            else -> operations
        }
        val pagosCount = operations.count { it.type == OperationType.PAGO }
        val cobrosCount = operations.count { it.type == OperationType.COBRO }

        AqScreenScaffold(
            topBar = { AqTopBar(title = AppStrings.Operaciones) },
            navDestinations = listOf(
                com.agendaqr.core.ui.components.AqNavDestination("home", AppStrings.Inicio, AqIcons.Home),
                com.agendaqr.core.ui.components.AqNavDestination("search", AppStrings.Buscar, AqIcons.Search),
                com.agendaqr.core.ui.components.AqNavDestination("operations", AppStrings.Operaciones, AqIcons.Register),
                com.agendaqr.core.ui.components.AqNavDestination("contexts", AppStrings.Contextos, AqIcons.Context),
            ),
            navSelectedId = "operations",
            onNavSelect = {},
            floatingActionButton = {
                AqExtendedFab(
                    label = AppStrings.RegistrarOperacion,
                    icon = AqIcons.Add,
                    onClick = {},
                )
            },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqSearchField(
                    value = "",
                    onValueChange = {},
                    label = AppStrings.Buscar,
                    placeholder = AppStrings.BuscarOperaciones,
                    onClear = {},
                    clearLabel = AppStrings.Limpiar,
                )
                AqSegmentedControl(
                    options = listOf(
                        com.agendaqr.core.ui.components.AqSegmentedOption(
                            label = AppStrings.Todas,
                            count = operations.size,
                        ),
                        com.agendaqr.core.ui.components.AqSegmentedOption(
                            label = AppStrings.Pago,
                            icon = AqIcons.Expense,
                            count = pagosCount,
                        ),
                        com.agendaqr.core.ui.components.AqSegmentedOption(
                            label = AppStrings.Cobro,
                            icon = AqIcons.Income,
                            activeTone = AqTone.Success,
                            count = cobrosCount,
                        ),
                    ),
                    selectedIndex = selected,
                    onSelect = {},
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (filtered.isNotEmpty()) {
                AqListBody {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AqSpacing.Md, vertical = AqSpacing.Sm),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                        ) {
                            AqIcon(
                                imageVector = AqIcons.Calendar,
                                contentDescription = null,
                                size = AqMetrics.IconSizeCompact,
                                tint = AqColor.AccentText,
                            )
                            Column {
                                AqText(
                                    monthYearLabel(filtered.first().occurredAt),
                                    size = AqType.BodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                AqText(
                                    filtered.size.toString() + " " + AppStrings.RegistrosLocales,
                                    color = AqColor.TextSecondary,
                                )
                            }
                        }

                    }
                }
            }

            if (filtered.isEmpty()) {
                AqEmptyState(
                    icon = AqIcons.Register,
                    title = AppStrings.AunNoHayOperaciones,
                    subtitle = AppStrings.registerFirstPaymentHint,
                    actionLabel = AppStrings.RegistrarOperacion,
                    onAction = {},
                )
            } else {
                AqListBody(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                ) {
                    Column {
                        filtered.forEachIndexed { index, operation ->
                            val amountText = operationAmountText(operation)
                            val amountColor = if (operation.type == OperationType.COBRO) {
                                AqColor.Success
                            } else {
                                AqColor.Danger
                            }
                            val isLast = index == filtered.lastIndex

                            AqListRow(
                                modifier = Modifier.fillMaxWidth(),
                                title = operationTitle(operation),
                                subtitle = operation.concept?.takeIf { it.isNotBlank() },
                                endMeta = formatDate(operation.occurredAt),
                                leading = {
                                    AqIconBadge(
                                        icon = operationTypeIcon(operation.type),
                                        tone = operationTypeTone(operation.type),
                                    )
                                },
                                trailing = {
                                    amountText?.let {
                                        AqText(
                                            it,
                                            size = AqType.SectionHeader,
                                            fontWeight = FontWeight.SemiBold,
                                            color = amountColor,
                                        )
                                    }
                                },
                                meta = {
                                    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
                                        ) {
                                            AqIcon(
                                                imageVector = AqIcons.Clock,
                                                contentDescription = null,
                                                size = AqMetrics.IconSizeCompact,
                                                tint = AqColor.TextTertiary,
                                            )
                                            AqText(
                                                AppStrings.RegPrefijo + formatTime(operation.createdAt),
                                                color = AqColor.TextTertiary,
                                                size = AqType.Caption,
                                            )
                                        }
                                    }

                                },
                                onClick = {},
                                showDivider = !isLast,
                            )
                        }
                    }
                }

                if (filtered.size < operations.size) {
                    AqLoadMoreFooter(
                        modifier = Modifier.fillMaxWidth().padding(top = AqSpacing.Sm),
                        onLoadMore = {},
                        loadMoreLabel = AppStrings.CargarMas,
                        loadingLabel = AppStrings.Cargando,
                        endLabel = AppStrings.NoHayMasElementos,
                    )
                }
            }
        }
    }

    // --- Acceso local (Fase B): onboarding, bloqueo, PIN incorrecto y
    // biometría. Son la puerta de entrada real (sin login remoto).
    @Test fun local_onboarding_light() = verifyLayout("local-onboarding-light", dark = false) {
        LocalOnboardingScreen(
            state = LocalAccessUiState(mode = LocalAccessMode.Onboarding),
            onPinChanged = {},
            onConfirmationChanged = {},
            onSubmit = {},
            onExploreDemo = {},
        )
    }

    @Test fun local_onboarding_dark() = verifyLayout("local-onboarding-dark", dark = true) {
        LocalOnboardingScreen(
            state = LocalAccessUiState(mode = LocalAccessMode.Onboarding),
            onPinChanged = {},
            onConfirmationChanged = {},
            onSubmit = {},
            onExploreDemo = {},
        )
    }

    @Test fun local_onboarding_mismatch_light() = verifyLayout("local-onboarding-mismatch-light", dark = false) {
        LocalOnboardingScreen(
            state = LocalAccessUiState(
                mode = LocalAccessMode.Onboarding,
                pin = "1234",
                confirmation = "4321",
                error = LocalAccessError.Mismatch,
            ),
            onPinChanged = {},
            onConfirmationChanged = {},
            onSubmit = {},
            onExploreDemo = {},
        )
    }

    @Test fun local_onboarding_mismatch_dark() = verifyLayout("local-onboarding-mismatch-dark", dark = true) {
        LocalOnboardingScreen(
            state = LocalAccessUiState(
                mode = LocalAccessMode.Onboarding,
                pin = "1234",
                confirmation = "4321",
                error = LocalAccessError.Mismatch,
            ),
            onPinChanged = {},
            onConfirmationChanged = {},
            onSubmit = {},
            onExploreDemo = {},
        )
    }

    @Test fun local_lock_light() = verifyLayout("local-lock-light", dark = false) {
        LocalLockScreen(
            state = LocalAccessUiState(mode = LocalAccessMode.Locked),
            onPinChanged = {},
            onSubmit = {},
            onExploreDemo = {},
        )
    }

    @Test fun local_lock_dark() = verifyLayout("local-lock-dark", dark = true) {
        LocalLockScreen(
            state = LocalAccessUiState(mode = LocalAccessMode.Locked),
            onPinChanged = {},
            onSubmit = {},
            onExploreDemo = {},
        )
    }

    @Test fun local_lock_incorrect_light() = verifyLayout("local-lock-incorrect-light", dark = false) {
        LocalLockScreen(
            state = LocalAccessUiState(
                mode = LocalAccessMode.Locked,
                pin = "0000",
                error = LocalAccessError.Incorrect,
                remainingAttempts = 3,
            ),
            onPinChanged = {},
            onSubmit = {},
            onExploreDemo = {},
        )
    }

    @Test fun local_lock_incorrect_dark() = verifyLayout("local-lock-incorrect-dark", dark = true) {
        LocalLockScreen(
            state = LocalAccessUiState(
                mode = LocalAccessMode.Locked,
                pin = "0000",
                error = LocalAccessError.Incorrect,
                remainingAttempts = 3,
            ),
            onPinChanged = {},
            onSubmit = {},
            onExploreDemo = {},
        )
    }

    // Biometría disponible con fallback visible: el PIN sigue presente como
    // vía principal; la biometría es solo el atajo secundario.
    @Test fun local_lock_biometric_light() = verifyLayout("local-lock-biometric-light", dark = false) {
        LocalLockScreen(
            state = LocalAccessUiState(
                mode = LocalAccessMode.Locked,
                biometricAvailability = BiometricAvailability.Available,
                biometricFallbackVisible = true,
            ),
            onPinChanged = {},
            onSubmit = {},
            onBiometric = {},
            onExploreDemo = {},
        )
    }

    @Test fun local_lock_biometric_dark() = verifyLayout("local-lock-biometric-dark", dark = true) {
        LocalLockScreen(
            state = LocalAccessUiState(
                mode = LocalAccessMode.Locked,
                biometricAvailability = BiometricAvailability.Available,
                biometricFallbackVisible = true,
            ),
            onPinChanged = {},
            onSubmit = {},
            onBiometric = {},
            onExploreDemo = {},
        )
    }

    // S01 vacío y con error recuperable (estados reales, no solo poblado).
    @Test fun s01_empty_light() = verifyLayout("s01-empty-light", dark = false) {
        DestinationsScreen(
            state = DestinationsUiState(destinations = emptyList(), isLoading = false),
            onAction = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Home)),
        )
    }

    @Test fun s01_empty_dark() = verifyLayout("s01-empty-dark", dark = true) {
        DestinationsScreen(
            state = DestinationsUiState(destinations = emptyList(), isLoading = false),
            onAction = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Home)),
        )
    }

    @Test fun s01_error_light() = verifyLayout("s01-error-light", dark = false) {
        DestinationsScreen(
            state = DestinationsUiState(
                destinations = emptyList(),
                isLoading = false,
                error = userFacingError(IllegalStateException("save failed"), ErrorFlow.SaveQr),
            ),
            onAction = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Home)),
        )
    }

    @Test fun s01_error_dark() = verifyLayout("s01-error-dark", dark = true) {
        DestinationsScreen(
            state = DestinationsUiState(
                destinations = emptyList(),
                isLoading = false,
                error = userFacingError(IllegalStateException("save failed"), ErrorFlow.SaveQr),
            ),
            onAction = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Home)),
        )
    }

    // S04: vacío sin resultados y error de búsqueda con reintento.
    @Test fun s04_search_empty_light() = verifyLayout("s04-search-empty-light", dark = false) {
        GlobalSearchScreen(
            state = GlobalSearchUiState(query = "inexistente"),
            navChrome = rootChrome(appRouteNameOf(AppRoute.Search)),
            onAction = {},
            onSelect = {},
        )
    }

    @Test fun s04_search_empty_dark() = verifyLayout("s04-search-empty-dark", dark = true) {
        GlobalSearchScreen(
            state = GlobalSearchUiState(query = "inexistente"),
            navChrome = rootChrome(appRouteNameOf(AppRoute.Search)),
            onAction = {},
            onSelect = {},
        )
    }

    @Test fun s04_search_error_light() = verifyLayout("s04-search-error-light", dark = false) {
        GlobalSearchScreen(
            state = GlobalSearchUiState(
                query = "mercado",
                error = userFacingError(IllegalStateException("search failed"), ErrorFlow.Search),
            ),
            navChrome = rootChrome(appRouteNameOf(AppRoute.Search)),
            onAction = {},
            onSelect = {},
        )
    }

    @Test fun s04_search_error_dark() = verifyLayout("s04-search-error-dark", dark = true) {
        GlobalSearchScreen(
            state = GlobalSearchUiState(
                query = "mercado",
                error = userFacingError(IllegalStateException("search failed"), ErrorFlow.Search),
            ),
            navChrome = rootChrome(appRouteNameOf(AppRoute.Search)),
            onAction = {},
            onSelect = {},
        )
    }

    // S12: lote con reconocidos + duplicado + pendiente (resultado,
    // revisión y error de guardado parcial).
    private fun importBatchFixture() = ImportBatch(
        candidates = listOf(
            ImportCandidate(
                id = "imp-1", kind = ImportKind.QR, fingerprint = "fp-1",
                mimeType = "image/png", extension = "png",
                qrAsset = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
            ),
            ImportCandidate(
                id = "imp-2", kind = ImportKind.QR, fingerprint = "fp-2",
                mimeType = "image/png", extension = "png",
                qrAsset = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
            ),
            ImportCandidate(
                id = "imp-3", kind = ImportKind.QR, fingerprint = "fp-1",
                mimeType = "image/png", extension = "png",
                qrAsset = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
            ),
            ImportCandidate(
                id = "imp-4", kind = ImportKind.DESCONOCIDO, fingerprint = "fp-4",
                mimeType = "image/png", extension = "png", error = "No se pudo clasificar",
            ),
        ),
    )

    @Test fun import_batch_result_light() = verifyLayout("import-batch-result-light", dark = false) {
        ImportBatchScreen(state = ImportBatchUiState.Result(importBatchFixture()), onAction = {})
    }

    @Test fun import_batch_result_dark() = verifyLayout("import-batch-result-dark", dark = true) {
        ImportBatchScreen(state = ImportBatchUiState.Result(importBatchFixture()), onAction = {})
    }

    @Test fun import_batch_review_light() = verifyLayout("import-batch-review-light", dark = false) {
        ImportBatchScreen(state = ImportBatchUiState.Review(importBatchFixture()), onAction = {})
    }

    @Test fun import_batch_review_dark() = verifyLayout("import-batch-review-dark", dark = true) {
        ImportBatchScreen(state = ImportBatchUiState.Review(importBatchFixture()), onAction = {})
    }

    @Test fun import_batch_error_light() = verifyLayout("import-batch-error-light", dark = false) {
        ImportBatchScreen(
            state = ImportBatchUiState.Error(
                error = userFacingError(IllegalStateException("batch save failed"), ErrorFlow.ImportBatchSave),
                batch = importBatchFixture(),
            ),
            onAction = {},
        )
    }

    @Test fun import_batch_error_dark() = verifyLayout("import-batch-error-dark", dark = true) {
        ImportBatchScreen(
            state = ImportBatchUiState.Error(
                error = userFacingError(IllegalStateException("batch save failed"), ErrorFlow.ImportBatchSave),
                batch = importBatchFixture(),
            ),
            onAction = {},
        )
    }

    // S06 vacío: sin contextos, con guía de creación.
    @Test fun s06_contexts_empty_light() = verifyLayout("s06-contexts-empty-light", dark = false) {
        ContextsScreen(
            state = ContextsUiState(contexts = emptyList(), isLoading = false),
            onAction = {},
            onBack = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Contexts)),
        )
    }

    @Test fun s06_contexts_empty_dark() = verifyLayout("s06-contexts-empty-dark", dark = true) {
        ContextsScreen(
            state = ContextsUiState(contexts = emptyList(), isLoading = false),
            onAction = {},
            onBack = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Contexts)),
        )
    }

    // Cuenta y respaldo: estado ocupado del formulario (la superficie
    // opcional que conserva el login remoto fuera del arranque).
    @Test fun login_submitting_light() = verifyLayout("login-submitting-light", dark = false) {
        AuthScreen(
            state = AuthUiState(email = "persona@example.com", password = "secreta123", isSubmitting = true),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignIn = {},
            onSignUp = {},
        )
    }

    @Test fun login_submitting_dark() = verifyLayout("login-submitting-dark", dark = true) {
        AuthScreen(
            state = AuthUiState(email = "persona@example.com", password = "secreta123", isSubmitting = true),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignIn = {},
            onSignUp = {},
        )
    }

    // S09 con varios QR: conteo plural y guardado múltiple.
    @Test fun s09_review_multi_light() = verifyLayout("s09-review-multi-light", dark = false) {
        ImportReviewScreen(
            assets = listOf(
                com.agendaqr.destinations.domain.QrAsset(
                    encoded = TEST_QR_BASE64,
                    content = "Mensualidad octubre 2026",
                ),
                com.agendaqr.destinations.domain.QrAsset(
                    encoded = TEST_QR_BASE64,
                    content = "Cuota colegio",
                ),
                com.agendaqr.destinations.domain.QrAsset(
                    encoded = TEST_QR_BASE64,
                    content = "Multa",
                ),
            ),
            onSaveAll = {},
            onBack = {},
        )
    }

    @Test fun s09_review_multi_dark() = verifyLayout("s09-review-multi-dark", dark = true) {
        ImportReviewScreen(
            assets = listOf(
                com.agendaqr.destinations.domain.QrAsset(
                    encoded = TEST_QR_BASE64,
                    content = "Mensualidad octubre 2026",
                ),
                com.agendaqr.destinations.domain.QrAsset(
                    encoded = TEST_QR_BASE64,
                    content = "Cuota colegio",
                ),
                com.agendaqr.destinations.domain.QrAsset(
                    encoded = TEST_QR_BASE64,
                    content = "Multa",
                ),
            ),
            onSaveAll = {},
            onBack = {},
        )
    }

    // Biometría lista (Available, sin mensaje de fallback): solo el atajo
    // visible junto al PIN — distinto del estado de fallo ya capturado.
    @Test fun local_lock_biometric_ready_light() = verifyLayout("local-lock-biometric-ready-light", dark = false) {
        LocalLockScreen(
            state = LocalAccessUiState(
                mode = LocalAccessMode.Locked,
                biometricAvailability = BiometricAvailability.Available,
            ),
            onPinChanged = {},
            onSubmit = {},
            onBiometric = {},
            onExploreDemo = {},
        )
    }

    @Test fun local_lock_biometric_ready_dark() = verifyLayout("local-lock-biometric-ready-dark", dark = true) {
        LocalLockScreen(
            state = LocalAccessUiState(
                mode = LocalAccessMode.Locked,
                biometricAvailability = BiometricAvailability.Available,
            ),
            onPinChanged = {},
            onSubmit = {},
            onBiometric = {},
            onExploreDemo = {},
        )
    }

    // S12 Saving: guardado local en curso — acción primaria ocupada, sin
    // vuelta atrás (§3); nunca "Guardado" antes de persistir (§5).
    @Test fun import_batch_saving_light() = verifyLayout("import-batch-saving-light", dark = false) {
        ImportBatchScreen(state = ImportBatchUiState.Saving(importBatchFixture()), onAction = {})
    }

    @Test fun import_batch_saving_dark() = verifyLayout("import-batch-saving-dark", dark = true) {
        ImportBatchScreen(state = ImportBatchUiState.Saving(importBatchFixture()), onAction = {})
    }

    // S12 Saved: persistencia local terminada; los pendientes conservan su
    // revisión (procesamiento parcial del contrato).
    @Test fun import_batch_saved_light() = verifyLayout("import-batch-saved-light", dark = false) {
        ImportBatchScreen(state = ImportBatchUiState.Saved(importBatchFixture()), onAction = {})
    }

    @Test fun import_batch_saved_dark() = verifyLayout("import-batch-saved-dark", dark = true) {
        ImportBatchScreen(state = ImportBatchUiState.Saved(importBatchFixture()), onAction = {})
    }

    // Cuenta y respaldo (superficie opcional del login remoto, fuera del
    // arranque local-first): SignedOut con un doble determinista — el
    // repositorio real de Supabase no puede construirse en JVM (supabase-kt
    // exige Settings nativo), y lo validado visualmente es el AuthForm.
    private val signedOutAuthRepository =
        object : com.agendaqr.destinations.domain.AuthRepository {
            override val state: kotlinx.coroutines.flow.Flow<com.agendaqr.destinations.domain.AuthState> =
                kotlinx.coroutines.flow.flowOf(com.agendaqr.destinations.domain.AuthState.SignedOut)
            override suspend fun signIn(email: String, password: String) =
                error("Not in a visual capture")
            override suspend fun signUp(email: String, password: String) =
                error("Not in a visual capture")
            override suspend fun signOut() = error("Not in a visual capture")
        }

    @Test fun account_light() = verifyLayout("account-light", dark = false) {
        AccountScreen(navChrome = null, appHosts = null, onBack = {}, authRepository = signedOutAuthRepository)
    }

    @Test fun account_dark() = verifyLayout("account-dark", dark = true) {
        AccountScreen(navChrome = null, appHosts = null, onBack = {}, authRepository = signedOutAuthRepository)
    }

    // UX-030: nombres largos no solapan ni tapan la acción primaria.
    @Test fun s01_long_name_light() = verifyLayout("s01-long-name-light", dark = false) {
        DestinationsScreen(
            state = DestinationsUiState(
                destinations = listOf(
                    com.agendaqr.destinations.domain.Destination(
                        id = "d-long",
                        name = "Carnicería Don Bife — Sucursal Centro Norte Av. San Martín 1852 Local 4",
                        qr = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                        createdAt = TEST_TIMESTAMP, updatedAt = TEST_TIMESTAMP,
                    ),
                ),
                isLoading = false,
            ),
            onAction = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Home)),
        )
    }

    @Test fun s01_long_name_dark() = verifyLayout("s01-long-name-dark", dark = true) {
        DestinationsScreen(
            state = DestinationsUiState(
                destinations = listOf(
                    com.agendaqr.destinations.domain.Destination(
                        id = "d-long",
                        name = "Carnicería Don Bife — Sucursal Centro Norte Av. San Martín 1852 Local 4",
                        qr = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                        createdAt = TEST_TIMESTAMP, updatedAt = TEST_TIMESTAMP,
                    ),
                ),
                isLoading = false,
            ),
            onAction = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Home)),
        )
    }

    // --- Escenarios nuevos: estados de importación ---

    // Error de persistencia con todos los candidatos fallidos
    @Test fun import_batch_error_all_failed_light() = verifyLayout("import-batch-error-all-failed-light", dark = false) {
        ImportBatchScreen(
            state = ImportBatchUiState.Error(
                error = userFacingError(IllegalStateException("storage unavailable"), ErrorFlow.ImportBatchSave),
                batch = importBatchFixture(),
            ),
            onAction = {},
        )
    }

    @Test fun import_batch_error_all_failed_dark() = verifyLayout("import-batch-error-all-failed-dark", dark = true) {
        ImportBatchScreen(
            state = ImportBatchUiState.Error(
                error = userFacingError(IllegalStateException("storage unavailable"), ErrorFlow.ImportBatchSave),
                batch = importBatchFixture(),
            ),
            onAction = {},
        )
    }

    // Guardado parcial: algunos guardados, algunos pendientes
    @Test fun import_batch_partial_save_light() = verifyLayout("import-batch-partial-save-light", dark = false) {
        val partialBatch = ImportBatch(
            candidates = listOf(
                ImportCandidate(
                    id = "imp-saved", kind = ImportKind.QR, fingerprint = "fp-saved",
                    mimeType = "image/png", extension = "png",
                    qrAsset = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                ),
                ImportCandidate(
                    id = "imp-pending", kind = ImportKind.DESCONOCIDO, fingerprint = "fp-pending",
                    mimeType = "image/png", extension = "png", error = "No se pudo clasificar",
                ),
            ),
        )
        ImportBatchScreen(state = ImportBatchUiState.Saved(partialBatch), onAction = {})
    }

    @Test fun import_batch_partial_save_dark() = verifyLayout("import-batch-partial-save-dark", dark = true) {
        val partialBatch = ImportBatch(
            candidates = listOf(
                ImportCandidate(
                    id = "imp-saved", kind = ImportKind.QR, fingerprint = "fp-saved",
                    mimeType = "image/png", extension = "png",
                    qrAsset = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                ),
                ImportCandidate(
                    id = "imp-pending", kind = ImportKind.DESCONOCIDO, fingerprint = "fp-pending",
                    mimeType = "image/png", extension = "png", error = "No se pudo clasificar",
                ),
            ),
        )
        ImportBatchScreen(state = ImportBatchUiState.Saved(partialBatch), onAction = {})
    }

    // Reintento después de un error
    @Test fun import_batch_retry_after_error_light() = verifyLayout("import-batch-retry-after-error-light", dark = false) {
        ImportBatchScreen(
            state = ImportBatchUiState.Error(
                error = userFacingError(IllegalStateException("storage unavailable"), ErrorFlow.ImportBatchSave),
                batch = importBatchFixture(),
            ),
            onAction = {},
        )
    }

    @Test fun import_batch_retry_after_error_dark() = verifyLayout("import-batch-retry-after-error-dark", dark = true) {
        ImportBatchScreen(
            state = ImportBatchUiState.Error(
                error = userFacingError(IllegalStateException("storage unavailable"), ErrorFlow.ImportBatchSave),
                batch = importBatchFixture(),
            ),
            onAction = {},
        )
    }

    // Estado después de un reintento exitoso
    @Test fun import_batch_after_successful_retry_light() = verifyLayout("import-batch-after-successful-retry-light", dark = false) {
        ImportBatchScreen(state = ImportBatchUiState.Saved(importBatchFixture()), onAction = {})
    }

    @Test fun import_batch_after_successful_retry_dark() = verifyLayout("import-batch-after-successful-retry-dark", dark = true) {
        ImportBatchScreen(state = ImportBatchUiState.Saved(importBatchFixture()), onAction = {})
    }

    // Revisión con varias incidencias y nombres largos
    @Test fun import_batch_review_multiple_issues_light() = verifyLayout("import-batch-review-multiple-issues-light", dark = false) {
        val longNameBatch = ImportBatch(
            candidates = listOf(
                ImportCandidate(
                    id = "imp-long-1", kind = ImportKind.DESCONOCIDO, fingerprint = "fp-long-1",
                    mimeType = "image/png", extension = "png", error = "No se pudo clasificar",
                ),
                ImportCandidate(
                    id = "imp-long-2", kind = ImportKind.QR, fingerprint = "fp-long-2",
                    mimeType = "image/png", extension = "png",
                    qrAsset = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                ),
                ImportCandidate(
                    id = "imp-long-3", kind = ImportKind.QR, fingerprint = "fp-long-2",
                    mimeType = "image/png", extension = "png",
                    qrAsset = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                ),
                ImportCandidate(
                    id = "imp-long-4", kind = ImportKind.DESCONOCIDO, fingerprint = "fp-long-4",
                    mimeType = "image/jpeg", extension = "jpg", error = "Formato no soportado",
                ),
                ImportCandidate(
                    id = "imp-long-5", kind = ImportKind.DESCONOCIDO, fingerprint = "fp-long-5",
                    mimeType = "application/pdf", extension = "pdf", error = "No se pudo leer el PDF",
                ),
            ),
        )
        ImportBatchScreen(state = ImportBatchUiState.Review(longNameBatch), onAction = {})
    }

    @Test fun import_batch_review_multiple_issues_dark() = verifyLayout("import-batch-review-multiple-issues-dark", dark = true) {
        val longNameBatch = ImportBatch(
            candidates = listOf(
                ImportCandidate(
                    id = "imp-long-1", kind = ImportKind.DESCONOCIDO, fingerprint = "fp-long-1",
                    mimeType = "image/png", extension = "png", error = "No se pudo clasificar",
                ),
                ImportCandidate(
                    id = "imp-long-2", kind = ImportKind.QR, fingerprint = "fp-long-2",
                    mimeType = "image/png", extension = "png",
                    qrAsset = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                ),
                ImportCandidate(
                    id = "imp-long-3", kind = ImportKind.QR, fingerprint = "fp-long-2",
                    mimeType = "image/png", extension = "png",
                    qrAsset = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                ),
                ImportCandidate(
                    id = "imp-long-4", kind = ImportKind.DESCONOCIDO, fingerprint = "fp-long-4",
                    mimeType = "image/jpeg", extension = "jpg", error = "Formato no soportado",
                ),
                ImportCandidate(
                    id = "imp-long-5", kind = ImportKind.DESCONOCIDO, fingerprint = "fp-long-5",
                    mimeType = "application/pdf", extension = "pdf", error = "No se pudo leer el PDF",
                ),
            ),
        )
        ImportBatchScreen(state = ImportBatchUiState.Review(longNameBatch), onAction = {})
    }

    // Login con error
    @Test fun login_error_light() = verifyLayout("login-error-light", dark = false) {
        AuthScreen(
            state = AuthUiState(
                email = "invalid",
                password = "123",
                isSubmitting = false,
                errorMessage = userFacingError(IllegalStateException("invalid credentials"), ErrorFlow.SignIn),
                errorFlow = ErrorFlow.SignIn,
            ),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignIn = {},
            onSignUp = {},
        )
    }

    @Test fun login_error_dark() = verifyLayout("login-error-dark", dark = true) {
        AuthScreen(
            state = AuthUiState(
                email = "invalid",
                password = "123",
                isSubmitting = false,
                errorMessage = userFacingError(IllegalStateException("invalid credentials"), ErrorFlow.SignIn),
                errorFlow = ErrorFlow.SignIn,
            ),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignIn = {},
            onSignUp = {},
        )
    }

    internal fun rootChrome(selected: String): com.agendaqr.core.ui.components.AqNavChromeState {
        return com.agendaqr.core.ui.components.AqNavChromeState(
            destinations = listOf(
                com.agendaqr.core.ui.components.AqNavDestination(
                    appRouteNameOf(AppRoute.Home), AppStrings.Inicio,
                    com.agendaqr.core.ui.components.AqIcons.Home,
                ),
                com.agendaqr.core.ui.components.AqNavDestination(
                    appRouteNameOf(AppRoute.Search), AppStrings.Buscar,
                    com.agendaqr.core.ui.components.AqIcons.Search,
                ),
                com.agendaqr.core.ui.components.AqNavDestination(
                    appRouteNameOf(AppRoute.Operations), AppStrings.Operaciones,
                    com.agendaqr.core.ui.components.AqIcons.Register,
                ),
                com.agendaqr.core.ui.components.AqNavDestination(
                    appRouteNameOf(AppRoute.Contexts), AppStrings.Contextos,
                    com.agendaqr.core.ui.components.AqIcons.Context,
                ),
            ),
            selectedId = selected,
            onSelect = {},
        )
    }

    // ------------------------------------------------------------------
    // Galería estricta por superficie (ADR-0007): escenarios de las
    // superficies que no tenían productor. Los fixtures reutilizan los
    // helpers de este archivo; las pantallas de operaciones se componen con
    // un OperationsViewModel real sobre repositorios fake (el mismo patrón
    // que usa commonTest), que sustituye el precedente "S10" de no componer
    // OperationsScreen en Robolectric.
    // ------------------------------------------------------------------

    @Test fun qr_fullscreen_light() = verifyLayout("qr-fullscreen-light", dark = false) {
        QrFullscreenPattern(
            encodedQr = TEST_QR_BASE64,
            onBack = {},
            destinationName = "Carniceria Don Bife",
        )
    }

    @Test fun qr_fullscreen_dark() = verifyLayout("qr-fullscreen-dark", dark = true) {
        QrFullscreenPattern(
            encodedQr = TEST_QR_BASE64,
            onBack = {},
            destinationName = "Carniceria Don Bife",
        )
    }

    @Test fun destination_detail_error_light() = verifyLayout("destination-detail-error-light", dark = false) {
        DestinationDetailScreen(
            destination = detailFixture(),
            onShowQr = {},
            onEdit = {},
            onDelete = {},
            onShare = {},
            onBack = {},
            syncStatus = ElementSyncStatus.Dead,
            onRetrySync = {},
        )
    }

    @Test fun destination_detail_error_dark() = verifyLayout("destination-detail-error-dark", dark = true) {
        DestinationDetailScreen(
            destination = detailFixture(),
            onShowQr = {},
            onEdit = {},
            onDelete = {},
            onShare = {},
            onBack = {},
            syncStatus = ElementSyncStatus.Dead,
            onRetrySync = {},
        )
    }

    private fun contextContentsFixture(empty: Boolean): ContextsUiState {
        val context = com.agendaqr.destinations.domain.Context(
            id = "ctx-1", name = "Mercado", note = "Compras semanales",
            createdAt = TEST_TIMESTAMP, updatedAt = TEST_TIMESTAMP,
        )
        return ContextsUiState(
            contexts = listOf(context),
            contents = com.agendaqr.destinations.domain.ContextContents(
                context = context,
                destinations = if (empty) emptyList() else listOf(detailFixture()),
                operations = if (empty) {
                    emptyList()
                } else {
                    listOf(
                        Operation(
                            "op-1", OperationType.PAGO,
                            occurredAt = TEST_TIMESTAMP, createdAt = TEST_TIMESTAMP,
                            amount = "4850", currency = "ARS", personOrEntity = "Cafetería Central",
                        ),
                    )
                },
                comprobantes = if (empty) {
                    emptyList()
                } else {
                    listOf(
                        com.agendaqr.destinations.domain.Comprobante(
                            id = "r-1", file = "recibo_ketal.jpg",
                            createdAt = TEST_TIMESTAMP, updatedAt = TEST_TIMESTAMP,
                            provenance = com.agendaqr.destinations.domain.ReceiptProvenance.RECIBIDO,
                        ),
                    )
                },
            ),
            route = ContextRoute.Detail("ctx-1"),
            isLoading = false,
        )
    }

    @Test fun context_detail_light() = verifyLayout("context-detail-light", dark = false) {
        ContextsScreen(
            state = contextContentsFixture(empty = false),
            onAction = {},
            onBack = {},
        )
    }

    @Test fun context_detail_dark() = verifyLayout("context-detail-dark", dark = true) {
        ContextsScreen(
            state = contextContentsFixture(empty = false),
            onAction = {},
            onBack = {},
        )
    }

    @Test fun context_detail_empty_light() = verifyLayout("context-detail-empty-light", dark = false) {
        ContextsScreen(
            state = contextContentsFixture(empty = true),
            onAction = {},
            onBack = {},
        )
    }

    @Test fun context_detail_empty_dark() = verifyLayout("context-detail-empty-dark", dark = true) {
        ContextsScreen(
            state = contextContentsFixture(empty = true),
            onAction = {},
            onBack = {},
        )
    }

    /**
     * Selector de contexto (S08, Overlay): el diálogo abre su propia
     * ventana, así que no se verifica ni captura desde la raíz — el título
     * se comprueba por semántica y la captura estricta toma la ventana
     * completa del diálogo. La ventana del host se fija en w400dp para que
     * el diálogo (0.9 × 400) mida exactamente 360 dp.
     */
    @Test
    @org.robolectric.annotation.Config(qualifiers = "w400dp-h840dp")
    fun context_picker_light() {
        compose.setContent {
            AqTheme(darkTheme = false) {
                ContextPickerDialog(
                    contexts = contextsFixture().contexts,
                    selection = ContextSelection(contextId = "ctx-1"),
                    onSelect = {},
                    onRemove = {},
                    onCancel = {},
                    onCreateContext = { _, _ -> },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.SeleccionarContexto).assertIsDisplayed()
        captureStrict("context-picker", dark = false)
    }

    @Test
    @org.robolectric.annotation.Config(qualifiers = "w400dp-h840dp")
    fun context_picker_dark() {
        compose.setContent {
            AqTheme(darkTheme = true) {
                ContextPickerDialog(
                    contexts = contextsFixture().contexts,
                    selection = ContextSelection(contextId = "ctx-1"),
                    onSelect = {},
                    onRemove = {},
                    onCancel = {},
                    onCreateContext = { _, _ -> },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.SeleccionarContexto).assertIsDisplayed()
        captureStrict("context-picker", dark = true)
    }

    /** Repositorio de autenticación que siempre falla (estado error de cuenta). */
    private class FailingAuthRepository : com.agendaqr.destinations.domain.AuthRepository {
        override val state = kotlinx.coroutines.flow.MutableStateFlow<com.agendaqr.destinations.domain.AuthState>(
            com.agendaqr.destinations.domain.AuthState.SignedOut,
        )

        override suspend fun signIn(email: String, password: String): com.agendaqr.destinations.domain.AuthUser =
            throw IllegalStateException("invalid credentials")

        override suspend fun signUp(email: String, password: String): com.agendaqr.destinations.domain.SignUpResult =
            throw IllegalStateException("invalid credentials")

        override suspend fun signOut() = Unit
    }

    /**
     * Cuenta con error de inicio de sesión: el estado se alcanza por la
     * interacción real (credenciales inválidas contra el repositorio que
     * falla); la captura solo ocurre cuando el error ES visible.
     */
    @Test fun account_error_light() {
        compose.setContent {
            AqTheme(darkTheme = false) {
                AccountScreen(
                    navChrome = null,
                    appHosts = null,
                    onBack = {},
                    authRepository = FailingAuthRepository(),
                )
            }
        }
        compose.waitForIdle()
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("demo@ejemplo.com")
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("invalida")
        compose.onNodeWithText(AppStrings.IniciarSesion).performClick()
        compose.waitUntil {
            compose.onAllNodesWithText(AppStrings.ErrorSignInWhat).fetchSemanticsNodes().isNotEmpty()
        }
        assertLayoutHolds("account-error-light")
        captureStrict("account-error", dark = false)
    }

    @Test fun account_error_dark() {
        compose.setContent {
            AqTheme(darkTheme = true) {
                AccountScreen(
                    navChrome = null,
                    appHosts = null,
                    onBack = {},
                    authRepository = FailingAuthRepository(),
                )
            }
        }
        compose.waitForIdle()
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("demo@ejemplo.com")
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("invalida")
        compose.onNodeWithText(AppStrings.IniciarSesion).performClick()
        compose.waitUntil {
            compose.onAllNodesWithText(AppStrings.ErrorSignInWhat).fetchSemanticsNodes().isNotEmpty()
        }
        assertLayoutHolds("account-error-dark")
        captureStrict("account-error", dark = true)
    }

    /** Operación y comprobante de ejemplo para la familia de Actividades. */
    private fun operationFixture(id: String) = Operation(
        id, OperationType.PAGO,
        occurredAt = TEST_TIMESTAMP, createdAt = TEST_TIMESTAMP,
        amount = "4850", currency = "ARS", personOrEntity = "Cafetería Central",
        concept = "Desayuno de trabajo",
    )

    private fun unassociatedReceiptFixture(id: String) =
        com.agendaqr.destinations.domain.Comprobante(
            id = id, file = "recibo_$id.jpg",
            createdAt = TEST_TIMESTAMP, updatedAt = TEST_TIMESTAMP,
            provenance = com.agendaqr.destinations.domain.ReceiptProvenance.RECIBIDO,
        )

    private class FakeOperations(initial: List<Operation> = emptyList()) :
        com.agendaqr.destinations.domain.OperationRepository {
        private val state = kotlinx.coroutines.flow.MutableStateFlow(initial)
        override fun observe(): kotlinx.coroutines.flow.Flow<List<Operation>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(operation: Operation) {
            state.value = state.value.filterNot { it.id == operation.id } + operation
        }
        override suspend fun update(operation: Operation) {
            state.value = state.value.map { if (it.id == operation.id) operation else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class FakeReceipts(initial: List<com.agendaqr.destinations.domain.Comprobante> = emptyList()) :
        com.agendaqr.destinations.domain.ComprobanteRepository {
        private val state = kotlinx.coroutines.flow.MutableStateFlow(initial)
        override fun observe(): kotlinx.coroutines.flow.Flow<List<com.agendaqr.destinations.domain.Comprobante>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(comprobante: com.agendaqr.destinations.domain.Comprobante) {
            state.value = state.value + comprobante
        }
        override suspend fun update(comprobante: com.agendaqr.destinations.domain.Comprobante) {
            state.value = state.value.map { if (it.id == comprobante.id) comprobante else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class FakeContextsRepository : com.agendaqr.destinations.domain.ContextRepository {
        private val state = kotlinx.coroutines.flow.MutableStateFlow<List<com.agendaqr.destinations.domain.Context>>(
            emptyList(),
        )
        override fun observe(): kotlinx.coroutines.flow.Flow<List<com.agendaqr.destinations.domain.Context>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(context: com.agendaqr.destinations.domain.Context) {
            state.value = state.value + context
        }
        override suspend fun update(context: com.agendaqr.destinations.domain.Context) {
            state.value = state.value.map { if (it.id == context.id) context else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class MemoryFiles : com.agendaqr.destinations.domain.ComprobanteFileStore {
        private val files = mutableMapOf<String, ByteArray>()
        override suspend fun save(id: String, bytes: ByteArray, extension: String): String {
            val path = "file://$id.$extension"
            files[path] = bytes.copyOf()
            return path
        }
        override suspend fun read(file: String): ByteArray? = files[file]?.copyOf()
        override suspend fun delete(file: String) {
            files.remove(file)
        }
    }

    private class FakeHistory : com.agendaqr.destinations.domain.DeletedOperationHistoryRepository {
        private val state = kotlinx.coroutines.flow.MutableStateFlow<List<com.agendaqr.destinations.domain.DeletedOperationHistory>>(
            emptyList(),
        )
        override fun observe(): kotlinx.coroutines.flow.Flow<List<com.agendaqr.destinations.domain.DeletedOperationHistory>> = state
        override suspend fun save(history: com.agendaqr.destinations.domain.DeletedOperationHistory) {
            state.value = state.value + history
        }
    }

    private fun operationsViewModel(
        operations: List<Operation> = emptyList(),
        receipts: List<com.agendaqr.destinations.domain.Comprobante> = emptyList(),
    ): OperationsViewModel {
        val operationRepo = FakeOperations(operations)
        val receiptRepo = FakeReceipts(receipts)
        val files = MemoryFiles()
        val contexts = FakeContextsRepository()
        val history = FakeHistory()
        val scope = kotlinx.coroutines.CoroutineScope(
            kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.Unconfined,
        )
        return OperationsViewModel(
            observeOperations = ObserveOperationsUseCase(operationRepo),
            observeContexts = ObserveContextsUseCase(contexts),
            observeUnassociated = ObserveUnassociatedComprobantesUseCase(receiptRepo),
            observeOperationComprobantes = ObserveOperationComprobantesUseCase(receiptRepo),
            getOperation = GetOperationUseCase(operationRepo),
            saveOperation = SaveOperationUseCase(operationRepo),
            updateOperation = UpdateOperationUseCase(operationRepo),
            deleteOperation = DeleteOperationWithHistoryUseCase(operationRepo, receiptRepo, files, history),
            associate = AssociateComprobanteToOperationUseCase(operationRepo, receiptRepo),
            unassociate = UnassociateComprobanteUseCase(receiptRepo),
            saveComprobante = SaveComprobanteUseCase(receiptRepo, files),
            findDuplicates = FindDuplicateComprobantesUseCase(receiptRepo, files),
            getComprobante = com.agendaqr.destinations.domain.GetComprobanteUseCase(receiptRepo),
            suggestReceiptAssociation = SuggestReceiptAssociationUseCase(receiptRepo, operationRepo),
            deleteComprobante = DeleteComprobanteUseCase(receiptRepo, files),
            comprobanteFiles = files,
            scope = scope,
        )
    }

    @Test fun s07_list_light() = verifyLayout("s07-list-light", dark = false) {
        val vm = operationsViewModel(operations = listOf(operationFixture("op-1"), operationFixture("op-2")))
        OperationListScreen(
            state = vm.state.value,
            viewModel = vm,
            navChrome = rootChrome(appRouteNameOf(AppRoute.Operations)),
        )
    }

    @Test fun s07_list_dark() = verifyLayout("s07-list-dark", dark = true) {
        val vm = operationsViewModel(operations = listOf(operationFixture("op-1"), operationFixture("op-2")))
        OperationListScreen(
            state = vm.state.value,
            viewModel = vm,
            navChrome = rootChrome(appRouteNameOf(AppRoute.Operations)),
        )
    }

    @Test fun s07_list_empty_light() = verifyLayout("s07-list-empty-light", dark = false) {
        val vm = operationsViewModel()
        OperationListScreen(
            state = vm.state.value,
            viewModel = vm,
            navChrome = rootChrome(appRouteNameOf(AppRoute.Operations)),
        )
    }

    @Test fun s07_list_empty_dark() = verifyLayout("s07-list-empty-dark", dark = true) {
        val vm = operationsViewModel()
        OperationListScreen(
            state = vm.state.value,
            viewModel = vm,
            navChrome = rootChrome(appRouteNameOf(AppRoute.Operations)),
        )
    }

    @Test fun operation_detail_light() = verifyLayout("operation-detail-light", dark = false) {
        val vm = operationsViewModel(operations = listOf(operationFixture("op-1")))
        vm.onAction(OperationAction.Open("op-1"))
        OperationDetailScreen(state = vm.state.value, viewModel = vm)
    }

    @Test fun operation_detail_dark() = verifyLayout("operation-detail-dark", dark = true) {
        val vm = operationsViewModel(operations = listOf(operationFixture("op-1")))
        vm.onAction(OperationAction.Open("op-1"))
        OperationDetailScreen(state = vm.state.value, viewModel = vm)
    }

    @Test fun operation_editor_light() = verifyLayout("operation-editor-light", dark = false) {
        val vm = operationsViewModel(operations = listOf(operationFixture("op-1")))
        OperationEditorScreen(state = vm.state.value, viewModel = vm, existing = null)
    }

    @Test fun operation_editor_dark() = verifyLayout("operation-editor-dark", dark = true) {
        val vm = operationsViewModel(operations = listOf(operationFixture("op-1")))
        OperationEditorScreen(state = vm.state.value, viewModel = vm, existing = null)
    }

    @Test fun unassociated_light() = verifyLayout("unassociated-light", dark = false) {
        val vm = operationsViewModel(
            receipts = listOf(unassociatedReceiptFixture("r-1"), unassociatedReceiptFixture("r-2")),
        )
        vm.onAction(OperationAction.OpenUnassociated)
        OperationListScreen(
            state = vm.state.value,
            viewModel = vm,
            navChrome = rootChrome(appRouteNameOf(AppRoute.Operations)),
        )
    }

    @Test fun unassociated_dark() = verifyLayout("unassociated-dark", dark = true) {
        val vm = operationsViewModel(
            receipts = listOf(unassociatedReceiptFixture("r-1"), unassociatedReceiptFixture("r-2")),
        )
        vm.onAction(OperationAction.OpenUnassociated)
        OperationListScreen(
            state = vm.state.value,
            viewModel = vm,
            navChrome = rootChrome(appRouteNameOf(AppRoute.Operations)),
        )
    }

    @Test fun unassociated_empty_light() = verifyLayout("unassociated-empty-light", dark = false) {
        val vm = operationsViewModel()
        vm.onAction(OperationAction.OpenUnassociated)
        OperationListScreen(
            state = vm.state.value,
            viewModel = vm,
            navChrome = rootChrome(appRouteNameOf(AppRoute.Operations)),
        )
    }

    @Test fun unassociated_empty_dark() = verifyLayout("unassociated-empty-dark", dark = true) {
        val vm = operationsViewModel()
        vm.onAction(OperationAction.OpenUnassociated)
        OperationListScreen(
            state = vm.state.value,
            viewModel = vm,
            navChrome = rootChrome(appRouteNameOf(AppRoute.Operations)),
        )
    }

    // S03 — Cámara: el overlay de captura es una ventana de diálogo propia
    // a pantalla completa (igual que S08); se verifica su contenido por
    // semántica y la captura estricta toma esa ventana al tamaño real del
    // viewport. El preview real requiere hardware: el render JVM muestra
    // la composición del marco tal como se compone (evidencia honesta del
    // entorno, sin hardware simulado).
    @Test fun camera_capture_light() {
        compose.setContent {
            AqTheme(darkTheme = false) {
                CameraQrCaptureOverlay(onQr = {}, onDismiss = {})
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.EncuadreElCodigoQr).assertIsDisplayed()
        captureStrict("camera-capture", dark = false)
    }

    @Test fun camera_capture_dark() {
        compose.setContent {
            AqTheme(darkTheme = true) {
                CameraQrCaptureOverlay(onQr = {}, onDismiss = {})
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.EncuadreElCodigoQr).assertIsDisplayed()
        captureStrict("camera-capture", dark = true)
    }

}
