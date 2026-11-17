package com.agendaqr.destinations.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.agendaqr.core.ui.theme.AmTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Comprobación de que las pantallas consumen las extensiones canónicas de core. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AmExtensionAdoptionTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun sync_badge_uses_core_indicator_and_keeps_domain_copy() {
        compose.setContent {
            AmTheme {
                ElementSyncBadge(ElementSyncStatus.ErrorRecoverable)
            }
        }
        compose.onNodeWithText(ElementSyncStatus.ErrorRecoverable.label).assertExists()
        compose.onNodeWithText("Sincronizado").assertDoesNotExist()
    }

    @Test
    fun sync_badge_hides_synced_state_without_hiding_actionable_states() {
        val status = mutableStateOf(ElementSyncStatus.Synced)
        compose.setContent {
            AmTheme { ElementSyncBadge(status.value) }
        }
        compose.onNodeWithText(ElementSyncStatus.Synced.label).assertDoesNotExist()
        compose.runOnIdle { status.value = ElementSyncStatus.Pending }
        compose.waitForIdle()
        compose.onNodeWithText(ElementSyncStatus.Pending.label).assertExists()
    }
}
