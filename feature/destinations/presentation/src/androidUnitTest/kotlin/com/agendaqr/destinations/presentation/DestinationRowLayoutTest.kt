package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.XauxaTheme
import com.agendaqr.destinations.data.PendingSyncMutation
import com.agendaqr.destinations.data.SyncMutationState
import com.agendaqr.destinations.data.SyncMutationType
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.QrAsset
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Defectos abiertos resueltos (docs/09-implementacion/01-estado.md):
 * 1. El badge de estado ("Sincronizado"/"Pendiente") NO compite por el
 *    ancho de la fila: vive en su línea propia bajo el título (slot meta
 *    del ListRow), no junto a la acción de trailing. Se cubre a 320 y
 *    360 dp con font scale 100/150/200 %.
 * 2. Los destinos con nombre genérico ("Sin nombre"/"QR importado")
 *    muestran un subtítulo distintivo con ORIGEN y FECHA.
 *
 * La escala de fuente se mueve por estado dentro de una única
 * composición: `setContent` solo puede llamarse una vez por test.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w320dp-h640dp")
class DestinationRowLayoutTest {

    @get:Rule
    val compose = createComposeRule()

    private val destination = Destination(
        id = "d-1",
        name = "Carniceria Don Bife Sin Nombre Largo",
        qr = QrAsset(encoded = "eA=="),
        createdAt = 1,
        updatedAt = 1,
    )

    private fun pendingLookup() = ElementSyncLookup(
        listOf(
            PendingSyncMutation(
                id = "m-1",
                resource = SyncResource.DESTINATION,
                mutation = SyncMutationType.UPSERT,
                entityId = "d-1",
                enqueuedAt = 1,
                state = SyncMutationState.PENDING,
            ),
        ),
    )

    private fun composeRowAtScales(scales: List<Float>, check: (Float) -> Unit) {
        var fontScale by mutableStateOf(scales.first())
        compose.setContent {
            XauxaTheme {
                CompositionLocalProvider(
                    LocalDensity provides Density(density = 1f, fontScale = fontScale),
                ) {
                    DestinationRow(
                        destination = destination,
                        onAction = {},
                        syncLookup = pendingLookup(),
                    )
                }
            }
        }
        scales.forEach { value ->
            compose.runOnIdle { fontScale = value }
            compose.waitForIdle()
            check(value)
        }
    }

    private fun assertBadgeOnOwnLineWithoutOverflow(scale: Float) {
        val badgeNode = compose.onNodeWithText("Pendiente", useUnmergedTree = true)
        badgeNode.assertExists()
        val titleBottom = compose.onNodeWithText(
            "Carniceria Don Bife Sin Nombre Largo",
            useUnmergedTree = true,
        ).getBoundsInRoot().bottom
        // El badge cuelga de la fila en línea propia (bajo el título),
        // no en un lado del row compitiendo por ancho.
        assertTrue(
            "el badge no está bajo el título a escala $scale",
            badgeNode.getBoundsInRoot().top >= titleBottom - 1.dp,
        )
        // Nada desborda el ancho: la acción sigue viva en el trailing y
        // el badge en su línea propia (por debajo del título).
        val screenRight = compose.onRoot().getBoundsInRoot().right.value
        compose.onAllNodesWithText("Pendiente", substring = true).fetchSemanticsNodes().forEach { node ->
            assertTrue(
                "el badge desborda el ancho a escala $scale",
                node.boundsInRoot.right <= screenRight + 1f,
            )
        }
        compose.onAllNodesWithText("Marcar favorito", substring = true)
            .fetchSemanticsNodes().forEach { node ->
                assertTrue(
                    "la acción desborda el ancho a escala $scale",
                    node.boundsInRoot.right <= screenRight + 1f,
                )
            }
    }

    @Test
    fun badge_sits_on_its_own_line_at_100_150_and_200_percent() {
        composeRowAtScales(listOf(1f, 1.5f, 2f), ::assertBadgeOnOwnLineWithoutOverflow)
    }

    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h800dp")
    fun badge_sits_on_its_own_line_at_200_percent_on_360dp() {
        composeRowAtScales(listOf(2f), ::assertBadgeOnOwnLineWithoutOverflow)
    }

    @Test
    fun unnamed_and_imported_destinations_get_distinctive_origin_subtitles() {
        compose.setContent {
            XauxaTheme {
                Column {
                    DestinationRow(
                        destination = destination.copy(id = "d-a", name = "", createdAt = 1_700_000_000_000),
                        onAction = {},
                    )
                    DestinationRow(
                        destination = destination.copy(id = "d-b", name = "QR importado", createdAt = 1_700_000_000_000),
                        onAction = {},
                    )
                }
            }
        }
        compose.waitForIdle()
        // "Sin nombre" y "QR importado" ya no son indistinguibles: cada
        // fila lleva origen + fecha (texto visible, nunca solo color).
        compose.onNodeWithText("Sin nombre").assertExists()
        compose.onNodeWithText("Añadido el 14/11/2023").assertExists()
        compose.onNodeWithText("QR importado").assertExists()
        compose.onNodeWithText("Importado el 14/11/2023").assertExists()
    }
}
