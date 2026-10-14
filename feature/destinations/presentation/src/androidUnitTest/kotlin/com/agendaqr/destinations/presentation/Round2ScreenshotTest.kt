package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.agendaqr.core.ui.components.AmIconBadge
import com.agendaqr.core.ui.components.AmListRow
import com.agendaqr.core.ui.components.AmSegmentedControl
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTonalTag
import com.agendaqr.core.ui.components.AmTappableCard
import com.agendaqr.core.ui.components.AmFilterChip
import com.agendaqr.core.ui.components.content
import com.agendaqr.core.ui.theme.AmTheme
import androidx.compose.ui.Modifier
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

    companion object {
        // Timestamp realista para pruebas visuales: 15 ene 2024 10:30:00 (milisegundos)
        const val TEST_TIMESTAMP = 1705314600000L
        // Base64 válido de un PNG 1x1 transparente (QR placeholder decodificable)
        const val TEST_QR_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
    }

    @Test fun s01_light() = shot("s01-light", dark = false) {
        DestinationsScreen(
            state = DestinationsUiState(
                destinations = listOf(
                    com.agendaqr.destinations.domain.Destination(
                        id = "d-1", name = "Carniceria Don Bife",
                        qr = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                        createdAt = Round2ScreenshotTest.TEST_TIMESTAMP, updatedAt = Round2ScreenshotTest.TEST_TIMESTAMP,
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
                        createdAt = Round2ScreenshotTest.TEST_TIMESTAMP, updatedAt = Round2ScreenshotTest.TEST_TIMESTAMP,
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
        operationHistoryPattern(selected = 0)
    }

    @Test fun s07_operation_row_dark() = shot("s07-operation-row-dark", dark = true) {
        operationHistoryPattern(selected = 2)
    }

    // Patrón del historial (referencia MCP): pestañas cápsula con conteo y
    // TARJETA por operación con tile direccional, título, importe
    // coloreado con signo, metadato temporal y tags reales. El
    // OperationsScreen completo requiere OperationsViewModel (precedente
    // S10: no en Robolectric); se captura el patrón con los mismos
    // helpers internos de la pantalla.
    @androidx.compose.runtime.Composable
    private fun operationHistoryPattern(selected: Int) {
        val pago = com.agendaqr.destinations.domain.Operation(
            "op-1", com.agendaqr.destinations.domain.OperationType.PAGO,
            occurredAt = 1760000000000, createdAt = 1760000040000,
            amount = "4850", currency = "ARS", personOrEntity = "Cafetería Central",
            concept = "Desayuno de trabajo",
        )
        val cobro = com.agendaqr.destinations.domain.Operation(
            "op-2", com.agendaqr.destinations.domain.OperationType.COBRO,
            occurredAt = 1760000000000, createdAt = 1760000040000,
            amount = "25000", currency = "ARS", personOrEntity = "Verdulería Mercedes",
        )
        androidx.compose.foundation.layout.Column {
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                    com.agendaqr.core.ui.theme.AmSpacing.Sm,
                ),
            ) {
                AmFilterChip(label = AppStrings.Todas, selected = selected == 0, onClick = {}, count = 2)
                AmFilterChip(label = AppStrings.Pago, selected = selected == 1, onClick = {}, count = 1)
                AmFilterChip(label = AppStrings.Cobro, selected = selected == 2, onClick = {}, count = 1)
            }
            com.agendaqr.core.ui.components.AmTappableCard(onClick = {}) {
                androidx.compose.foundation.layout.Column(
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        com.agendaqr.core.ui.theme.AmSpacing.Sm,
                    ),
                ) {
                    listOf(pago, cobro).forEach { operation ->
                        androidx.compose.foundation.layout.Column(
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                                com.agendaqr.core.ui.theme.AmSpacing.Xs,
                            ),
                        ) {
                            androidx.compose.foundation.layout.Row(
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                                    com.agendaqr.core.ui.theme.AmSpacing.Md,
                                ),
                            ) {
                                AmIconBadge(
                                    icon = operationTypeIcon(operation.type),
                                    tone = operationTypeTone(operation.type),
                                )
                                androidx.compose.foundation.layout.Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    AmText(operationTitle(operation))
                                    operation.concept?.let { AmText(it, color = com.agendaqr.core.ui.theme.AmColor.TextSecondary) }
                                }
                                operationAmountText(operation)?.let {
                                    AmText(
                                        it,
                                        color = if (operation.type == com.agendaqr.destinations.domain.OperationType.COBRO) {
                                            com.agendaqr.core.ui.theme.AmColor.Success
                                        } else {
                                            com.agendaqr.core.ui.theme.AmColor.Danger
                                        },
                                    )
                                }
                            }
                            AmText(
                                formatDate(operation.occurredAt) + " · " + AppStrings.RegPrefijo + formatTime(operation.createdAt),
                                color = com.agendaqr.core.ui.theme.AmColor.TextTertiary,
                            )
                        }
                    }
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
