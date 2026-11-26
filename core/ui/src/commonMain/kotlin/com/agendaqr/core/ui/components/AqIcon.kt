package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.icons.PhosphorProductVectors01
import com.agendaqr.core.ui.icons.PhosphorProductVectors02
import com.agendaqr.core.ui.icons.PhosphorProductVectors03
import com.agendaqr.core.ui.icons.PhosphorProductVectors04
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

/** Stable product concepts mapped exclusively to Phosphor Icons v2.0.8. */
object AqIcons {
    val Search: ImageVector get() = PhosphorProductVectors01.MagnifyingGlass
    val Add: ImageVector get() = PhosphorProductVectors01.Plus
    val Register: ImageVector get() = PhosphorProductVectors01.Receipt
    val Favorite: ImageVector get() = PhosphorProductVectors01.Star
    val Context: ImageVector get() = PhosphorProductVectors01.Folder
    val Back: ImageVector get() = PhosphorProductVectors01.ArrowLeft
    val Cancel: ImageVector get() = PhosphorProductVectors01.XCircle
    /** X simple sin círculo: cierre de banner/diálogo. */
    val Close: ImageVector get() = PhosphorProductVectors01.X
    val More: ImageVector get() = PhosphorProductVectors01.DotsThree
    val Home: ImageVector get() = PhosphorProductVectors02.House
    val Gallery: ImageVector get() = PhosphorProductVectors02.Image
    val Camera: ImageVector get() = PhosphorProductVectors02.Camera
    val Info: ImageVector get() = PhosphorProductVectors02.Info
    val Warning: ImageVector get() = PhosphorProductVectors02.Warning
    val CircleCheck: ImageVector get() = PhosphorProductVectors02.CheckCircle
    val Check: ImageVector get() = PhosphorProductVectors02.Check
    val QrCode: ImageVector get() = PhosphorProductVectors02.QrCode
    val AlertCircle: ImageVector get() = PhosphorProductVectors02.WarningCircle
    val Inbox: ImageVector get() = PhosphorProductVectors03.Tray
    val ScanLine: ImageVector get() = PhosphorProductVectors03.Scan
    val Income: ImageVector get() = PhosphorProductVectors03.ArrowDown
    val Expense: ImageVector get() = PhosphorProductVectors03.ArrowUp
    val Attachment: ImageVector get() = PhosphorProductVectors03.Paperclip
    val Store: ImageVector get() = PhosphorProductVectors03.Basket
    val Calendar: ImageVector get() = PhosphorProductVectors03.CalendarBlank
    val Clock: ImageVector get() = PhosphorProductVectors03.Clock
    val ChevronRight: ImageVector get() = PhosphorProductVectors03.CaretRight
    val ArrowDown: ImageVector get() = PhosphorProductVectors03.ArrowDown
    val ArrowUp: ImageVector get() = PhosphorProductVectors03.ArrowUp
    val ShieldCheck: ImageVector get() = PhosphorProductVectors04.ShieldCheck
    val Wallet: ImageVector get() = PhosphorProductVectors04.Wallet
    val Lock: ImageVector get() = PhosphorProductVectors04.LockKey
    /** Backspace en campos; no equivale a Trash. */
    val Erase: ImageVector get() = PhosphorProductVectors04.Backspace
    val NoteEdit: ImageVector get() = PhosphorProductVectors04.NotePencil
    val Edit: ImageVector get() = PhosphorProductVectors04.PencilSimple
    val Trash: ImageVector get() = PhosphorProductVectors04.Trash
    val Share: ImageVector get() = PhosphorProductVectors04.ShareNetwork
    val Copy: ImageVector get() = PhosphorProductVectors04.Copy
    val Maximize: ImageVector get() = PhosphorProductVectors04.ArrowsOut
}
