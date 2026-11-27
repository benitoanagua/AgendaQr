package com.agendaqr.core.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqTheme
import com.agendaqr.core.ui.theme.DarkAqColorScheme
import com.agendaqr.core.ui.theme.LightAqColorScheme
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.contrastRatio
import com.github.takahirom.roborazzi.captureRoboImage
import java.io.File
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Fidelidad del switch contra la spec Moon v1 (ADR-0051):
 * - la sombra del pulgar no pinta nada fuera de la pista (`aq-switch-track`),
 * - pista y pulgar usan los tokens del tema activo en light y dark,
 * - la pista inactiva cumple el contraste funcional ≥ 3:1.
 *
 * Los píxeles se capturan con Roborazzi (`captureRoboImage`), que bajo
 * Robolectric solo escribe el PNG cuando `roborazzi.test.record=true`
 * (la misma propiedad que activa `-Pagendaqr.capture=true` en la evidencia
 * pixel); `captureToImage` de ui-test no produce frame en Robolectric.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class AqSwitchRenderTest {

    @get:Rule
    val compose = createComposeRule()

    @Before
    fun enablePixelCapture() {
        System.setProperty("roborazzi.test.record", "true")
    }

    @After
    fun disablePixelCapture() {
        System.clearProperty("roborazzi.test.record")
    }

    // `setContent` solo puede llamarse una vez por test: tema y estado del
    // switch se conducen con estado mutable entre iteraciones.
    private val darkState = mutableStateOf(false)
    private val checkedState = mutableStateOf(false)

    private fun setContentOnce() {
        compose.setContent {
            AqTheme(darkTheme = darkState.value) {
                val surface = LocalAqColorScheme.current.surface
                Box(
                    Modifier
                        .width(CanvasWidth)
                        .height(CanvasHeight)
                        .background(surface)
                        .testTag(CanvasTag),
                ) {
                    AqSwitch(
                        label = "",
                        checked = checkedState.value,
                        onCheckedChange = {},
                        size = AqSwitchSize.Sm,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }

    private fun showSwitch(dark: Boolean, checked: Boolean) {
        darkState.value = dark
        checkedState.value = checked
        // Las animaciones de color/posición (200 ms) deben llegar a su estado
        // final antes de medir píxeles.
        compose.mainClock.advanceTimeBy(1000)
        compose.waitForIdle()
    }

    private fun nodeBounds(tag: String): Rect =
        compose.onNodeWithTag(tag, useUnmergedTree = true)
            .fetchSemanticsNode().boundsInRoot

    /** PNG del canvas (región del nodo, px == unidades de layout a densidad 1). */
    private fun captureCanvas(): Pair<Bitmap, Rect> {
        val node = compose.onNodeWithTag(CanvasTag, useUnmergedTree = true)
        val bounds = node.fetchSemanticsNode().boundsInRoot
        val tmp = File.createTempFile("aq-switch-render", ".png")
        node.captureRoboImage(tmp.absolutePath)
        val bmp = BitmapFactory.decodeFile(tmp.absolutePath)
            ?: error("captureRoboImage no produjo PNG legible en ${tmp.absolutePath}")
        tmp.delete()
        return bmp to bounds
    }

    /** Bounds de la pista en coordenadas de píxel del bitmap del canvas. */
    private fun trackRectInBitmap(
        bmp: Bitmap,
        canvas: Rect,
        track: Rect,
    ): Rect = nodeRectInBitmap(bmp, canvas, track)

    /** Cualquier bounds de semántica mapeado a píxeles del bitmap. */
    private fun nodeRectInBitmap(
        bmp: Bitmap,
        canvas: Rect,
        node: Rect,
    ): Rect {
        val sx = bmp.width / canvas.width
        val sy = bmp.height / canvas.height
        return Rect(
            (node.left - canvas.left) * sx,
            (node.top - canvas.top) * sy,
            (node.right - canvas.left) * sx,
            (node.bottom - canvas.top) * sy,
        )
    }

    @Test
    fun switch_paints_nothing_outside_track_bounds_in_both_themes_and_states() {
        setContentOnce()
        for (dark in listOf(false, true)) {
            for (checked in listOf(false, true)) {
                showSwitch(dark, checked)
                val canvas = nodeBounds(CanvasTag)
                val track = nodeBounds("aq-switch-track")
                val (bmp, _) = captureCanvas()
                val surface = bmp.getPixel(2, 2)
                val trackRect = trackRectInBitmap(bmp, canvas, track)
                // Margen de 2 px para el antialiasing del borde del estadio;
                // la sombra del pulgar está recortada al contorno interior,
                // así que nada debe quedar fuera de la pista.
                val m = 2f
                var painted = 0
                var insidePainted = 0
                for (y in 0 until bmp.height) {
                    for (x in 0 until bmp.width) {
                        val inside = x >= trackRect.left - m && x < trackRect.right + m &&
                            y >= trackRect.top - m && y < trackRect.bottom + m
                        val onTrack = x >= trackRect.left && x < trackRect.right &&
                            y >= trackRect.top && y < trackRect.bottom
                        if (!inside) continue
                        if (channelDistancePx(bmp.getPixel(x, y), surface) > ColorTolerance) {
                            if (onTrack) insidePainted++ else painted++
                        }
                    }
                }
                assertEquals(
                    "píxeles fuera de la pista (dark=$dark, checked=$checked)",
                    0, painted,
                )
                assertTrue(
                    "la pista debe tener contenido (dark=$dark, checked=$checked)",
                    insidePainted > 0,
                )
            }
        }
    }

    @Test
    fun switch_track_and_thumb_match_active_theme_tokens() {
        data class Probe(
            val dark: Boolean,
            val checked: Boolean,
            val expectedTrack: Color,
            val expectedThumb: Color,
        )
        val cases = listOf(
            Probe(false, false, LightAqColorScheme.functionalBorder, LightAqColorScheme.onBrandFill),
            Probe(false, true, LightAqColorScheme.brandFill, LightAqColorScheme.onBrandFill),
            Probe(true, false, DarkAqColorScheme.functionalBorder, DarkAqColorScheme.onBrandFill),
            Probe(true, true, DarkAqColorScheme.brandFill, DarkAqColorScheme.onBrandFill),
        )
        // Geometría Sm: pista 60×32, padding 4, pulgar 24 → centro del pulgar
        // a 16 px de cada extremo; la muestra de pista libre queda en el
        // extremo opuesto al pulgar, a 2 px del borde interior.
        setContentOnce()
        for (c in cases) {
            showSwitch(c.dark, c.checked)
            val canvas = nodeBounds(CanvasTag)
            val track = nodeBounds("aq-switch-track")
            val (bmp, _) = captureCanvas()
            val t = trackRectInBitmap(bmp, canvas, track)
            val midY = (t.top + t.bottom) / 2f
            val trackSampleX = if (c.checked) {
                t.left + 6f
            } else {
                t.right - 6f
            }
            val thumbCenterX = if (c.checked) t.right - 16f else t.left + 16f
            assertColorNear(
                bmp.getPixel(trackSampleX.toInt(), midY.toInt()),
                c.expectedTrack,
                "pista (dark=${c.dark}, checked=${c.checked})",
            )
            // El pulgar lleva el glifo Phosphor Check/X (12 dp) centrado
            // (ADR-0051/0052): la muestra del fondo del pulgar se toma en
            // el anillo a 9 px del centro — fuera de la caja del icono
            // (±6 px) y dentro del pulgar de 24 px.
            assertColorNear(
                bmp.getPixel((thumbCenterX + 9f).toInt(), midY.toInt()),
                c.expectedThumb,
                "pulgar (dark=${c.dark}, checked=${c.checked})",
            )
        }
    }

    /**
     * ADR-0054: la pista y el pulgar son rectángulos redondeados (radio =
     * un cuarto del alto), no la cápsula `radius=999` del spec Moon ni un
     * disco. Se verifica por píxeles en la esquina y en el borde superior:
     *
     * - una cápsula/elipse no pintaría la esquina (3,3) de la pista ni la
     *   corrida larga del borde superior;
     * - un rectángulo vivo pintaría la esquina (1,1);
     * - un disco en el pulgar no cubriría las muestras (3,2)/(2,3) de su
     *   esquina, y un cuadrado vivo sí pintaría (1,1).
     */
    @Test
    fun switch_track_and_thumb_are_rounded_rectangles_not_capsules() {
        setContentOnce()
        for (dark in listOf(false, true)) {
            for (checked in listOf(false, true)) {
                showSwitch(dark, checked)
                val scheme = if (dark) DarkAqColorScheme else LightAqColorScheme
                val canvas = nodeBounds(CanvasTag)
                val track = nodeBounds("aq-switch-track")
                val thumb = nodeBounds("aq-switch-thumb")
                val (bmp, _) = captureCanvas()
                val surface = bmp.getPixel(2, 2)
                val t = trackRectInBitmap(bmp, canvas, track)
                val h = nodeRectInBitmap(bmp, canvas, thumb)
                val thumbArgb = scheme.onBrandFill.toArgb()
                val expectedTrack = if (checked) {
                    scheme.brandFill.toArgb()
                } else {
                    scheme.functionalBorder.toArgb()
                }
                fun nearest(px: Int, vararg candidates: Int): Int =
                    candidates.minByOrNull { channelDistancePx(px, it) } ?: px
                // Las muestras de esquina pueden llevar sombra del pulgar o
                // antialiasing: se clasifican por el color más cercano en
                // vez de exigir igualdad exacta.
                fun closerTo(px: Int, a: Int, b: Int): Boolean =
                    channelDistancePx(px, a) <= channelDistancePx(px, b)

                // --- Pista: esquina (3,3) pintada = no cápsula ---
                val corner = bmp.getPixel(
                    (t.left + 3).toInt(), (t.top + 3).toInt(),
                )
                assertTrue(
                    "pista oscura=$dark on=$checked: la esquina (3,3) debe " +
                        "pertenecer a la pista (rectángulo redondeado), " +
                        "no a la superficie (cápsula): $corner",
                    !closerTo(corner, surface, expectedTrack),
                )
                // --- Pista: esquina (1,1) vacía = esquinas redondeadas ---
                val tip = bmp.getPixel(
                    (t.left + 1).toInt(), (t.top + 1).toInt(),
                )
                assertTrue(
                    "pista oscura=$dark on=$checked: la esquina (1,1) debe " +
                        "quedar más cerca de la superficie (esquinas " +
                        "redondeadas), no de la pista: $tip",
                    closerTo(tip, surface, expectedTrack),
                )
                // --- Pista: borde superior con corrida larga = rectangular ---
                // En la fila y=top+1 la elipse solo pinta ~20 px alrededor
                // del centro; el rectángulo redondeado deja ~50 px rectos.
                val y = (t.top + 1).toInt()
                val midX = ((t.left + t.right) / 2).toInt()
                var run = 0
                var x = midX
                while (x < t.right.toInt() &&
                    nearest(bmp.getPixel(x, y), surface, expectedTrack) == expectedTrack
                ) {
                    run++
                    x++
                }
                x = midX - 1
                while (x >= t.left.toInt() &&
                    nearest(bmp.getPixel(x, y), surface, expectedTrack) == expectedTrack
                ) {
                    run++
                    x--
                }
                assertTrue(
                    "borde superior oscuro=$dark on=$checked: corrida " +
                        "recta=$run px (una cápsula daría ~20)",
                    run >= 40,
                )

                // --- Pulgar: esquinas redondeadas, no disco ni cuadrado ---
                for ((dx, dy) in listOf(3 to 2, 2 to 3)) {
                    val inside = bmp.getPixel(
                        (h.left + dx).toInt(), (h.top + dy).toInt(),
                    )
                    assertEquals(
                        "pulgar oscuro=$dark on=$checked: la muestra " +
                            "($dx,$dy) debe pertenecer al pulgar (no disco)",
                        thumbArgb,
                        nearest(inside, surface, expectedTrack, thumbArgb),
                    )
                }
                val thumbTip = bmp.getPixel(
                    (h.left + 1).toInt(), (h.top + 1).toInt(),
                )
                assertTrue(
                    "pulgar oscuro=$dark on=$checked: la esquina (1,1) debe " +
                        "mostrar la pista, no el pulgar (esquinas " +
                        "redondeadas): $thumbTip",
                    closerTo(thumbTip, expectedTrack, thumbArgb),
                )

                // --- Desplazamiento: el pulgar no está en la posición opuesta ---
                val midY = ((t.top + t.bottom) / 2).toInt()
                val offCenterX = (t.left + 16f).toInt()
                val onCenterX = (t.right - 16f).toInt()
                val vacated = bmp.getPixel(
                    (if (checked) offCenterX else onCenterX), midY,
                )
                assertEquals(
                    "pulgar oscuro=$dark on=$checked: la posición opuesta " +
                        "debe mostrar la pista, no el pulgar",
                    expectedTrack,
                    nearest(vacated, surface, expectedTrack, thumbArgb),
                )
            }
        }
    }

    @Test
    fun switch_inactive_track_meets_functional_contrast_in_both_themes() {
        // La pista inactiva es la que debe identificarse sin ambigüedad
        // (ADR-0051): `functionalBorder` ≥ 3:1 sobre toda superficie.
        // La pista activa conserva `brandFill` (piccolo de Moon): el estado
        // on/off ya lo codifica la posición del pulgar y el color de marca
        // es autoridad de ADR-0044.
        for (scheme in listOf(LightAqColorScheme, DarkAqColorScheme)) {
            for (surface in listOf(scheme.surface, scheme.surfaceSecondary, scheme.background)) {
                assertTrue(
                    "pista inactiva/superficie = " +
                        "${contrastRatio(scheme.functionalBorder, surface)}",
                    contrastRatio(scheme.functionalBorder, surface) >= 3.0,
                )
            }
        }
    }

    private fun channelDistancePx(a: Int, b: Int): Int =
        kotlin.math.abs(((a shr 0) and 0xFF) - ((b shr 0) and 0xFF)) +
            kotlin.math.abs(((a shr 8) and 0xFF) - ((b shr 8) and 0xFF)) +
            kotlin.math.abs(((a shr 16) and 0xFF) - ((b shr 16) and 0xFF))

    private fun assertColorNear(actual: Int, expected: Color, what: String) {
        val exp = expected.toArgb()
        // Tolerancia por la sombra del pulgar (alpha oscurece levemente las
        // muestras cercanas) y el antialiasing del borde del estadio.
        assertTrue(
            "$what: $actual != $exp",
            channelDistancePx(actual, exp) <= ColorTolerance,
        )
    }

    private companion object {
        const val CanvasTag = "switch-canvas"
        val CanvasWidth = 160.dp
        val CanvasHeight = 120.dp
        const val ColorTolerance = 60
    }
}
