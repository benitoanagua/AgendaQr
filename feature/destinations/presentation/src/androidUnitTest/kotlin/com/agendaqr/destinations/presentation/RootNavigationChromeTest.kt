package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.agendaqr.core.ui.components.AmFeedbackHost
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.components.AmNavDestination
import com.agendaqr.core.ui.components.AmNavBar
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.AmFeedbackEvent
import com.agendaqr.core.ui.theme.AmTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Chrome de navegación raíz (§4 S01 V1.2): 4 destinos con etiqueta+icono;
 * cambiar de destino conserva la raíz del back stack (popToRoot+push) y el
 * Back vuelve a Inicio.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RootNavigationChromeTest {

    @get:Rule
    val compose = createComposeRule()

    private fun destinations() = listOf(
        AmNavDestination(appRouteNameOf(AppRoute.Home), AppStrings.Inicio, AmIcons.Home),
        AmNavDestination(appRouteNameOf(AppRoute.Search), AppStrings.Buscar, AmIcons.Search),
        AmNavDestination(appRouteNameOf(AppRoute.Operations), AppStrings.Operaciones, AmIcons.Register),
        AmNavDestination(appRouteNameOf(AppRoute.Contexts), AppStrings.Contextos, AmIcons.Context),
    )

    @Test
    fun nav_bar_shows_all_four_destinations_with_labels() {
        compose.setContent {
            AmTheme {
                AmNavBar(
                    destinations = destinations(),
                    selectedId = appRouteNameOf(AppRoute.Home),
                    onSelect = {},
                )
            }
        }
        compose.waitForIdle()
        // §12: cada destino con etiqueta visible (nunca solo icono).
        listOf(AppStrings.Inicio, AppStrings.Buscar, AppStrings.Operaciones, AppStrings.Contextos)
            .forEach { label ->
                compose.onNodeWithText(label).assertExists()
            }
    }

    @Test
    fun switching_destination_keeps_home_as_root_and_back_returns_to_it() {
        val nav = AppBackStack()
        compose.setContent {
            AmTheme {
                AmNavBar(
                    destinations = destinations(),
                    selectedId = appRouteNameOf(nav.top),
                    onSelect = { name ->
                        // Contrato V1.2: popToRoot + push — Inicio es raíz.
                        nav.popToRoot()
                        val destination = appRouteForName(name)
                        if (destination != null && destination != AppRoute.Home) nav.push(destination)
                    },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.Contextos).performClick()
        compose.waitForIdle()
        assert(nav.top == AppRoute.Contexts) { "Destino visible tras el cambio: ${nav.top}" }
        assert(nav.stack.value.first() == AppRoute.Home) { "Inicio sigue siendo la raíz del back stack" }
        // Back desde otro destino vuelve a Inicio (pop de superficie).
        nav.pop()
        assert(nav.top == AppRoute.Home) { "El Back desde otro destino vuelve a Inicio" }
        assert(!nav.canPop()) { "Desde Inicio no hay superficie que hacer pop" }
    }

    @Test
    fun transient_feedback_publishes_to_the_snackbar_host() {
        val host = AmFeedbackHost()
        compose.setContent {
            AmTheme {
                AmFeedbackEvent(
                    event = Any(),
                    message = AppStrings.SincronizacionCompletada,
                    feedbackHost = host,
                )
                AmSnackbarHost(host)
            }
        }
        compose.waitForIdle()
        // El evento transitorio vive en el Snackbar del Scaffold; el
        // anuncio accesible lo mantiene el propio AmFeedbackEvent.
        compose.onNodeWithText(AppStrings.SincronizacionCompletada).assertExists()
    }

    @Test
    fun snackbar_action_runs_the_callback_when_tapped() {
        val host = AmFeedbackHost()
        var acted = false
        compose.setContent {
            AmTheme {
                AmFeedbackEvent(
                    event = Any(),
                    message = "No pudimos sincronizar.",
                    feedbackHost = host,
                    actionLabel = AppStrings.Reintentar,
                    onAction = { acted = true },
                )
                AmSnackbarHost(host)
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.Reintentar).performClick()
        compose.waitForIdle()
        assert(acted) { "La acción del Snackbar debe ejecutar su callback" }
    }
}
