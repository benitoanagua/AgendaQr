package com.agendaqr.core.ui.components

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AmIconMappingTest {

    @Test
    fun product_icon_concepts_resolve_to_tdesign_vectors() {
        val vectors = listOf(
            AmIcons.Search to "search",
            AmIcons.Add to "add",
            AmIcons.Register to "bill",
            AmIcons.Favorite to "star",
            AmIcons.Context to "folder",
            AmIcons.Back to "arrow-left",
            AmIcons.Cancel to "close",
            AmIcons.More to "more",
            AmIcons.Home to "home",
            AmIcons.Gallery to "image",
            AmIcons.Camera to "camera",
            AmIcons.Info to "info-circle",
            AmIcons.Warning to "error-triangle",
            AmIcons.CircleCheck to "check-circle",
            AmIcons.QrCode to "qrcode",
            AmIcons.SearchX to "search-error",
            AmIcons.Inbox to "queue",
            AmIcons.ImageOff to "image-off",
            AmIcons.ScanLine to "scan",
            AmIcons.Income to "arrow-left-down",
            AmIcons.Expense to "arrow-right-up",
            AmIcons.Attachment to "attach",
            AmIcons.Store to "store",
            AmIcons.Calendar to "calendar",
            AmIcons.Clock to "time",
            AmIcons.ChevronRight to "chevron-right",
            AmIcons.ShieldCheck to "secured",
            AmIcons.Wallet to "wallet",
            AmIcons.Lock to "lock-on",
            AmIcons.Erase to "delete",
            AmIcons.NoteEdit to "edit",
            AmIcons.Edit to "edit",
            AmIcons.Trash to "delete",
            AmIcons.Share to "share",
            AmIcons.Copy to "file-copy",
            AmIcons.Maximize to "fullscreen",
        )

        assertEquals(36, vectors.size)
        assertTrue(vectors.all { (vector, expectedName) -> vector.name == expectedName })
    }
}
