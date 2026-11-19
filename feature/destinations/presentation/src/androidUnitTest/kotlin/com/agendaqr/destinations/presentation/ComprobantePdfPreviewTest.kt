package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h760dp")
class ComprobantePdfPreviewTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun invalid_pdf_uses_a_honest_fallback_without_crashing() {
        compose.setContent {
            AqTheme {
                ComprobantePreview("not a pdf".encodeToByteArray(), "application/pdf")
            }
        }
        compose.waitForIdle()
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText(AppStrings.PdfAdjuntoSinVistaPrevia).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(AppStrings.PdfAdjuntoSinVistaPrevia).assertExists()
    }
}
