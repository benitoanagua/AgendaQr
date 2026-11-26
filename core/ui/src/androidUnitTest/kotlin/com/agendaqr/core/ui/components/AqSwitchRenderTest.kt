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
    ): Rect {
        val sx = bmp.width / canvas.width
        val sy = bmp.height / canvas.height
        return Rect(
            (track.left - canvas.left) * sx,
            (track.top - canvas.top) * sy,
            (track.right - canvas.left) * sx,
            (track.bottom - canvas.top) * sy,
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
            assertColorNear(
                bmp.getPixel(thumbCenterX.toInt(), midY.toInt()),
                c.expectedThumb,
                "pulgar (dark=${c.dark}, checked=${c.checked})",
            )
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
