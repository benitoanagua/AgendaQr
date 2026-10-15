package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Contrato de NO solapamiento del scaffold (mandato de corrección §1):
 * ninguna acción inferior puede tapar el contenido.
 *
 * El espacio se DERIVA de los componentes reales (el `Scaffold` reserva la
 * altura real de su `bottomBar` en `innerPadding` y el FAB flotante recibe
 * su holgura por token), así que estas pruebas fallarían si alguien
 * reintrodujera una altura estimada a mano: el contenido dejaría de caber
 * o la última fila quedaría bajo el FAB.
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h740dp")
class AmScaffoldOverlapContractTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun bottom_action_dock_never_covers_content() {
        compose.setContent {
            AmTheme {
                AmScreenScaffold(
                    topBar = { AmTopBar(title = "Detalle") },
                    bottomAction = {
                        Column(
                            modifier = Modifier.fillMaxWidth().testTag(DockTag),
                            verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
                        ) {
                            AmPrimaryButton("Registrar", {}, Modifier.fillMaxWidth())
                            AmSecondaryButton("Compartir", {}, Modifier.fillMaxWidth())
                            AmTextAction("Editar", {})
                        }
                    },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(AmColor.SurfaceContainerLow)
                            .testTag(ContentTag),
                    )
                }
            }
        }
        compose.waitForIdle()
        val dockTop = compose.onNodeWithTag(DockTag).getUnclippedBoundsInRoot().top
        val contentBottom = compose.onNodeWithTag(ContentTag, useUnmergedTree = true)
            .getUnclippedBoundsInRoot().bottom
        assertTrue(
            "El contenido ($contentBottom) queda bajo el dock de acciones ($dockTop)",
            contentBottom.value <= dockTop.value + 0.5f,
        )
        compose.onNodeWithTag(DockTag).assertIsDisplayed()
    }

    @Test
    fun fab_reserves_its_real_height_so_it_never_covers_content() {
        compose.setContent {
            AmTheme {
                AmScreenScaffold(
                    topBar = { AmTopBar(title = "Actividades") },
                    floatingActionButton = {
                        AmExtendedFab(
                            label = "Registrar",
                            icon = AmIcons.Register,
                            onClick = {},
                            modifier = Modifier.testTag(FabTag),
                        )
                    },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(AmColor.SurfaceContainerLow)
                            .testTag(ContentTag),
                    )
                }
            }
        }
        compose.waitForIdle()
        assertContentEndsAboveFab()
    }

    @Test
    fun fab_and_nav_bar_reserve_their_real_space_together() {
        compose.setContent {
            AmTheme {
                AmScreenScaffold(
                    topBar = { AmTopBar(title = "Panel") },
                    navDestinations = listOf(
                        AmNavDestination("home", "Inicio", AmIcons.Home),
                        AmNavDestination("register", "Registrar", AmIcons.Register),
                    ),
                    navSelectedId = "home",
                    floatingActionButton = {
                        AmExtendedFab(
                            label = "Añadir",
                            icon = AmIcons.Register,
                            onClick = {},
                            modifier = Modifier.testTag(FabTag),
                        )
                    },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(AmColor.SurfaceContainerLow)
                            .testTag(ContentTag),
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Inicio").assertIsDisplayed()
        assertContentEndsAboveFab()
    }

    private fun assertContentEndsAboveFab() {
        val fabTop = compose.onNodeWithTag(FabTag).getUnclippedBoundsInRoot().top
        val contentBottom = compose.onNodeWithTag(ContentTag, useUnmergedTree = true)
            .getUnclippedBoundsInRoot().bottom
        assertTrue(
            "El contenido ($contentBottom) queda bajo el FAB ($fabTop): el scaffold no reserva la holgura real del FAB",
            contentBottom.value <= fabTop.value + 0.5f,
        )
    }

    private companion object {
        const val ContentTag = "content-viewport"
        const val DockTag = "bottom-action-dock"
        const val FabTag = "fab"
    }
}
