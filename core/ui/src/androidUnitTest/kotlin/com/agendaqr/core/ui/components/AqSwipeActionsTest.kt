package com.agendaqr.core.ui.components

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqSwipeActionsTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun swipeActions_rendersContent_andExposesCustomAccessibilityActions() {
        var actionExecuted = false
        val actionLabel = "Eliminar elemento"

        compose.setContent {
            AqTheme {
                AqSwipeActions(
                    actions = listOf(
                        AqSwipeAction(
                            label = actionLabel,
                            icon = AqIcons.Trash,
                            onAction = { actionExecuted = true },
                        ),
                    ),
                ) {
                    AqText(text = "Contenido de prueba")
                }
            }
        }

        // Verifica que el contenido se renderiza
        compose.onNodeWithText("Contenido de prueba").assertExists()

        // Verifica que la acción semántica existe para TalkBack en el árbol de accesibilidad
        val customActionsMatcher = SemanticsMatcher.keyIsDefined(SemanticsActions.CustomActions)
        val node = compose.onNode(customActionsMatcher).fetchSemanticsNode()
        val customActions = node.config.getOrNull(SemanticsActions.CustomActions)
        assertNotNull(customActions)
        assertTrue(customActions!!.any { it.label == actionLabel })

        // Invoca la acción directamente como haría TalkBack
        val matchingAction = customActions.first { it.label == actionLabel }
        matchingAction.action()
        assertTrue(actionExecuted)
    }

    @Test
    fun swipeActions_disabled_doesNotExposeCustomActions() {
        val actionLabel = "Eliminar elemento"

        compose.setContent {
            AqTheme {
                AqSwipeActions(
                    actions = listOf(
                        AqSwipeAction(
                            label = actionLabel,
                            icon = AqIcons.Trash,
                            onAction = {},
                        ),
                    ),
                    enabled = false,
                ) {
                    AqText(text = "Contenido deshabilitado")
                }
            }
        }

        compose.onNodeWithText("Contenido deshabilitado").assertExists()
        val customActionsMatcher = SemanticsMatcher.keyIsDefined(SemanticsActions.CustomActions)
        compose.onNode(customActionsMatcher).assertDoesNotExist()
    }
}
