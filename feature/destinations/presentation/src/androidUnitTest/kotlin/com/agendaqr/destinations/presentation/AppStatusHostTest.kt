package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.agendaqr.core.ui.theme.AmTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * AppStatusHost (§12 V1.2): prioridad de estados — error de importación >
 * banner FAILED con "Reintentar ahora" > indicadores compactos; "Sin
 * conexión" y "N pendientes" nunca son banner a ancho completo. Viven
 * dentro del Scaffold (insets del contenedor; el componente no consume
 * insets propios).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppStatusHostTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun import_error_banner_wins_over_everything_else() {
        compose.setContent {
            AmTheme {
                AppStatusHost(
                    importError = "No se pudo abrir el archivo compartido.",
                    isOffline = true,
                    pendingCount = 3,
                    hasFailed = true,
                    onDismissImportError = {},
                    onRetryNow = {},
                )
            }
        }
        compose.waitForIdle()
        // Solo el banner de error de importación (con acción y descarte).
        compose.onNodeWithText(AppStrings.Descartar).assertExists()
        // Los indicadores compactos no se duplican detrás del banner.
        val compact = compose.onAllNodesWithText(AppStrings.SinConexion).fetchSemanticsNodes()
        assert(compact.isEmpty()) { "El indicador compacto no compite con el banner de error" }
    }

    @Test
    fun failed_sync_shows_full_banner_with_retry_now() {
        compose.setContent {
            AmTheme {
                AppStatusHost(
                    importError = null,
                    isOffline = false,
                    pendingCount = 2,
                    hasFailed = true,
                    onDismissImportError = {},
                    onRetryNow = {},
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Sincronización pendiente: 2 · reintentando…").assertExists()
        compose.onNodeWithText(AppStrings.ReintentarAhora).assertExists()
    }

    @Test
    fun offline_and_pending_without_failures_are_compact_indicators() {
        compose.setContent {
            AmTheme {
                AppStatusHost(
                    importError = null,
                    isOffline = true,
                    pendingCount = 1,
                    hasFailed = false,
                    onDismissImportError = {},
                    onRetryNow = {},
                )
            }
        }
        compose.waitForIdle()
        // Chips compactos con texto (§11: nunca solo color).
        compose.onNodeWithText(AppStrings.SinConexion).assertExists()
        compose.onNodeWithText("Sincronización pendiente: 1").assertExists()
        // Sin banner completo de reintento cuando no hay FAILED.
        val retry = compose.onAllNodesWithText(AppStrings.ReintentarAhora).fetchSemanticsNodes()
        assert(retry.isEmpty()) { "'Reintentar ahora' solo con mutaciones FAILED" }
    }

    @Test
    fun quiet_state_renders_nothing() {
        compose.setContent {
            AmTheme {
                AppStatusHost(
                    importError = null,
                    isOffline = false,
                    pendingCount = 0,
                    hasFailed = false,
                    onDismissImportError = {},
                    onRetryNow = {},
                )
            }
        }
        compose.waitForIdle()
        val offline = compose.onAllNodesWithText(AppStrings.SinConexion).fetchSemanticsNodes()
        assert(offline.isEmpty())
    }
}
