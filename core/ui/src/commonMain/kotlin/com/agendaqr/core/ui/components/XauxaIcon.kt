package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.size
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Ellipsis
import com.composables.icons.lucide.Folder
import com.composables.icons.lucide.Image
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.TriangleAlert
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Receipt
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.X

/**
 * V1.1 (ADR-0005, spec §12 Iconografía): único punto de entrada de
 * iconografía de producto. Set: Lucide (ISC — ver
 * `docs/05-design-system/06-licencias-terceros.md`), glifos de línea de
 * trazo uniforme, un solo color, sin relleno. Concesión registrada en
 * ADR-0005 (Puntos abiertos): Lucide usa terminales redondeadas, menos
 * angulosas que el Metro original; se acepta conscientemente.
 *
 * Reglas (spec §11/§12):
 * - TODO icono interactivo lleva su etiqueta de texto visible compuesta
 *   por el llamador (botón/fila/app bar); este componente jamás dibuja un
 *   icono interactivo suelto como única señal.
 * - [contentDescription] nulo = icono decorativo; se excluye por completo
 *   de la accesibilidad ([clearAndSetSemantics]).
 * - Nunca numeros dp literales fuera de `XauxaTokens.kt` (gate
 *   verify-xauxa.sh): [size] viene por defecto de [XauxaMetrics.IconSize].
 *
 * El set Material de iconos queda prohibido en producto (invariante del
 * guard del design system): este archivo es el único lugar que toca
 * `com.composables.icons.lucide`.
 */
@Composable
fun XauxaIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = XauxaMetrics.IconSize,
    tint: Color = XauxaColor.TextPrimary,
) {
    val semantics = if (contentDescription == null) {
        Modifier.clearAndSetSemantics { }
    } else {
        Modifier
    }
    androidx.compose.material3.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(size).then(semantics),
        tint = tint,
    )
}

/**
 * Mapeo mínimo de la spec V1.1 (§12 Iconografía), confirmado contra el
 * catálogo real de Lucide 1.1.0 (`com.composables:icons-lucide`): todos
 * existen — ningún pendiente de sustitución. Fuera de esta lista no se
 * añaden iconos sin decisión (los iconos por fila de lista no forman parte
 * de Metro — spec §12).
 */
object XauxaIcons {
    /** buscar. */
    val Search: ImageVector get() = Lucide.Search
    /** añadir. */
    val Add: ImageVector get() = Lucide.Plus
    /** registrar (icono de recibo). */
    val Register: ImageVector get() = Lucide.Receipt
    /** favorito. */
    val Favorite: ImageVector get() = Lucide.Star
    /** contexto (carpeta). `Layers` sigue reservada como alternativa de la
     * spec ("Folder o Layers"); se fija Folder para un único glifo por
     * concepto — si producto prefiere Layers es un cambio de una línea
     * registrado en ADR-0005 (Puntos abiertos). */
    val Context: ImageVector get() = Lucide.Folder
    /** atrás (flecha de la app bar; requerida en iOS, redundante en
     * Android donde el Back del sistema se mantiene). */
    val Back: ImageVector get() = Lucide.ArrowLeft
    /** guardar. */
    val Save: ImageVector get() = Lucide.Check
    /** cancelar. */
    val Cancel: ImageVector get() = Lucide.X
    /** menú "…" de la app bar. */
    val More: ImageVector get() = Lucide.Ellipsis
    /** Galería (S02). */
    val Gallery: ImageVector get() = Lucide.Image
    /** Cámara (S02/S03). */
    val Camera: ImageVector get() = Lucide.Camera

    // Glifos de ESTADO (auditoría a11y, 2026-10-08): el estado nunca se
    // comunica solo por color (§11) — banner/toast/inline llevan icono por
    // tono. Aprobados en esta pasada; no ampliar sin decisión.
    /** Info (tono informativo). */
    val Info: ImageVector get() = Lucide.Info
    /** Aviso (tono de advertencia). */
    val Warning: ImageVector get() = Lucide.TriangleAlert

    // La spec menciona como alternativa "lápiz sobre cuadrado" para
    // registrar y "Folder o Layers" para contexto: se fijaron `Receipt` y
    // `Folder` (un único glifo por concepto); `SquarePen`/`Layers` quedan
    // disponibles en el set si producto decide cambiarlos (Puntos abiertos
    // de ADR-0005). Fuera de la lista mínima no se añaden iconos sin
    // decisión (los iconos por fila de lista no forman parte de Metro —
    // spec §12).
}
