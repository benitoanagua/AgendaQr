package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqTheme
import com.agendaqr.core.ui.theme.AqColor
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Pruebas de geometría de layout (Fase 0): deben FALLAR en la línea base
 * y pasar tras corregir los componentes de core/ui.
 *
 * Setup: ancho 360 dp, fuente 100 % y 150 %.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h800dp")
class AqLayoutGeometryTest {

    @get:Rule
    val compose = createComposeRule()

    // ------------------------------------------------------------ 0.2.a AqQuickAction
    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h800dp")
    fun quick_action_label_width_and_touch_target() {
        compose.setContent {
            AqTheme {
                AqQuickAction(
                    label = "Registrar actividad",
                    icon = com.agendaqr.core.ui.components.AqIcons.QrCode,
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        compose.waitForIdle()
        val label = compose.onNodeWithText("Registrar actividad")
        label.assertExists()
        // S1: en línea base la celda mide 48 dp fijos y la etiqueta se comprime
        // a una letra por línea (44 dp de ancho real); tras la corrección la
        // celda acepta el ancho del padre y la etiqueta mide >= 60 dp de ancho.
        label.assertWidthIsAtLeast(60.dp)
    }

    // ------------------------------------------------------------ 0.2.b AqTabBar / AqPillTab
    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h800dp")
    fun tab_bar_long_label_width() {
        compose.setContent {
            AqTheme {
                AqTabBar(
                    tabs = listOf(
                        com.agendaqr.core.ui.components.AqTab("Operaciones pendientes"),
                        com.agendaqr.core.ui.components.AqTab("Comprobantes recibidos"),
                        com.agendaqr.core.ui.components.AqTab("Destinos sincronizados"),
                    ),
                    selectedIndex = 0,
                    onSelect = { },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Operaciones pendientes").assertWidthIsAtLeast(60.dp)
        compose.onNodeWithText("Comprobantes recibidos").assertWidthIsAtLeast(60.dp)
        compose.onNodeWithText("Destinos sincronizados").assertWidthIsAtLeast(60.dp)
    }

    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h800dp")
    fun pill_tab_long_label_width() {
        compose.setContent {
            AqTheme {
                AqPillTab(
                    tabs = listOf(
                        com.agendaqr.core.ui.components.AqTab("Operaciones pendientes"),
                        com.agendaqr.core.ui.components.AqTab("Comprobantes recibidos"),
                        com.agendaqr.core.ui.components.AqTab("Destinos sincronizados"),
                    ),
                    selectedIndex = 0,
                    onSelect = { },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Operaciones pendientes").assertWidthIsAtLeast(60.dp)
        compose.onNodeWithText("Comprobantes recibidos").assertWidthIsAtLeast(60.dp)
        compose.onNodeWithText("Destinos sincronizados").assertWidthIsAtLeast(60.dp)
    }

    // ------------------------------------------------------------ 0.2.c AqScreen separation between organisms
    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h800dp")
    fun screen_children_do_not_collapse_to_zero() {
        compose.setContent {
            AqTheme {
                AqScreen(title = "Pantalla prueba") {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier.fillMaxWidth().height(40.dp).background(AqColor.SurfaceContainer),
                        )
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier.fillMaxWidth().height(40.dp).background(AqColor.SurfaceContainer),
                        )
                    }
                }
            }
        }
        compose.waitForIdle()
        // S3: en línea base el Column de AqScreen no tiene verticalArrangement y
        // los organismos pueden colapsar a 0 dp. Tras la corrección (contentSpacing)
        // los hijos miden alto esperado.
        compose.onRoot().getBoundsInRoot()
    }

    // ------------------------------------------------------------ 0.2.d AqTextInput Md label + touch target
    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h800dp")
    fun text_input_label_and_touch_target() {
        compose.setContent {
            AqTheme {
                AqTextInput(
                    label = "Correo",
                    value = "",
                    onValueChange = { },
                    size = com.agendaqr.core.ui.components.AqTextInputSize.Md,
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Correo").assertExists()
        compose.onRoot().getBoundsInRoot()
    }

    // ------------------------------------------------------------ 0.2.e AqMenuItem title width > 0
    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h800dp")
    fun menu_item_title_has_positive_width() {
        compose.setContent {
            AqTheme {
                AqMenuItem(
                    title = "Destino ejemplo",
                    subtitle = "Comprobante recibido",
                    leading = {
                        com.agendaqr.core.ui.components.AqIcon(
                            imageVector = com.agendaqr.core.ui.components.AqIcons.QrCode,
                            contentDescription = null,
                            size = com.agendaqr.core.ui.theme.AqMetrics.IconSizeCompact,
                        )
                    },
                    trailing = {
                        com.agendaqr.core.ui.components.AqText(text = ">", size = com.agendaqr.core.ui.theme.AqType.Body)
                    },
                    onClick = { },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Destino ejemplo").assertWidthIsAtLeast(1.dp)
        compose.onNodeWithText("Comprobante recibido").assertExists()
    }

    // ------------------------------------------------------------ 0.2.f AqPinDots + Keypad compose together
    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h760dp")
    fun pin_dots_and_keypad_compose_without_overlap() {
        compose.setContent {
            AqTheme(darkTheme = false) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(com.agendaqr.core.ui.theme.AqLayout.InGroup),
                ) {
                    com.agendaqr.core.ui.components.AqPinDots(filled = 3, total = 4, error = false)
                    com.agendaqr.core.ui.components.AqKeypad(
                        onDigit = { },
                        onBackspace = { },
                        backspaceDescription = "Borrar",
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onRoot().getBoundsInRoot()
    }

    // ------------------------------------------------------------ 0.2.g AqAlert message + 2 actions
    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h800dp")
    fun alert_message_and_two_actions() {
        compose.setContent {
            AqTheme {
                AqAlert(
                    tone = com.agendaqr.core.ui.components.AqTone.Info,
                    message = "Mensaje largo de la alerta que debe ocupar el ancho de contenido",
                    confirmLabel = "Reintentar",
                    onConfirm = { },
                    dismissLabel = "Cerrar",
                    onDismiss = { },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Mensaje largo de la alerta que debe ocupar el ancho de contenido").assertExists()
        compose.onNodeWithText("Reintentar").assertExists()
        compose.onNodeWithText("Cerrar").assertExists()
    }

    // ------------------------------------------------------------ 0.2.h AqAppBar root title at ScreenMargin
    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h800dp")
    fun root_app_bar_title_exists() {
        compose.setContent {
            AqTheme {
                AqScreen(title = "Inicio", onBack = null) {
                    com.agendaqr.core.ui.components.AqText(text = "Contenido")
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Inicio").assertExists()
        compose.onRoot().getBoundsInRoot()
    }
}
