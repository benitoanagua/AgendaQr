package com.agendaqr.destinations.presentation

import com.agendaqr.core.ui.components.ComprobanteThumbnail
import androidx.compose.ui.test.junit4.createComposeRule
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Pruebas de humo para [ComprobanteThumbnail]: debe componerse sin lanzar
 * incluso cuando el archivo no existe o no se puede decodificar (ruta de
 * fallback).
 *
 * Nota de entorno: bajo Robolectric, `BitmapFactory.decodeFile` devuelve un
 * bitmap ficticio para rutas inexistentes, por lo que este módulo no puede
 * forzar el estado `Failure` a través de un archivo ausente. La distinción
 * determinista de estados (`Loading` esqueleto vs. `Failure` ícono QR /
 * adjunto vs. `Success` imagen) se prueba en
 * `core.ui.components.AqThumbnailTest` inyectando el estado directamente.
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
