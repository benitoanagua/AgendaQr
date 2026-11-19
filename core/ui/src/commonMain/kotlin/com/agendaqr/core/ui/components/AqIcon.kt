package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.icons.PolarisIconVectors
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics

/**
 * Product icon entry point. Interactive icons always have a visible text
 * label composed by the caller; a null description marks a decorative icon.
 */
@Composable
fun AqIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = AqMetrics.IconSize,
    tint: Color = AqColor.TextPrimary,
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

/** Stable product concepts mapped to the pinned Polaris Icons 9.3.1 vectors. */
object AqIcons {
    val Search: ImageVector get() = PolarisIconVectors.Search
    val Add: ImageVector get() = PolarisIconVectors.Plus
    val Register: ImageVector get() = PolarisIconVectors.Bill
    val Favorite: ImageVector get() = PolarisIconVectors.Star
    val Context: ImageVector get() = PolarisIconVectors.Folder
    val Back: ImageVector get() = PolarisIconVectors.ArrowLeft
    val Cancel: ImageVector get() = PolarisIconVectors.XCircle
    /** X simple sin círculo: cierre de banner/diálogo (acción, no estado). */
    val Close: ImageVector get() = PolarisIconVectors.X
    val More: ImageVector get() = PolarisIconVectors.MenuHorizontal
    val Home: ImageVector get() = PolarisIconVectors.Home
    val Gallery: ImageVector get() = PolarisIconVectors.Image
    val Camera: ImageVector get() = PolarisIconVectors.Camera
    val Info: ImageVector get() = PolarisIconVectors.Info
    val Warning: ImageVector get() = PolarisIconVectors.AlertTriangle
    val CircleCheck: ImageVector get() = PolarisIconVectors.CheckCircle
    /** Check simple: indicador de selección en chips y opciones. */
    val Check: ImageVector get() = PolarisIconVectors.Check
    val QrCode: ImageVector get() = PolarisIconVectors.Shopcodes
    val SearchX: ImageVector get() = PolarisIconVectors.AlertCircle
    /** Signo de alerta en círculo: icono de severidad de errores. */
    val AlertCircle: ImageVector get() = PolarisIconVectors.AlertCircle
    val Inbox: ImageVector get() = PolarisIconVectors.Archive
    val ImageOff: ImageVector get() = PolarisIconVectors.ImageAlt
    val ScanLine: ImageVector get() = PolarisIconVectors.Barcode
    val Income: ImageVector get() = PolarisIconVectors.ArrowDown
    val Expense: ImageVector get() = PolarisIconVectors.ArrowUp
    val Attachment: ImageVector get() = PolarisIconVectors.Attachment
    val Store: ImageVector get() = PolarisIconVectors.Store
    val Calendar: ImageVector get() = PolarisIconVectors.Calendar
    val Clock: ImageVector get() = PolarisIconVectors.Clock
    val ChevronRight: ImageVector get() = PolarisIconVectors.ChevronRight
    val ArrowDown: ImageVector get() = PolarisIconVectors.ArrowDown
    val ArrowUp: ImageVector get() = PolarisIconVectors.ArrowUp
    val ShieldCheck: ImageVector get() = PolarisIconVectors.ShieldCheckMark
    val Wallet: ImageVector get() = PolarisIconVectors.Wallet
    val Lock: ImageVector get() = PolarisIconVectors.Lock
    /** Borrado de texto en campos (retroceso), NO el glifo de papelera. */
    val Erase: ImageVector get() = PolarisIconVectors.Backspace
    val NoteEdit: ImageVector get() = PolarisIconVectors.Edit
    val Edit: ImageVector get() = PolarisIconVectors.Edit
    val Trash: ImageVector get() = PolarisIconVectors.Delete
    val Share: ImageVector get() = PolarisIconVectors.Share
    val Copy: ImageVector get() = PolarisIconVectors.Duplicate
    val Maximize: ImageVector get() = PolarisIconVectors.Maximize
}
