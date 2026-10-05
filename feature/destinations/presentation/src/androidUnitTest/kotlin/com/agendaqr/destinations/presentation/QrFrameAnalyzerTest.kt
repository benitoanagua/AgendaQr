package com.agendaqr.destinations.presentation

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.encoder.Encoder
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * T8 — S03: lógica de decodificación continua de cámara.
 *
 * El frame sintético es un QR REAL (matriz de ZXing renderizada a
 * luminancia): la prueba ejercita el mismo camino que un frame de
 * ImageAnalysis (plano Y + rotación del sensor).
 */
class QrFrameAnalyzerTest {

    private val content = "https://agendaqr.test/qr/camera-check"

    private fun qrLuminance(scale: Int = 4, quiet: Int = 4): Triple<ByteArray, Int, Int> {
        val code = Encoder.encode(content, ErrorCorrectionLevel.L)
        val matrix = code.matrix
        val width = matrix.width
        val height = matrix.height
        val out = Array(width + quiet * 2) { IntArray(height + quiet * 2) { 0xFF } }
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (matrix.get(x, y).toInt() != 0) {
                    out[x + quiet][y + quiet] = 0x00
                }
            }
        }
        val w = (width + quiet * 2) * scale
        val h = (height + quiet * 2) * scale
        val bytes = ByteArray(w * h)
        for (y in 0 until h) {
            for (x in 0 until w) {
                bytes[y * w + x] = out[x / scale][y / scale].toByte()
            }
        }
        return Triple(bytes, w, h)
    }

    @Test
    fun decodes_a_qr_from_an_upright_frame() {
        val (y, w, h) = qrLuminance()
        assertEquals(content, QrFrameAnalyzer.decodeLuminance(y, w, h, rotationDegrees = 0))
    }

    @Test
    fun decodes_a_qr_from_a_frame_rotated_by_the_sensor() {
        val (y, w, h) = qrLuminance()
        // El sensor entrega el frame girado; el analizador lo endereza.
        val rotated90 = QrFrameAnalyzer.rotateLuminance(y, w, h, 270)
        assertEquals(content, QrFrameAnalyzer.decodeLuminance(rotated90, h, w, rotationDegrees = 90))

        val rotated180 = QrFrameAnalyzer.rotateLuminance(y, w, h, 180)
        assertEquals(content, QrFrameAnalyzer.decodeLuminance(rotated180, w, h, rotationDegrees = 180))

        val rotated270 = QrFrameAnalyzer.rotateLuminance(y, w, h, 90)
        assertEquals(content, QrFrameAnalyzer.decodeLuminance(rotated270, h, w, rotationDegrees = 270))
    }

    @Test
    fun blank_frame_decodes_nothing() {
        val (y, w, h) = qrLuminance()
        val blank = ByteArray(y.size) { 0x7F.toByte() }
        assertNull(QrFrameAnalyzer.decodeLuminance(blank, w, h, 0))
    }

    @Test
    fun invalid_dimensions_are_rejected_safely() {
        assertNull(QrFrameAnalyzer.decodeLuminance(ByteArray(0), 0, 0, 0))
        assertNull(QrFrameAnalyzer.decodeLuminance(ByteArray(10), -5, 5, 0))
    }

    @Test
    fun scan_session_emits_only_the_first_detection() {
        val session = QrScanSession()
        assertEquals(content, session.onFrame(content))
        assertTrue(session.hasEmitted)
        // El mismo QR detectado de nuevo no re-dispara el flujo (sin doble
        // navegación a S09 con el mismo código).
        assertNull(session.onFrame(content))
        assertNull(session.onFrame("otro"))
        session.reset()
        assertFalse(session.hasEmitted)
        assertEquals("otro", session.onFrame("otro"))
    }

    @Test
    fun scan_session_ignores_empty_frames_until_a_real_one() {
        val session = QrScanSession()
        assertNull(session.onFrame(null))
        assertFalse(session.hasEmitted)
        assertEquals(content, session.onFrame(content))
    }
}

/**
 * S09 — la galería captura el MISMO contenido que la cámara (decodeArgb
 * comparte lector y hints con decodeLuminance).
 */
class QrGalleryContentTest {

    private val content = "https://agendaqr.test/qr/gallery-check"

