package com.agendaqr.core.ui.components

import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * S00: [AqPinDots] acepta estado de error sin romper composición.
 * El mensaje textual sigue en la pantalla; el flag solo refuerza el tono.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h760dp")
class AqPinDotsErrorTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun dots_compose_with_error_flag() {
        compose.setContent {
            AqTheme(darkTheme = false) {
                AqPinDots(filled = 3, total = 4, error = true)
            }
        }
        compose.waitForIdle()
        compose.onRoot().getBoundsInRoot()
    }

    @Test
    fun dots_compose_without_error_flag() {
        compose.setContent {
            AqTheme(darkTheme = false) {
                AqPinDots(filled = 2, total = 4, error = false)
            }
        }
        compose.waitForIdle()
        compose.onRoot().getBoundsInRoot()
    }
}
