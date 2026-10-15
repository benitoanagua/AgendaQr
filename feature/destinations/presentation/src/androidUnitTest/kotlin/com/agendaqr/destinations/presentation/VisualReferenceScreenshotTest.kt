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
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import com.agendaqr.core.ui.components.AmIcon
import com.agendaqr.core.ui.components.AmIconBadge
import com.agendaqr.core.ui.components.AmListRow
import com.agendaqr.core.ui.components.AmSegmentedControl
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTonalTag
import com.agendaqr.core.ui.components.AmTappableCard
import com.agendaqr.core.ui.components.AmFilterChip
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.components.AmExtendedFab
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.components.AmSearchField
import com.agendaqr.core.ui.components.AmListBody
import com.agendaqr.core.ui.components.AmStatusPill
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmEmptyState
import com.agendaqr.core.ui.components.AmLoadMoreFooter
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmCompactAction
import com.agendaqr.core.ui.components.AmHairline
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.components.content
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationType
import com.agendaqr.destinations.domain.BiometricAvailability
import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind
import com.agendaqr.destinations.presentation.AppStrings
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Capturas de referencia de las pantallas críticas en claro y oscuro
 * (ADR-0007). Los PNG se generan en
 * `feature/destinations/presentation/build/roborazzi/` y NO se versionan
 * (decisión del 2026-10-09: sin binarios de imagen en git): la regresión
 * se detecta comparando el SHA-256 de cada PNG contra
 * `docs/04-ux/visual-hashes/manifest.json` (verificar con
 * `./gradlew verifyVisualHashes`, tarea sensible al entorno —
 * ver ADR).
 */
@RunWith(RobolectricTestRunner::class)
// NATIVE graphics: requisito de Roborazzi para render real de píxeles
// (sin esto las capturas salen en negro/idénticas).
@org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h740dp")
class VisualReferenceScreenshotTest {

    @get:Rule
    val compose = createComposeRule()

    private fun shot(name: String, dark: Boolean, content: @androidx.compose.runtime.Composable () -> Unit) {
        compose.setContent { AmTheme(darkTheme = dark) { content() } }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/roborazzi/$name.png")
    }

    companion object {
        // Timestamp realista para pruebas visuales: 15 ene 2024 10:30:00 (milisegundos)
        const val TEST_TIMESTAMP = 1705314600000L
        // QR REAL para las capturas: PNG legible generado con ZXing (ver
        // QrTestFixture). Nunca una imagen ficticia (el fixture anterior era
        // un píxel verde 1×1 que se veía como un bloque verde uniforme).
        val TEST_QR_BASE64: String = QrTestFixture.base64Png
    }

    @Test fun s01_light() = shot("s01-light", dark = false) {
        DestinationsScreen(
            state = DestinationsUiState(
                destinations = listOf(
                    com.agendaqr.destinations.domain.Destination(
                        id = "d-1", name = "Carniceria Don Bife",
                        qr = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                        createdAt = VisualReferenceScreenshotTest.TEST_TIMESTAMP, updatedAt = VisualReferenceScreenshotTest.TEST_TIMESTAMP,
                    ),
                ),
                isLoading = false,
            ),
            onAction = {},
            // La captura de referencia incluye el chrome de producción: la
            // barra inferior aparece en las superficies raíz autenticadas.
            navChrome = com.agendaqr.core.ui.components.AmNavChromeState(
                destinations = listOf(
                    com.agendaqr.core.ui.components.AmNavDestination(
                        appRouteNameOf(AppRoute.Home), AppStrings.Inicio,
                        com.agendaqr.core.ui.components.AmIcons.Home,
                    ),
                    com.agendaqr.core.ui.components.AmNavDestination(
                        appRouteNameOf(AppRoute.Search), AppStrings.Buscar,
                        com.agendaqr.core.ui.components.AmIcons.Search,
                    ),
                    com.agendaqr.core.ui.components.AmNavDestination(
                        appRouteNameOf(AppRoute.Operations), AppStrings.Operaciones,
                        com.agendaqr.core.ui.components.AmIcons.Register,
                    ),
                    com.agendaqr.core.ui.components.AmNavDestination(
                        appRouteNameOf(AppRoute.Contexts), AppStrings.Contextos,
                        com.agendaqr.core.ui.components.AmIcons.Context,
                    ),
                ),
                selectedId = appRouteNameOf(AppRoute.Home),
                onSelect = {},
            ),
        )
    }

