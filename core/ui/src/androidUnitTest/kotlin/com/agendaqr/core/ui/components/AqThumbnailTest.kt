package com.agendaqr.core.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.motion.LocalReducedMotion
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Pruebas de los estados de [AqThumbnail]: la distinción Loading / Failure /
 * Success es determinística porque el estado se inyecta (no depende de la
 * decodificación asíncrona de un archivo real).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h760dp")
class AqThumbnailTest {

    @get:Rule
    val compose = createComposeRule()

    private val bitmap = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888).asImageBitmap()

    @Test
    fun loading_render_esqueleto_sin_icono_de_fallback() {
        compose.setContent {
            AqTheme {
                CompositionLocalProvider(LocalReducedMotion provides true) {
                    AqThumbnail(
                        result = ThumbnailResult(ThumbnailState.Loading),
                        mimeType = "image/jpeg",
                        modifier = Modifier.size(56.dp),
                    )
                }
            }
        }
        compose.waitForIdle()
        // Carga nunca muestra el ícono de fallback.
        compose.onNodeWithTag("aq-thumbnail-fallback-qr").assertDoesNotExist()
        compose.onNodeWithTag("aq-thumbnail-fallback-attachment").assertDoesNotExist()
    }

    @Test
    fun failure_imagen_render_icono_qr_con_descripcion() {
        compose.setContent {
            AqTheme {
                CompositionLocalProvider(LocalReducedMotion provides true) {
                    AqThumbnail(
                        result = ThumbnailResult(ThumbnailState.Failure),
                        mimeType = "image/jpeg",
                        modifier = Modifier.size(56.dp),
                        contentDescription = "Vista previa no disponible",
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("aq-thumbnail-fallback-qr").assertExists()
        compose.onNodeWithTag("aq-thumbnail-fallback-attachment").assertDoesNotExist()
        compose.onNodeWithContentDescription("Vista previa no disponible").assertExists()
    }

    @Test
    fun failure_pdf_render_icono_attachment_con_descripcion() {
        compose.setContent {
            AqTheme {
                CompositionLocalProvider(LocalReducedMotion provides true) {
                    AqThumbnail(
                        result = ThumbnailResult(ThumbnailState.Failure),
                        mimeType = "application/pdf",
                        modifier = Modifier.size(56.dp),
                        contentDescription = "PDF adjunto (sin vista previa)",
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("aq-thumbnail-fallback-attachment").assertExists()
        compose.onNodeWithTag("aq-thumbnail-fallback-qr").assertDoesNotExist()
        compose.onNodeWithContentDescription("PDF adjunto (sin vista previa)").assertExists()
    }

    @Test
    fun success_render_imagen_recortada_sin_fallback() {
        compose.setContent {
            AqTheme {
                CompositionLocalProvider(LocalReducedMotion provides true) {
                    AqThumbnail(
                        result = ThumbnailResult(ThumbnailState.Success, bitmap = bitmap),
                        mimeType = "image/jpeg",
                        modifier = Modifier.size(56.dp),
                    )
                }
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("aq-thumbnail-fallback-qr").assertDoesNotExist()
        compose.onNodeWithTag("aq-thumbnail-fallback-attachment").assertDoesNotExist()
    }
}
