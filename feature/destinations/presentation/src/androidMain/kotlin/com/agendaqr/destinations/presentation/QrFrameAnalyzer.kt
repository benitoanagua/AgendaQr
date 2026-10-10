package com.agendaqr.destinations.presentation

import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.ReaderException
import com.google.zxing.common.HybridBinarizer

/**
 * S03: decodificación continua de frames de cámara.
 *
 * Pura (sin Android): recibe los planos de luminancia que entrega
 * ImageAnalysis y devuelve el contenido del QR si el frame contiene uno.
 * La rotación del sensor se respetan girando el contenido al buffer de
 * lectura de ZXing.
 */
object QrFrameAnalyzer {

    /**
     * Decodifica un frame YUV (solo el plano Y).
     *
     * @param y plano de luminancia del frame
     * @param width ancho del frame en píxeles
     * @param height alto del frame en píxeles
     * @param rotationDegrees rotación del frame respecto a la vertical
     * natural del lector (0/90/180/270 según el sensor)
     * @return el contenido del QR o null si el frame no contiene uno
     */
    fun decodeLuminance(y: ByteArray, width: Int, height: Int, rotationDegrees: Int): String? {
        if (width <= 0 || height <= 0) return null
        val rotated = rotateLuminance(y, width, height, rotationDegrees)
        val rotatedWidth: Int
        val rotatedHeight: Int
        when (rotationDegrees) {
            90, 270 -> {
                rotatedWidth = height
                rotatedHeight = width
            }
            else -> {
                rotatedWidth = width
                rotatedHeight = height
            }
        }
        val source = PlanarYUVLuminanceSource(
            rotated,
            rotatedWidth,
            rotatedHeight,
            0,
            0,
            rotatedWidth,
            rotatedHeight,
            false,
        )
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        return decodeSource(bitmap)
    }

    /**
     * Decodifica píxeles ARGB (vía de galería/share): mismo lector y mismas
     * hints que el análisis de cámara, para que el contenido capturado en
     * S09 sea idéntico venga de donde venga.
     */
    fun decodeArgb(pixels: IntArray, width: Int, height: Int): String? {
        if (width <= 0 || height <= 0 || pixels.size < width * height) return null
        return decodeSource(BinaryBitmap(HybridBinarizer(RGBLuminanceSource(width, height, pixels))))
    }

    private fun decodeSource(bitmap: BinaryBitmap): String? {
        val reader = MultiFormatReader().apply {
            setHints(
                mapOf(
                    DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
                    DecodeHintType.TRY_HARDER to true,
                ),
            )
        }
        return try {
            reader.decodeWithState(bitmap).text
        } catch (_: ReaderException) {
            null
        } finally {
            reader.reset()
        }
    }

    /**
     * Quita el padding de fin de fila de un plano Y real: los frames de
     * cámara traen `rowStride >= width` (alineación del sensor) y copiar
     * el buffer crudo cizalla la imagen — en físicos eso rompía TODA
     * decodificación. Puro y testeable.
     */
    fun stripStride(padded: ByteArray, width: Int, height: Int, rowStride: Int): ByteArray {
        require(width > 0 && height > 0 && rowStride >= width) { "dimensiones inválidas" }
        require(padded.size >= rowStride * (height - 1) + width) { "buffer corto para su stride" }
        if (rowStride == width) return padded.copyOf(width * height)
        val out = ByteArray(width * height)
        for (row in 0 until height) {
            padded.copyInto(out, destinationOffset = row * width, startIndex = row * rowStride, endIndex = row * rowStride + width)
        }
        return out
    }

    /** Gira el plano de luminancia al orientación de lectura natural. */
    internal fun rotateLuminance(y: ByteArray, width: Int, height: Int, rotationDegrees: Int): ByteArray {
        if (rotationDegrees % 360 == 0) return y
        val output = ByteArray(y.size)
        when (((rotationDegrees % 360) + 360) % 360) {
            90 -> {
                for (row in 0 until height) {
                    for (col in 0 until width) {
                        output[col * height + (height - 1 - row)] = y[row * width + col]
                    }
                }
            }
            180 -> {
                for (row in 0 until height) {
                    for (col in 0 until width) {
                        output[(height - 1 - row) * width + (width - 1 - col)] = y[row * width + col]
                    }
                }
            }
            270 -> {
                for (row in 0 until height) {
                    for (col in 0 until width) {
                        output[(width - 1 - col) * height + row] = y[row * width + col]
                    }
                }
            }
        }
        return output
    }
}

/**
 * Lógica de estado del escaneo continuo (S03): "al detectar un QR pasa a
 * S09" — el primer QR detectado se emite una única vez y la sesión deja de
 * aceptar frames hasta reiniciarse, para no disparar el flujo dos veces
 * con el mismo código.
 */
class QrScanSession {
    private var emitted = false

    /** Procesa un contenido decodificado; emite solo el primero. */
    fun onFrame(content: String?): String? {
        if (emitted || content == null) return null
        emitted = true
        return content
    }

    val hasEmitted: Boolean get() = emitted

    fun reset() {
        emitted = false
    }
}