    private fun qrArgb(scale: Int = 4, quiet: Int = 4): Triple<IntArray, Int, Int> {
        val code = Encoder.encode(content, ErrorCorrectionLevel.L)
        val matrix = code.matrix
        val w = (matrix.width + quiet * 2) * scale
        val h = (matrix.height + quiet * 2) * scale
        val pixels = IntArray(w * h) { 0xFFFFFFFF.toInt() }
        for (y in 0 until h) {
            for (x in 0 until w) {
                val mx = x / scale - quiet
                val my = y / scale - quiet
                if (mx in 0 until matrix.width && my in 0 until matrix.height &&
                    matrix.get(mx, my).toInt() != 0
                ) {
                    pixels[y * w + x] = 0xFF000000.toInt()
                }
            }
        }
        return Triple(pixels, w, h)
    }

    @Test
    fun gallery_decode_captures_the_same_content_as_camera() {
        val (pixels, w, h) = qrArgb()
        assertEquals(content, QrFrameAnalyzer.decodeArgb(pixels, w, h))
    }

    @Test
    fun blank_argb_decodes_nothing() {
        val (_, w, h) = qrArgb()
        assertNull(QrFrameAnalyzer.decodeArgb(IntArray(w * h) { 0xFFFFFFFF.toInt() }, w, h))
    }

    @Test
    fun undersized_buffer_is_rejected_safely() {
        assertNull(QrFrameAnalyzer.decodeArgb(IntArray(10), 100, 100))
        assertNull(QrFrameAnalyzer.decodeArgb(IntArray(0), 0, 0))
    }

    @Test
    fun qr_asset_content_defaults_to_unknown() {
        // Sin contenido decodificado (p. ej. releído del remoto) la UI
        // simplemente no muestra evidencia; el flujo no se rompe.
        val asset = com.agendaqr.destinations.domain.QrAsset(encoded = "eA==")
        assertNull(asset.content)
        assertEquals("https://x.test", asset.copy(content = "https://x.test").content)
    }
}

/**
 * Frames reales: el sensor alinea filas (rowStride > width). Sin quitar
 * el padding, la imagen llega cizallada y NINGÚN frame decodifica
 * (hallado con un físico: en el emulador no hay QR que lo evidencie).
 */
class QrStrideTest {

    private val content = "https://agendaqr.test/qr/stride-check"

    private fun qrLuminance(scale: Int = 4, quiet: Int = 4): Triple<ByteArray, Int, Int> {
        val code = Encoder.encode(content, ErrorCorrectionLevel.L)
        val matrix = code.matrix
        val w = (matrix.width + quiet * 2) * scale
        val h = (matrix.height + quiet * 2) * scale
        val bytes = ByteArray(w * h) { 0xFF.toByte() }
        for (y in 0 until h) {
            for (x in 0 until w) {
                val mx = x / scale - quiet
                val my = y / scale - quiet
                if (mx in 0 until matrix.width && my in 0 until matrix.height &&
                    matrix.get(mx, my).toInt() != 0
                ) {
                    bytes[y * w + x] = 0x00
                }
            }
        }
        return Triple(bytes, w, h)
    }

    @Test
    fun padded_frame_decodes_after_stripping_stride() {
        val (y, w, h) = qrLuminance()
        val rowStride = w + 32 // alineación típica del sensor
        val padded = ByteArray(rowStride * h) { 0x7F.toByte() }
        for (row in 0 until h) {
            y.copyInto(padded, destinationOffset = row * rowStride, startIndex = row * w, endIndex = row * w + w)
        }
        // Sin strip: el padding cizalla y no decodifica (el bug).
        assertNull(QrFrameAnalyzer.decodeLuminance(padded, w, h, rotationDegrees = 0))
        // Con strip: el contenido del QR real.
        val stripped = QrFrameAnalyzer.stripStride(padded, w, h, rowStride)
        assertEquals(content, QrFrameAnalyzer.decodeLuminance(stripped, w, h, rotationDegrees = 0))
    }

    @Test
    fun contiguous_frame_passes_through_unchanged() {
        val (y, w, h) = qrLuminance()
        assertContentEquals(y, QrFrameAnalyzer.stripStride(y, w, h, w))
    }

    @Test
    fun invalid_stride_is_rejected() {
        assertFailsWith<IllegalArgumentException> {
            QrFrameAnalyzer.stripStride(ByteArray(10), 100, 100, 50)
        }
        assertFailsWith<IllegalArgumentException> {
            QrFrameAnalyzer.stripStride(ByteArray(10), 10, 10, 100)
        }
    }
}
