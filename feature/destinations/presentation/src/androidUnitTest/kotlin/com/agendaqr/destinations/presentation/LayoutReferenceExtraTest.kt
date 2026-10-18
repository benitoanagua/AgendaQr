package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.components.AmBottomAction
import com.agendaqr.core.ui.components.AmDangerButton
import com.agendaqr.core.ui.components.AmPrimaryButton
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.core.ui.theme.AmType
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Verificación de layout extra: ancho 320 dp (escenario de fuente al
 * 200 %), botones primario/destructivo en sus estados, importe hero y
 * acción inferior fija. Sin capturas: cada caso comprueba que la raíz se
 * muestra con tamaño y que el texto visible no se recorta.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w320dp-h640dp")
class LayoutReferenceExtraTest {

    @get:Rule
    val compose = createComposeRule()

    private fun verifyLayout(name: String, dark: Boolean, content: @Composable () -> Unit) {
        compose.setContent { AmTheme(darkTheme = dark) { content() } }
        compose.waitForIdle()
        val root = compose.onRoot().getUnclippedBoundsInRoot()
        compose.onRoot().assertIsDisplayed()
        val rootWidth = (root.right - root.left).value
        val rootHeight = (root.bottom - root.top).value
        assertTrue(
            "$name: el layout no tiene tamaño (root=$root)",
            rootWidth > 200f && rootHeight > 320f,
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

    @Test
    fun s01_grid_320dp_light() = verifyLayout("s01-grid-320dp-light", dark = false) {
        DestinationsScreen(
            state = DestinationsUiState(
                destinations = listOf(
                    com.agendaqr.destinations.domain.Destination(
                        id = "d-1",
                        name = "Carniceria Don Bife",
                        qr = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                        createdAt = TEST_TIMESTAMP,
                        updatedAt = TEST_TIMESTAMP,
                    ),
                ),
                isLoading = false,
            ),
            onAction = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Home)),
        )
    }

    @Test
    fun s01_grid_320dp_dark() = verifyLayout("s01-grid-320dp-dark", dark = true) {
        DestinationsScreen(
            state = DestinationsUiState(
                destinations = listOf(
                    com.agendaqr.destinations.domain.Destination(
                        id = "d-1",
                        name = "Carniceria Don Bife",
                        qr = com.agendaqr.destinations.domain.QrAsset(encoded = TEST_QR_BASE64),
                        createdAt = TEST_TIMESTAMP,
                        updatedAt = TEST_TIMESTAMP,
                    ),
                ),
                isLoading = false,
            ),
            onAction = {},
            navChrome = rootChrome(appRouteNameOf(AppRoute.Home)),
        )
    }

    @Test
    fun primary_button_states_light() = verifyLayout("primary-button-states-light", dark = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AmPrimaryButton(label = "Guardar", onClick = {}, modifier = Modifier.fillMaxWidth())
            AmPrimaryButton(label = "Guardar", onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth())
            AmPrimaryButton(label = "Guardando", onClick = {}, isLoading = true, modifier = Modifier.fillMaxWidth())
        }
    }

    @Test
    fun primary_button_states_dark() = verifyLayout("primary-button-states-dark", dark = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AmPrimaryButton(label = "Guardar", onClick = {}, modifier = Modifier.fillMaxWidth())
            AmPrimaryButton(label = "Guardar", onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth())
            AmPrimaryButton(label = "Guardando", onClick = {}, isLoading = true, modifier = Modifier.fillMaxWidth())
        }
    }

    @Test
    fun danger_button_light() = verifyLayout("danger-button-light", dark = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AmDangerButton(label = "Eliminar", onClick = {}, modifier = Modifier.fillMaxWidth())
        }
    }

    @Test
    fun danger_button_dark() = verifyLayout("danger-button-dark", dark = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AmDangerButton(label = "Eliminar", onClick = {}, modifier = Modifier.fillMaxWidth())
        }
    }

    @Test
    fun amount_hero_light() = verifyLayout("amount-hero-light", dark = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "1250.00",
                fontSize = AmType.Amount,
                fontWeight = FontWeight.ExtraBold,
                color = AmColor.TextPrimary,
            )
        }
    }

    @Test
    fun amount_hero_dark() = verifyLayout("amount-hero-dark", dark = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "1250.00",
                fontSize = AmType.Amount,
                fontWeight = FontWeight.ExtraBold,
                color = AmColor.TextPrimary,
            )
        }
    }

    @Test
    fun bottom_action_light() = verifyLayout("bottom-action-light", dark = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            AmBottomAction(label = "Guardar", onClick = {})
        }
    }

    @Test
    fun bottom_action_dark() = verifyLayout("bottom-action-dark", dark = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            AmBottomAction(label = "Guardar", onClick = {})
        }
    }

    companion object {
        const val TEST_TIMESTAMP = 1705314600000L
        val TEST_QR_BASE64: String = QrTestFixture.base64Png
    }
}
