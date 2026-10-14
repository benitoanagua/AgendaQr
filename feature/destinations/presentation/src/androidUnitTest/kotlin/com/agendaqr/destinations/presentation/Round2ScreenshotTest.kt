package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.agendaqr.core.ui.components.AmIconBadge
import com.agendaqr.core.ui.components.AmListRow
import com.agendaqr.core.ui.components.AmSegmentedControl
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTonalTag
import com.agendaqr.core.ui.components.content
import com.agendaqr.core.ui.theme.AmTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Ronda 2 (Área D, ADR-0007): capturas de referencia de las pantallas
 * críticas en claro y oscuro. Los PNG se generan en
 * `feature/destinations/presentation/build/roborazzi/` y NO se versionan
 * (decisión del 2026-10-09: sin binarios de imagen en git): la regresión
 * se detecta comparando el SHA-256 de cada PNG contra
 * `docs/04-ux/visual-hashes/manifest.json` (verificar con
 * `bash docs/04-ux/visual-hashes/verify.sh`, tarea sensible al entorno —
 * ver ADR).
 */
@RunWith(RobolectricTestRunner::class)
// NATIVE graphics: requisito de Roborazzi para render real de píxeles
// (sin esto las capturas salen en negro/idénticas — hallado en esta ronda).
@org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h740dp")
class Round2ScreenshotTest {

    @get:Rule
    val compose = createComposeRule()

    private fun shot(name: String, dark: Boolean, content: @androidx.compose.runtime.Composable () -> Unit) {
        compose.setContent { AmTheme(darkTheme = dark) { content() } }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/roborazzi/$name.png")
    }

    @Test fun s01_light() = shot("s01-light", dark = false) {
        DestinationsScreen(
            state = DestinationsUiState(
                destinations = listOf(
                    com.agendaqr.destinations.domain.Destination(
                        id = "d-1", name = "Carniceria Don Bife",
                        qr = com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="),
                        createdAt = 1, updatedAt = 1,
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
                        qr = com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="),
                        createdAt = 1, updatedAt = 1,
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
                createdAt = 1, updatedAt = 1,
            ),
            com.agendaqr.destinations.domain.Context(
                id = "ctx-2", name = "Ferretería", createdAt = 1, updatedAt = 1,
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
                com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="),
            ),
            onSaveAll = {},
            onBack = {},
        )
    }

    @Test fun s09_review_dark() = shot("s09-review-dark", dark = true) {
        ImportReviewScreen(
            assets = listOf(
                com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="),
            ),
            onSaveAll = {},
            onBack = {},
        )
    }

    private fun editorFixture() = com.agendaqr.destinations.domain.Destination(
        id = "d-1", name = "Carniceria Don Bife",
        qr = com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="),
        createdAt = 1, updatedAt = 1,
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
        qr = com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="),
        createdAt = 1, updatedAt = 1,
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
        val pago = com.agendaqr.destinations.domain.Operation(
            "op-1", com.agendaqr.destinations.domain.OperationType.PAGO,
            occurredAt = 1760000000000, createdAt = 1760000000000,
            amount = "120", currency = "USD", personOrEntity = "Carnicería Don Bife",
        )
        val cobro = com.agendaqr.destinations.domain.Operation(
            "op-2", com.agendaqr.destinations.domain.OperationType.COBRO,
            occurredAt = 1760000000000, createdAt = 1760000000000,
            amount = "45", currency = "USD", personOrEntity = "Verdulería Mercedes",
        )
        androidx.compose.foundation.layout.Column {
            AmSegmentedControl(
                options = listOf(
                    com.agendaqr.core.ui.components.AmSegmentedOption(AppStrings.Todos),
                    com.agendaqr.core.ui.components.AmSegmentedOption(
                        label = AppStrings.Pago,
                        icon = com.agendaqr.core.ui.components.AmIcons.Expense,
                    ),
                    com.agendaqr.core.ui.components.AmSegmentedOption(
                        label = AppStrings.Cobro,
                        icon = com.agendaqr.core.ui.components.AmIcons.Income,
                    ),
                ),
                selectedIndex = 0,
                onSelect = {},
            )
            com.agendaqr.core.ui.components.AmListBody {
                listOf(pago, cobro).forEachIndexed { index, operation ->
                    AmListRow(
                        title = operationTitle(operation),
                        subtitle = listOfNotNull(
                            operationAmountText(operation),
                            formatDate(operation.occurredAt),
                        ).joinToString(" · "),
                        onClick = {},
                        leading = {
                            AmIconBadge(
                                icon = operationTypeIcon(operation.type),
                                tone = operationTypeTone(operation.type),
                            )
                        },
                        meta = {
                            AmTonalTag(
                                text = operationTypeLabel(operation.type),
                                tone = operationTypeTone(operation.type),
                                uppercase = true,
                            )
                        },
                        showDivider = index < 1,
                    )
                }
            }
        }
    }

    @Test fun s07_operation_row_dark() = shot("s07-operation-row-dark", dark = true) {
        val pago = com.agendaqr.destinations.domain.Operation(
            "op-1", com.agendaqr.destinations.domain.OperationType.PAGO,
            occurredAt = 1760000000000, createdAt = 1760000000000,
            amount = "120", currency = "USD", personOrEntity = "Carnicería Don Bife",
        )
        val cobro = com.agendaqr.destinations.domain.Operation(
            "op-2", com.agendaqr.destinations.domain.OperationType.COBRO,
            occurredAt = 1760000000000, createdAt = 1760000000000,
            amount = "45", currency = "USD", personOrEntity = "Verdulería Mercedes",
        )
        androidx.compose.foundation.layout.Column {
            AmSegmentedControl(
                options = listOf(
                    com.agendaqr.core.ui.components.AmSegmentedOption(AppStrings.Todos),
                    com.agendaqr.core.ui.components.AmSegmentedOption(
                        label = AppStrings.Pago,
                        icon = com.agendaqr.core.ui.components.AmIcons.Expense,
                    ),
                    com.agendaqr.core.ui.components.AmSegmentedOption(
                        label = AppStrings.Cobro,
                        icon = com.agendaqr.core.ui.components.AmIcons.Income,
                    ),
                ),
                selectedIndex = 2,
                onSelect = {},
            )
            com.agendaqr.core.ui.components.AmListBody {
                listOf(pago, cobro).forEachIndexed { index, operation ->
                    AmListRow(
                        title = operationTitle(operation),
                        subtitle = listOfNotNull(
                            operationAmountText(operation),
                            formatDate(operation.occurredAt),
                        ).joinToString(" · "),
                        onClick = {},
                        leading = {
                            AmIconBadge(
                                icon = operationTypeIcon(operation.type),
                                tone = operationTypeTone(operation.type),
                            )
                        },
                        meta = {
                            AmTonalTag(
                                text = operationTypeLabel(operation.type),
                                tone = operationTypeTone(operation.type),
                                uppercase = true,
                            )
                        },
                        showDivider = index < 1,
                    )
                }
            }
        }
    }

    private fun rootChrome(selected: String) =
        com.agendaqr.core.ui.components.AmNavChromeState(
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
