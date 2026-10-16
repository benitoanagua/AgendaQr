package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.ArrowDownLeft
import com.composables.icons.lucide.ArrowUpRight
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Database
import com.composables.icons.lucide.Delete
import com.composables.icons.lucide.Ellipsis
import com.composables.icons.lucide.Folder
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Image
import com.composables.icons.lucide.ImageOff
import com.composables.icons.lucide.Inbox
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.NotebookPen
import com.composables.icons.lucide.Paperclip
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.QrCode
import com.composables.icons.lucide.Receipt
import com.composables.icons.lucide.ScanLine
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.SearchX
import com.composables.icons.lucide.Share2
import com.composables.icons.lucide.ShieldCheck
import com.composables.icons.lucide.Star
import com.composables.icons.lucide.Store
import com.composables.icons.lucide.TriangleAlert
import com.composables.icons.lucide.Wallet
import com.composables.icons.lucide.X

/**
 * Iconografía (ADR-0014): único punto de entrada de
 * iconos de producto. Set: Lucide (ISC — ver
 * `docs/05-design-system/06-licencias-terceros.md`), glifos de línea de
 * trazo uniforme, un solo color, sin relleno.
 *
 * Reglas (contrato de accesibilidad):
 * - TODO icono interactivo lleva su etiqueta de texto visible compuesta
 *   por el llamador (botón/fila/barra); este componente jamás dibuja un
 *   icono interactivo suelto como única señal.
 * - [contentDescription] nulo = icono decorativo; se excluye por completo
 *   de la accesibilidad ([clearAndSetSemantics]).
 * - [size] viene por defecto de [AmMetrics.IconSize]: ningún componente
 *   fija tamaños de icono como literales.
 *
 * El set Material de iconos queda prohibido en producto (invariante del
 * gate del sistema de diseño): este archivo es el único lugar que toca
 * `com.composables.icons.lucide`.
 */
@Composable
fun AmIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = AmMetrics.IconSize,
    tint: Color = AmColor.TextPrimary,
) {
    val semantics = if (contentDescription == null) {
        Modifier.clearAndSetSemantics { }
    } else {
        Modifier
    }
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(size).then(semantics),
        tint = tint,
    )
}

/**
 * Mapeo de glifos, confirmado contra el catálogo real de
 * Lucide 1.1.0 (`com.composables:icons-lucide`). Un único glifo por
 * concepto; fuera de esta lista no se añaden iconos sin decisión.
 */
object AmIcons {
    /** buscar. */
    val Search: ImageVector get() = Lucide.Search
    /** añadir. */
    val Add: ImageVector get() = Lucide.Plus
    /** registrar (icono de recibo). */
    val Register: ImageVector get() = Lucide.Receipt
    /** favorito. */
    val Favorite: ImageVector get() = Lucide.Star
    /** contexto (carpeta). */
    val Context: ImageVector get() = Lucide.Folder
    /** atrás (flecha de la barra superior). */
    val Back: ImageVector get() = Lucide.ArrowLeft
    /** cancelar. */
    val Cancel: ImageVector get() = Lucide.X
    /** menú "…" de la barra superior. */
    val More: ImageVector get() = Lucide.Ellipsis
    /** Inicio (destino raíz de la NavigationBar). */
    val Home: ImageVector get() = Lucide.House
    /** Galería. */
    val Gallery: ImageVector get() = Lucide.Image
    /** Cámara. */
    val Camera: ImageVector get() = Lucide.Camera

    // Glifos de ESTADO: el estado nunca se comunica solo por color
    // (contrato de accesibilidad) — banner/inline llevan icono por tono.
    /** Info (tono informativo). */
    val Info: ImageVector get() = Lucide.Info
    /** Aviso (tono de advertencia). */
    val Warning: ImageVector get() = Lucide.TriangleAlert
    /** Confirmación (tono de éxito). */
    val CircleCheck: ImageVector get() = Lucide.CircleCheck

    // Glifos de estados vacíos y de resultado (decorativos: el título y
    // el subtítulo comunican; el glifo solo ancla visualmente).
    /** Código QR (estado vacío de destinos/QR). */
    val QrCode: ImageVector get() = Lucide.QrCode
    /** Sin resultados de búsqueda. */
    val SearchX: ImageVector get() = Lucide.SearchX
    /** Bandeja vacía (sin actividad asociada). */
    val Inbox: ImageVector get() = Lucide.Inbox
    /** Imagen no disponible. */
    val ImageOff: ImageVector get() = Lucide.ImageOff
    /** Escanear (entrada de captura). */
    val ScanLine: ImageVector get() = Lucide.ScanLine
    /** Cobro (entrada de dinero; flecha entrante). */
    val Income: ImageVector get() = Lucide.ArrowDownLeft
    /** Pago (salida de dinero; flecha saliente). */
    val Expense: ImageVector get() = Lucide.ArrowUpRight
    /** Comprobante adjunto (sujeta-papeles). */
    val Attachment: ImageVector get() = Lucide.Paperclip
    // ------------------------------------------------------------------
    // Glifos de filas, formularios y micro-estados (ADR-0015). Un glifo
    // por concepto; decorativos salvo indicación.
    // ------------------------------------------------------------------
    /** Comercio/entidad (fila de persona o entidad). */
    val Store: ImageVector get() = Lucide.Store
    /** Fecha/momento ocurrido. */
    val Calendar: ImageVector get() = Lucide.Calendar
    /** Momento registrado (reloj). */
    val Clock: ImageVector get() = Lucide.Clock
    /** Navegación hacia adelante (fila con detalle propio). */
    val ChevronRight: ImageVector get() = Lucide.ChevronRight
    /** Almacenamiento local (reaseguro de persistencia). */
    val Database: ImageVector get() = Lucide.Database
    /** Seguridad/verificación local. */
    val ShieldCheck: ImageVector get() = Lucide.ShieldCheck
    /** Moneda. */
    val Wallet: ImageVector get() = Lucide.Wallet
    /** Valor automático de solo lectura. */
    val Lock: ImageVector get() = Lucide.Lock
    /** Borrado de importe (⌫). */
    val Erase: ImageVector get() = Lucide.Delete
    /** Nota/concepto (lápiz de nota). */
    val NoteEdit: ImageVector get() = Lucide.NotebookPen
    /** Compartir (salida del contenido). */
    val Share: ImageVector get() = Lucide.Share2
}
