package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.agendaqr.core.ui.components.XauxaFeedbackHost
import com.agendaqr.core.ui.components.XauxaIcons
import com.agendaqr.core.ui.components.XauxaNavDestination
import com.agendaqr.core.ui.components.XauxaNavBar
import com.agendaqr.core.ui.components.XauxaSnackbarHost
import com.agendaqr.core.ui.components.XauxaFeedbackEvent
import com.agendaqr.core.ui.theme.XauxaTheme
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
        XauxaNavDestination(appRouteNameOf(AppRoute.Home), AppStrings.Inicio, XauxaIcons.Home),
        XauxaNavDestination(appRouteNameOf(AppRoute.Search), AppStrings.Buscar, XauxaIcons.Search),
        XauxaNavDestination(appRouteNameOf(AppRoute.Operations), AppStrings.Operaciones, XauxaIcons.Register),
        XauxaNavDestination(appRouteNameOf(AppRoute.Contexts), AppStrings.Contextos, XauxaIcons.Context),
    )

    @Test
    fun nav_bar_shows_all_four_destinations_with_labels() {
        compose.setContent {
            XauxaTheme {
                XauxaNavBar(
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
            XauxaTheme {
                XauxaNavBar(
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
        val host = XauxaFeedbackHost()
        compose.setContent {
            XauxaTheme {
                XauxaFeedbackEvent(
                    event = Any(),
                    message = AppStrings.SincronizacionCompletada,
                    feedbackHost = host,
                )
                XauxaSnackbarHost(host)
            }
        }
        compose.waitForIdle()
        // El evento transitorio vive en el Snackbar del Scaffold; el
        // anuncio accesible lo mantiene el propio XauxaFeedbackEvent.
        compose.onNodeWithText(AppStrings.SincronizacionCompletada).assertExists()
    }

    @Test
    fun snackbar_action_runs_the_callback_when_tapped() {
        val host = XauxaFeedbackHost()
        var acted = false
        compose.setContent {
            XauxaTheme {
                XauxaFeedbackEvent(
                    event = Any(),
                    message = "No pudimos sincronizar.",
                    feedbackHost = host,
                    actionLabel = AppStrings.Reintentar,
                    onAction = { acted = true },
                )
                XauxaSnackbarHost(host)
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.Reintentar).performClick()
        compose.waitForIdle()
        assert(acted) { "La acción del Snackbar debe ejecutar su callback" }
    }
}
