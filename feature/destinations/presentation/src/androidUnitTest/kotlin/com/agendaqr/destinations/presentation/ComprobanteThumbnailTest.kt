package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Prueba de humo para [ComprobanteThumbnail]: debe componerse sin lanzar
 * incluso cuando el archivo no existe o no se puede decodificar (ruta de
 * fallback).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h760dp")
class ComprobanteThumbnailTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun thumbnail_composes_without_crashing_when_file_is_missing() {
        compose.setContent {
            AqTheme {
                ComprobanteThumbnail(
                    file = "file:///no-existe.jpg",
                    mimeType = "image/jpeg",
                )
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun thumbnail_composes_without_crashing_for_pdf() {
        compose.setContent {
            AqTheme {
                ComprobanteThumbnail(
                    file = "file:///no-existe.pdf",
                    mimeType = "application/pdf",
                )
            }
        }
        compose.waitForIdle()
    }
}