    @Test fun s01_dark() = shot("s01-dark", dark = true) {
        DestinationsScreen(
            state = DestinationsUiState(
                destinations = listOf(
                    com.agendaqr.destinations.domain.Destination(
                        id = "d-1", name = "Carniceria Don Bife",
                        qr = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                        createdAt = VisualReferenceScreenshotTest.TEST_TIMESTAMP, updatedAt = VisualReferenceScreenshotTest.TEST_TIMESTAMP,
                    ),
                ),
                isLoading = false,
            ),
            onAction = {},
            navChrome = com.agendaqr.core.ui.components.AmNavChromeState(
                destinations = listOf(
                    com.agendaqr.core.ui.components.AmNavDestination(
                        appRouteNameOf(AppRoute.Home), AppStrings.Inicio,
                        com.agendaqr.core.ui.components.AmIcons.Home,
                    ),
                    com.agendaqr.core.ui.components.AmNavDestination(
                        appRouteNameOf(AppRoute.Search), AppStrings.Buscar,
                        com.agendaqr.core.ui.components.AmIcons.Search,
                    ),
                    com.agendaqr.core.ui.components.AmNavDestination(
                        appRouteNameOf(AppRoute.Operations), AppStrings.Operaciones,
                        com.agendaqr.core.ui.components.AmIcons.Register,
                    ),
                    com.agendaqr.core.ui.components.AmNavDestination(
                        appRouteNameOf(AppRoute.Contexts), AppStrings.Contextos,
                        com.agendaqr.core.ui.components.AmIcons.Context,
                    ),
                ),
                selectedId = appRouteNameOf(AppRoute.Home),
                onSelect = {},
            ),
        )
    }

    @Test fun login_light() = shot("login-light", dark = false) {
        AuthScreen(
            state = AuthUiState(email = "", password = "", isSubmitting = false),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignIn = {},
            onSignUp = {},
        )
    }

    @Test fun login_dark() = shot("login-dark", dark = true) {
        AuthScreen(
            state = AuthUiState(email = "", password = "", isSubmitting = false),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignIn = {},
            onSignUp = {},
        )
    }

    @Test fun import_batch_light() = shot("import-batch-light", dark = false) {
        ImportBatchScreen(state = ImportBatchUiState.Idle, onAction = {})
    }

    @Test fun import_batch_dark() = shot("import-batch-dark", dark = true) {
        ImportBatchScreen(state = ImportBatchUiState.Idle, onAction = {})
    }

    @Test fun s02_add_light() = shot("s02-add-light", dark = false) {
        AddDestinationScreen(onImport = {}, onBack = {})
    }

    @Test fun s02_add_dark() = shot("s02-add-dark", dark = true) {
        AddDestinationScreen(onImport = {}, onBack = {})
    }

    // Fallo de decodificación del QR: estado explícito (glifo sobre el
    // marco tonal), nunca un rectángulo blanco sin contexto (§9).
    @Test fun qr_preview_fallback_light() = shot("qr-preview-fallback-light", dark = false) {
        com.agendaqr.core.ui.components.AmQrPreview("!!")
    }

