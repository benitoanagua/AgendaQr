package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AmTheme
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h760dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AmSecondaryButtonWidthTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun fillMaxWidth_surface_matches_parent() {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, 1f)) {
                AmTheme {
                    Column(Modifier.fillMaxWidth()) {
                        AmSecondaryButton("Guardar", onClick = {}, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
        compose.waitForIdle()
        val screenRight = compose.onRoot().getBoundsInRoot().right
        val node = compose.onNodeWithText("Guardar").getBoundsInRoot()
        // La superficie visible debe ocupar casi todo el ancho (>= 90%).
        assertTrue(node.right.value - node.left.value >= screenRight.value * 0.9f)
    }
}