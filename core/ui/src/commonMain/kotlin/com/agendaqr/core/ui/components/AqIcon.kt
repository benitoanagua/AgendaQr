package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.icons.MoonIconVectors
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

/** Stable product concepts mapped to the pinned Moon Icons 10.7.4 vectors. */
object AqIcons {
    val Search: ImageVector get() = MoonIconVectors.Search
    val Add: ImageVector get() = MoonIconVectors.Add
    val Register: ImageVector get() = MoonIconVectors.Bill
    val Favorite: ImageVector get() = MoonIconVectors.Star
    val Context: ImageVector get() = MoonIconVectors.Folder
    val Back: ImageVector get() = MoonIconVectors.ArrowLeft
    val Cancel: ImageVector get() = MoonIconVectors.CircleX
    /** X simple sin círculo: cierre de banner/diálogo (acción, no estado). */
    val Close: ImageVector get() = MoonIconVectors.Close
    val More: ImageVector get() = MoonIconVectors.MoreHorizontal
    val Home: ImageVector get() = MoonIconVectors.Home
    val Gallery: ImageVector get() = MoonIconVectors.Picture
    val Camera: ImageVector get() = MoonIconVectors.Camera
    val Info: ImageVector get() = MoonIconVectors.Info
    val Warning: ImageVector get() = MoonIconVectors.Alert
    val CircleCheck: ImageVector get() = MoonIconVectors.CheckRounded
    /** Check simple: indicador de selección en chips y opciones. */
    val Check: ImageVector get() = MoonIconVectors.Check
    val QrCode: ImageVector get() = MoonIconVectors.QrCode
    /** Signo de alerta en círculo: icono de severidad de errores. */
    val AlertCircle: ImageVector get() = MoonIconVectors.Alert
    val Inbox: ImageVector get() = MoonIconVectors.Box
    val ScanLine: ImageVector get() = MoonIconVectors.Scan
    val Income: ImageVector get() = MoonIconVectors.ArrowDown
    val Expense: ImageVector get() = MoonIconVectors.ArrowUp
    val Attachment: ImageVector get() = MoonIconVectors.Attach
    val Store: ImageVector get() = MoonIconVectors.Basket
    val Calendar: ImageVector get() = MoonIconVectors.Calendar
    val Clock: ImageVector get() = MoonIconVectors.Clock
    val ChevronRight: ImageVector get() = MoonIconVectors.ChevronRight
    val ArrowDown: ImageVector get() = MoonIconVectors.ArrowDown
    val ArrowUp: ImageVector get() = MoonIconVectors.ArrowUp
    val ShieldCheck: ImageVector get() = MoonIconVectors.ShieldSecured
    val Wallet: ImageVector get() = MoonIconVectors.Wallet
    val Lock: ImageVector get() = MoonIconVectors.Lock
    /** Borrado de texto en campos (retroceso), NO el glifo de papelera. */
    val Erase: ImageVector get() = MoonIconVectors.Clear
    val NoteEdit: ImageVector get() = MoonIconVectors.Edit
    val Edit: ImageVector get() = MoonIconVectors.Edit
    val Trash: ImageVector get() = MoonIconVectors.Delete
    val Share: ImageVector get() = MoonIconVectors.Share
    val Copy: ImageVector get() = MoonIconVectors.Copy
    val Maximize: ImageVector get() = MoonIconVectors.FullScreen
}