    @Test fun qr_preview_fallback_dark() = shot("qr-preview-fallback-dark", dark = true) {
        com.agendaqr.core.ui.components.AmQrPreview("!!")
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
            ),
            com.agendaqr.destinations.domain.AgendaSearchResult(
                type = com.agendaqr.destinations.domain.AgendaSearchResultType.CONTEXT,
                id = "ctx-1", title = "Mercado", contextId = "ctx-1",
            ),
        ),
    )

    @Test fun s04_search_light() = shot("s04-search-light", dark = false) {
        GlobalSearchScreen(
            state = searchFixture(),
            navChrome = rootChrome(appRouteNameOf(AppRoute.Search)),
            onAction = {},
            onSelect = {},
        )
    }

    @Test fun s04_search_dark() = shot("s04-search-dark", dark = true) {
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

    @Test fun s06_contexts_light() = shot("s06-contexts-light", dark = false) {
        ContextsScreen(
            state = contextsFixture(),
            onAction = {},
            onBack = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Contexts)),
        )
    }

    @Test fun s06_contexts_dark() = shot("s06-contexts-dark", dark = true) {
        ContextsScreen(
            state = contextsFixture(),
            onAction = {},
            onBack = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Contexts)),
        )
    }

    // La ventana del diálogo crea una segunda raíz y la versión de
    // Roborazzi del proyecto solo captura una raíz exacta: el diálogo se
    // verifica por semántica (ContextPickerDialogTest,
    // AmDialogProminenceTest), no por captura.

    @Test fun s09_review_light() = shot("s09-review-light", dark = false) {
        ImportReviewScreen(
            assets = listOf(
                com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
            ),
            onSaveAll = {},
            onBack = {},
        )
    }

    @Test fun s09_review_dark() = shot("s09-review-dark", dark = true) {
        ImportReviewScreen(
            assets = listOf(
                com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
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

    @Test fun edit_destination_light() = shot("edit-destination-light", dark = false) {
        DestinationEditorScreen(
            existing = editorFixture(),
            onSave = {},
            onImportMany = {},
            onBack = {},
        )
    }

    @Test fun edit_destination_dark() = shot("edit-destination-dark", dark = true) {
        DestinationEditorScreen(
            existing = editorFixture(),
            onSave = {},
            onImportMany = {},
            onBack = {},
        )
    }

    // Detalle y exhibición QR (referencia Stitch): hero de 320 dp con
    // metadatos en tarjeta compacta.
    private fun detailFixture() = com.agendaqr.destinations.domain.Destination(
        id = "d-1", name = "Carniceria Don Bife",
        category = "Almacén",
        note = "Compra semanal de la fiambrería.",
        qr = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
        createdAt = TEST_TIMESTAMP, updatedAt = TEST_TIMESTAMP,
    )

    @Test fun destination_detail_light() = shot("destination-detail-light", dark = false) {
        DestinationDetailScreen(
            destination = detailFixture(),
            onShowQr = {},
            onEdit = {},
            onDelete = {},
            onShare = {},
            onBack = {},
        )
    }

    @Test fun destination_detail_dark() = shot("destination-detail-dark", dark = true) {
        DestinationDetailScreen(
            destination = detailFixture(),
            onShowQr = {},
            onEdit = {},
            onDelete = {},
            onShare = {},
            onBack = {},
        )
    }

    // Anatomía de fila de actividad (referencia Stitch): insignia
    // direccional + contraparte + tipo/fecha + monto con signo. El
    // OperationsScreen completo requiere OperationsViewModel (precedente
    // S10: no se compone en Robolectric); aquí se captura el patrón
    // visual con los mismos helpers internos que usa la pantalla.
    @Test fun s07_operation_row_light() = shot("s07-operation-row-light", dark = false) {
        operationListScreenPattern(selected = 0)
    }

    @Test fun s07_operation_row_dark() = shot("s07-operation-row-dark", dark = true) {
        operationListScreenPattern(selected = 2)
    }

    // Patrón visual de la pantalla de operaciones (referencia Stitch):
    // Usa la estructura real de OperationListScreen: AmScreenScaffold con
    // márgenes, búsqueda, filtros, AmListBody con AmListRow.
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

        AmScreenScaffold(
            topBar = { AmTopBar(title = AppStrings.Operaciones) },
            navDestinations = listOf(
                com.agendaqr.core.ui.components.AmNavDestination("home", AppStrings.Inicio, AmIcons.Home),
                com.agendaqr.core.ui.components.AmNavDestination("search", AppStrings.Buscar, AmIcons.Search),
                com.agendaqr.core.ui.components.AmNavDestination("operations", AppStrings.Operaciones, AmIcons.Register),
                com.agendaqr.core.ui.components.AmNavDestination("contexts", AppStrings.Contextos, AmIcons.Context),
            ),
            navSelectedId = "operations",
            onNavSelect = {},
            floatingActionButton = {
                AmExtendedFab(
                    label = AppStrings.RegistrarOperacion,
                    icon = AmIcons.Add,
                    onClick = {},
                )
            },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
                AmSearchField(
                    value = "",
                    onValueChange = {},
                    label = AppStrings.Buscar,
                    placeholder = AppStrings.BuscarOperaciones,
                    onClear = {},
                    clearLabel = AppStrings.Limpiar,
                )
                AmSegmentedControl(
                    options = listOf(
                        com.agendaqr.core.ui.components.AmSegmentedOption(
                            label = AppStrings.Todas,
                            count = operations.size,
                        ),
                        com.agendaqr.core.ui.components.AmSegmentedOption(
                            label = AppStrings.Pago,
                            icon = AmIcons.Expense,
                            count = pagosCount,
                        ),
                        com.agendaqr.core.ui.components.AmSegmentedOption(
                            label = AppStrings.Cobro,
                            icon = AmIcons.Income,
                            activeTone = AmTone.Success,
                            count = cobrosCount,
                        ),
                    ),
                    selectedIndex = selected,
                    onSelect = {},
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (filtered.isNotEmpty()) {
                AmListBody {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AmSpacing.Md, vertical = AmSpacing.Sm),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
                        ) {
                            AmIcon(
                                imageVector = AmIcons.Calendar,
                                contentDescription = null,
                                size = AmMetrics.IconSizeCompact,
                                tint = AmColor.AccentText,
                            )
                            Column {
                                AmText(
                                    monthYearLabel(filtered.first().occurredAt),
                                    size = AmType.BodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                AmText(
                                    filtered.size.toString() + " " + AppStrings.RegistrosLocales,
                                    color = AmColor.TextSecondary,
                                )
                            }
                        }
                    }
                }
            }

            if (filtered.isEmpty()) {
                AmEmptyState(
                    icon = AmIcons.Register,
                    title = AppStrings.AunNoHayOperaciones,
                    subtitle = AppStrings.registerFirstPaymentHint,
                    actionLabel = AppStrings.RegistrarOperacion,
                    onAction = {},
                )
            } else {
                AmListBody(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                ) {
                    Column {
                        filtered.forEachIndexed { index, operation ->
                            val amountText = operationAmountText(operation)
                            val amountColor = if (operation.type == OperationType.COBRO) {
                                AmColor.Success
                            } else {
                                AmColor.Danger
                            }
                            val isLast = index == filtered.lastIndex

                            AmListRow(
                                modifier = Modifier.fillMaxWidth(),
                                title = operationTitle(operation),
                                subtitle = operation.concept?.takeIf { it.isNotBlank() },
                                leading = {
                                    AmIconBadge(
                                        icon = operationTypeIcon(operation.type),
                                        tone = operationTypeTone(operation.type),
                                    )
                                },
                                trailing = {
                                    amountText?.let {
                                        AmText(
                                            it,
                                            size = AmType.SectionHeader,
                                            fontWeight = FontWeight.SemiBold,
                                            color = amountColor,
                                        )
                                    }
                                },
                                meta = {
                                    Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
                                        ) {
                                            AmIcon(
                                                imageVector = AmIcons.Clock,
                                                contentDescription = null,
                                                size = AmMetrics.IconSizeCompact,
                                                tint = AmColor.TextTertiary,
                                            )
                                            AmText(
                                                formatDate(operation.occurredAt) + " · " + AppStrings.RegPrefijo + formatTime(operation.createdAt),
                                                color = AmColor.TextTertiary,
                                                size = AmType.Caption,
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
                    AmLoadMoreFooter(
                        modifier = Modifier.fillMaxWidth().padding(top = AmSpacing.Sm),
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
    @Test fun local_onboarding_light() = shot("local-onboarding-light", dark = false) {
        LocalOnboardingScreen(
            state = LocalAccessUiState(mode = LocalAccessMode.Onboarding),
            onPinChanged = {},
            onConfirmationChanged = {},
            onSubmit = {},
            onExploreDemo = {},
        )
    }

    @Test fun local_onboarding_dark() = shot("local-onboarding-dark", dark = true) {
        LocalOnboardingScreen(
            state = LocalAccessUiState(mode = LocalAccessMode.Onboarding),
            onPinChanged = {},
            onConfirmationChanged = {},
            onSubmit = {},
            onExploreDemo = {},
        )
    }

    @Test fun local_onboarding_mismatch_light() = shot("local-onboarding-mismatch-light", dark = false) {
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

    @Test fun local_onboarding_mismatch_dark() = shot("local-onboarding-mismatch-dark", dark = true) {
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

    @Test fun local_lock_light() = shot("local-lock-light", dark = false) {
        LocalLockScreen(
            state = LocalAccessUiState(mode = LocalAccessMode.Locked),
            onPinChanged = {},
            onSubmit = {},
            onExploreDemo = {},
        )
    }

    @Test fun local_lock_dark() = shot("local-lock-dark", dark = true) {
        LocalLockScreen(
            state = LocalAccessUiState(mode = LocalAccessMode.Locked),
            onPinChanged = {},
            onSubmit = {},
            onExploreDemo = {},
        )
    }

    @Test fun local_lock_incorrect_light() = shot("local-lock-incorrect-light", dark = false) {
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

    @Test fun local_lock_incorrect_dark() = shot("local-lock-incorrect-dark", dark = true) {
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
    @Test fun local_lock_biometric_light() = shot("local-lock-biometric-light", dark = false) {
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

    @Test fun local_lock_biometric_dark() = shot("local-lock-biometric-dark", dark = true) {
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
    @Test fun s01_empty_light() = shot("s01-empty-light", dark = false) {
        DestinationsScreen(
            state = DestinationsUiState(destinations = emptyList(), isLoading = false),
            onAction = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Home)),
        )
    }

    @Test fun s01_empty_dark() = shot("s01-empty-dark", dark = true) {
        DestinationsScreen(
            state = DestinationsUiState(destinations = emptyList(), isLoading = false),
            onAction = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Home)),
        )
    }

    @Test fun s01_error_light() = shot("s01-error-light", dark = false) {
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

    @Test fun s01_error_dark() = shot("s01-error-dark", dark = true) {
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
    @Test fun s04_search_empty_light() = shot("s04-search-empty-light", dark = false) {
        GlobalSearchScreen(
            state = GlobalSearchUiState(query = "inexistente"),
            navChrome = rootChrome(appRouteNameOf(AppRoute.Search)),
            onAction = {},
            onSelect = {},
        )
    }

    @Test fun s04_search_empty_dark() = shot("s04-search-empty-dark", dark = true) {
        GlobalSearchScreen(
            state = GlobalSearchUiState(query = "inexistente"),
            navChrome = rootChrome(appRouteNameOf(AppRoute.Search)),
            onAction = {},
            onSelect = {},
        )
    }

    @Test fun s04_search_error_light() = shot("s04-search-error-light", dark = false) {
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

    @Test fun s04_search_error_dark() = shot("s04-search-error-dark", dark = true) {
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

    @Test fun import_batch_result_light() = shot("import-batch-result-light", dark = false) {
        ImportBatchScreen(state = ImportBatchUiState.Result(importBatchFixture()), onAction = {})
    }

    @Test fun import_batch_result_dark() = shot("import-batch-result-dark", dark = true) {
        ImportBatchScreen(state = ImportBatchUiState.Result(importBatchFixture()), onAction = {})
    }

    @Test fun import_batch_review_light() = shot("import-batch-review-light", dark = false) {
        ImportBatchScreen(state = ImportBatchUiState.Review(importBatchFixture()), onAction = {})
    }

    @Test fun import_batch_review_dark() = shot("import-batch-review-dark", dark = true) {
        ImportBatchScreen(state = ImportBatchUiState.Review(importBatchFixture()), onAction = {})
    }

    @Test fun import_batch_error_light() = shot("import-batch-error-light", dark = false) {
        ImportBatchScreen(
            state = ImportBatchUiState.Error(
                error = userFacingError(IllegalStateException("batch save failed"), ErrorFlow.ImportBatchSave),
                batch = importBatchFixture(),
            ),
            onAction = {},
        )
    }

    @Test fun import_batch_error_dark() = shot("import-batch-error-dark", dark = true) {
        ImportBatchScreen(
            state = ImportBatchUiState.Error(
                error = userFacingError(IllegalStateException("batch save failed"), ErrorFlow.ImportBatchSave),
                batch = importBatchFixture(),
            ),
            onAction = {},
        )
    }

    // S06 vacío: sin contextos, con guía de creación.
    @Test fun s06_contexts_empty_light() = shot("s06-contexts-empty-light", dark = false) {
        ContextsScreen(
            state = ContextsUiState(contexts = emptyList(), isLoading = false),
            onAction = {},
            onBack = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Contexts)),
        )
    }

    @Test fun s06_contexts_empty_dark() = shot("s06-contexts-empty-dark", dark = true) {
        ContextsScreen(
            state = ContextsUiState(contexts = emptyList(), isLoading = false),
            onAction = {},
            onBack = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Contexts)),
        )
    }

    // Cuenta y respaldo: estado ocupado del formulario (la superficie
    // opcional que conserva el login remoto fuera del arranque).
    @Test fun login_submitting_light() = shot("login-submitting-light", dark = false) {
        AuthScreen(
            state = AuthUiState(email = "persona@example.com", password = "secreta123", isSubmitting = true),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignIn = {},
            onSignUp = {},
        )
    }

    @Test fun login_submitting_dark() = shot("login-submitting-dark", dark = true) {
        AuthScreen(
            state = AuthUiState(email = "persona@example.com", password = "secreta123", isSubmitting = true),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignIn = {},
            onSignUp = {},
        )
    }

    // S09 con varios QR: conteo plural y guardado múltiple.
    @Test fun s09_review_multi_light() = shot("s09-review-multi-light", dark = false) {
        ImportReviewScreen(
            assets = listOf(
                com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
            ),
            onSaveAll = {},
            onBack = {},
        )
    }

    @Test fun s09_review_multi_dark() = shot("s09-review-multi-dark", dark = true) {
        ImportReviewScreen(
            assets = listOf(
                com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
            ),
            onSaveAll = {},
            onBack = {},
        )
    }

    private fun rootChrome(selected: String): com.agendaqr.core.ui.components.AmNavChromeState {
        return com.agendaqr.core.ui.components.AmNavChromeState(
            destinations = listOf(
                com.agendaqr.core.ui.components.AmNavDestination(
                    appRouteNameOf(AppRoute.Home), AppStrings.Inicio,
                    com.agendaqr.core.ui.components.AmIcons.Home,
                ),
                com.agendaqr.core.ui.components.AmNavDestination(
                    appRouteNameOf(AppRoute.Search), AppStrings.Buscar,
                    com.agendaqr.core.ui.components.AmIcons.Search,
                ),
                com.agendaqr.core.ui.components.AmNavDestination(
                    appRouteNameOf(AppRoute.Operations), AppStrings.Operaciones,
                    com.agendaqr.core.ui.components.AmIcons.Register,
                ),
                com.agendaqr.core.ui.components.AmNavDestination(
                    appRouteNameOf(AppRoute.Contexts), AppStrings.Contextos,
                    com.agendaqr.core.ui.components.AmIcons.Context,
                ),
            ),
            selectedId = selected,
            onSelect = {},
        )
    }
}
