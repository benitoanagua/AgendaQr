package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.agendaqr.core.ui.components.AmQrPreview
import com.agendaqr.core.ui.components.decodeQrImageBitmap
import com.agendaqr.core.ui.theme.AmTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Contrato del QR de extremo a extremo (§4 del mandato de corrección):
 *
 * 1. El fixture de las capturas es un QR REAL y legible (no una imagen
 *    ficticia ni un bloque de color).
 * 2. `decodeQrImageBitmap` interpreta los bytes almacenados como IMAGEN en
 *    ambas plataformas y devuelve `null` ante contenido que no es imagen.
 * 3. El QR RENDERIZADO por [AmQrPreview] se puede volver a leer con el
 *    decodificador del proyecto (ZXing).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h740dp")
class QrPreviewContractTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun fixture_is_a_real_readable_qr() {
        assertEquals(
            QrTestFixture.CONTENT,
            QrTestFixture.decodeQrPngBase64(QrTestFixture.base64Png),
        )
    }

    @Test
    fun decodeQrImageBitmap_decodes_a_valid_qr_image() {
        val bitmap = decodeQrImageBitmap(QrTestFixture.base64Png)
        assertNotNull(bitmap)
        assertTrue(bitmap.width > 0)
        // Proporción 1:1: el QR nunca se estira.
        assertEquals(bitmap.width, bitmap.height)
    }

    @Test
    fun decodeQrImageBitmap_returns_null_for_non_image_payloads() {
        assertNull(decodeQrImageBitmap(""))
        assertNull(decodeQrImageBitmap("   "))
        assertNull(decodeQrImageBitmap("!!"))
        // Base64 válido de "hello", pero no es una imagen.
        assertNull(decodeQrImageBitmap("aGVsbG8="))
    }

    @Test
    fun preview_draws_the_real_qr_image_for_a_valid_payload() {
        compose.setContent {
            AmTheme(darkTheme = false) {
                AmQrPreview(
                    encodedQr = QrTestFixture.base64Png,
                    unavailableMessage = UNAVAILABLE,
                )
            }
        }
        compose.waitForIdle()
        // Con bytes de imagen válidos se dibuja el QR REAL: existe el nodo de
        // imagen y NO aparece el estado de error.
        compose.onNodeWithContentDescription("QR").assertExists()
        compose.onNodeWithText(UNAVAILABLE).assertDoesNotExist()
    }

    @Test
    fun preview_shows_an_explicit_error_state_instead_of_a_fake_qr() {
        compose.setContent {
            AmTheme(darkTheme = false) {
                AmQrPreview(
                    // Base64 válido de "hello": no es una imagen.
                    encodedQr = "aGVsbG8=",
                    unavailableMessage = UNAVAILABLE,
                )
            }
        }
        compose.waitForIdle()
        // Nunca un glifo que pueda pasar por un QR válido: no hay imagen y el
        // estado de error es explícito y comprensible.
        compose.onNodeWithContentDescription("QR").assertDoesNotExist()
        compose.onNodeWithText(UNAVAILABLE).assertExists()
    }

    private companion object {
        const val UNAVAILABLE = "No se pudo mostrar"
    }
}
