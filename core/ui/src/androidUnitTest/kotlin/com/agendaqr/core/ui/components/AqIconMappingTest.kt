package com.agendaqr.core.ui.components

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AqIconMappingTest {

    @Test
    fun product_icon_concepts_resolve_to_tdesign_vectors() {
        val vectors = listOf(
            AqIcons.Search to "search",
            AqIcons.Add to "add",
            AqIcons.Register to "bill",
            AqIcons.Favorite to "star",
            AqIcons.Context to "folder",
            AqIcons.Back to "arrow-left",
            AqIcons.Cancel to "close",
            AqIcons.More to "more",
            AqIcons.Home to "home",
            AqIcons.Gallery to "image",
            AqIcons.Camera to "camera",
            AqIcons.Info to "info-circle",
            AqIcons.Warning to "error-triangle",
            AqIcons.CircleCheck to "check-circle",
            AqIcons.QrCode to "qrcode",
            AqIcons.SearchX to "search-error",
            AqIcons.Inbox to "queue",
            AqIcons.ImageOff to "image-off",
            AqIcons.ScanLine to "scan",
            AqIcons.Income to "arrow-left-down",
            AqIcons.Expense to "arrow-right-up",
            AqIcons.Attachment to "attach",
            AqIcons.Store to "store",
            AqIcons.Calendar to "calendar",
            AqIcons.Clock to "time",
            AqIcons.ChevronRight to "chevron-right",
            AqIcons.ShieldCheck to "secured",
            AqIcons.Wallet to "wallet",
            AqIcons.Lock to "lock-on",
            AqIcons.Erase to "delete",
            AqIcons.NoteEdit to "edit",
            AqIcons.Edit to "edit",
            AqIcons.Trash to "delete",
            AqIcons.Share to "share",
            AqIcons.Copy to "file-copy",
            AqIcons.Maximize to "fullscreen",
        )

        assertEquals(36, vectors.size)
        assertTrue(vectors.all { (vector, expectedName) -> vector.name == expectedName })
    }
}
