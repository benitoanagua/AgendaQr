package com.agendaqr.core.ui.components

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AmTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Contratos de la capa aditiva de componentes del sistema. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AmPhase3ContractTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun link_is_named_action_with_48dp_target() {
        var clicks = 0
        compose.setContent {
            AmTheme { AmLink(label = "Ver detalle", onClick = { clicks++ }) }
        }
        compose.onNodeWithText("Ver detalle")
            .assertHasClickAction()
            .assertHeightIsAtLeast(48.dp)
            .performClick()
        assert(clicks == 1)
    }

    @Test
    fun checkbox_exposes_role_and_48dp_target() {
        var checked = false
        compose.setContent {
            AmTheme {
                AmCheckbox(
                    label = "Adjuntar comprobante",
                    checked = checked,
                    onCheckedChange = { checked = it },
                )
            }
        }
        compose.onNodeWithText("Adjuntar comprobante")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .assertHeightIsAtLeast(48.dp)
            .performClick()
        assert(checked) { "La casilla debe reflejar el cambio del llamador" }
    }

    @Test
    fun switch_exposes_role_and_48dp_target() {
        compose.setContent {
            AmTheme {
                AmSwitch(label = "Sincronización", checked = true, onCheckedChange = {})
            }
        }
        compose.onNodeWithText("Sincronización")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
            .assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun progress_is_clamped_to_the_unit_interval() {
        assert(AmProgress(0f).percent == 0)
        assert(AmProgress(1f).percent == 100)
        assert(runCatching { AmProgress(1.2f) }.isFailure)
        assert(runCatching { AmProgress(-0.1f) }.isFailure)
    }

    @Test
    fun evidence_badge_uses_injected_copy_only() {
        compose.setContent {
            AmTheme {
                AmEvidenceBadge(text = "Comprobante recibido", icon = AmIcons.Attachment)
            }
        }
        compose.onNodeWithText("Comprobante recibido").assertExists()
    }

    @Test
    fun sync_indicator_hides_synced_state_by_default() {
        val labels = AmSyncLabels("Sincronizado", "Pendiente", "Sincronizando", "Error", "Sin servicio")
        val showSynced = mutableStateOf(false)
        compose.setContent {
            AmTheme {
                AmSyncIndicator(
                    state = AmSyncState.Synced,
                    labels = labels,
                    showWhenSynced = showSynced.value,
                )
            }
        }
        compose.onNodeWithText("Sincronizado").assertDoesNotExist()
        compose.runOnIdle { showSynced.value = true }
        compose.waitForIdle()
        compose.onNodeWithText("Sincronizado").assertExists()
    }

    @Test
    fun index_table_renders_headers_and_rows_with_injected_copy() {
        compose.setContent {
            AmTheme {
                AmIndexTable(
                    columns = listOf(
                        AmTableColumn("concept", "Concepto"),
                        AmTableColumn("amount", "Importe"),
                    ),
                    rows = listOf(AmTableRow("1", mapOf("concept" to "Cobro", "amount" to "10,00"))),
                )
            }
        }
        compose.onNodeWithText("Concepto").assertExists()
        compose.onNodeWithText("10,00").assertExists()
    }
}
