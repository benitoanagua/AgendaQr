package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.icons.TDesignIconVectors
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics

/**
 * Product icon entry point. Interactive icons always have a visible text
 * label composed by the caller; a null description marks a decorative icon.
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

/** Stable product concepts mapped to TDesign Mobile outline glyphs. */
object AmIcons {
    val Search: ImageVector get() = TDesignIconVectors.Search
    val Add: ImageVector get() = TDesignIconVectors.Add
    val Register: ImageVector get() = TDesignIconVectors.Register
    val Favorite: ImageVector get() = TDesignIconVectors.Favorite
    val Context: ImageVector get() = TDesignIconVectors.Context
    val Back: ImageVector get() = TDesignIconVectors.Back
    val Cancel: ImageVector get() = TDesignIconVectors.Cancel
    val More: ImageVector get() = TDesignIconVectors.More
    val Home: ImageVector get() = TDesignIconVectors.Home
    val Gallery: ImageVector get() = TDesignIconVectors.Gallery
    val Camera: ImageVector get() = TDesignIconVectors.Camera
    val Info: ImageVector get() = TDesignIconVectors.Info
    val Warning: ImageVector get() = TDesignIconVectors.Warning
    val CircleCheck: ImageVector get() = TDesignIconVectors.CircleCheck
    val QrCode: ImageVector get() = TDesignIconVectors.QrCode
    val SearchX: ImageVector get() = TDesignIconVectors.SearchX
    val Inbox: ImageVector get() = TDesignIconVectors.Inbox
    val ImageOff: ImageVector get() = TDesignIconVectors.ImageOff
    val ScanLine: ImageVector get() = TDesignIconVectors.ScanLine
    val Income: ImageVector get() = TDesignIconVectors.Income
    val Expense: ImageVector get() = TDesignIconVectors.Expense
    val Attachment: ImageVector get() = TDesignIconVectors.Attachment
    val Store: ImageVector get() = TDesignIconVectors.Store
    val Calendar: ImageVector get() = TDesignIconVectors.Calendar
    val Clock: ImageVector get() = TDesignIconVectors.Clock
    val ChevronRight: ImageVector get() = TDesignIconVectors.ChevronRight
    val ShieldCheck: ImageVector get() = TDesignIconVectors.ShieldCheck
    val Wallet: ImageVector get() = TDesignIconVectors.Wallet
    val Lock: ImageVector get() = TDesignIconVectors.Lock
    val Erase: ImageVector get() = TDesignIconVectors.Erase
    val NoteEdit: ImageVector get() = TDesignIconVectors.NoteEdit
    val Edit: ImageVector get() = TDesignIconVectors.NoteEdit
    val Trash: ImageVector get() = TDesignIconVectors.Erase
    val Share: ImageVector get() = TDesignIconVectors.Share
    val Copy: ImageVector get() = TDesignIconVectors.Copy
    val Maximize: ImageVector get() = TDesignIconVectors.Maximize
}
