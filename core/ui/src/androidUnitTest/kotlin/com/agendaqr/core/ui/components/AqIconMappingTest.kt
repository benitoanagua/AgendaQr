package com.agendaqr.core.ui.components

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AqIconMappingTest {

    @Test
    fun every_product_icon_concept_resolves_to_the_pinned_phosphor_catalog() {
        val vectors = listOf(
            AqIcons.Search to "PhosphorMagnifyingGlass",
            AqIcons.Add to "PhosphorPlus",
            AqIcons.Register to "PhosphorReceipt",
            AqIcons.Favorite to "PhosphorStar",
            AqIcons.Context to "PhosphorFolder",
            AqIcons.Back to "PhosphorArrowLeft",
            AqIcons.Cancel to "PhosphorXCircle",
            AqIcons.Close to "PhosphorX",
            AqIcons.More to "PhosphorDotsThree",
            AqIcons.Home to "PhosphorHouse",
            AqIcons.Gallery to "PhosphorImage",
            AqIcons.Camera to "PhosphorCamera",
            AqIcons.Info to "PhosphorInfo",
            AqIcons.Warning to "PhosphorWarning",
            AqIcons.CircleCheck to "PhosphorCheckCircle",
            AqIcons.Check to "PhosphorCheck",
            AqIcons.QrCode to "PhosphorQrCode",
            AqIcons.AlertCircle to "PhosphorWarningCircle",
            AqIcons.Inbox to "PhosphorTray",
            AqIcons.ScanLine to "PhosphorScan",
            AqIcons.Income to "PhosphorArrowDown",
            AqIcons.Expense to "PhosphorArrowUp",
            AqIcons.Attachment to "PhosphorPaperclip",
            AqIcons.Store to "PhosphorBasket",
            AqIcons.Calendar to "PhosphorCalendarBlank",
            AqIcons.Clock to "PhosphorClock",
            AqIcons.ChevronRight to "PhosphorCaretRight",
            AqIcons.ArrowDown to "PhosphorArrowDown",
            AqIcons.ArrowUp to "PhosphorArrowUp",
            AqIcons.ShieldCheck to "PhosphorShieldCheck",
            AqIcons.Wallet to "PhosphorWallet",
            AqIcons.Lock to "PhosphorLockKey",
            AqIcons.Erase to "PhosphorBackspace",
            AqIcons.NoteEdit to "PhosphorNotePencil",
            AqIcons.Edit to "PhosphorPencilSimple",
            AqIcons.Trash to "PhosphorTrash",
            AqIcons.Share to "PhosphorShareNetwork",
            AqIcons.Copy to "PhosphorCopy",
            AqIcons.Maximize to "PhosphorArrowsOut",
        )

        assertEquals(39, vectors.size)
        assertTrue(vectors.all { (vector, expectedName) -> vector.name == expectedName })
        assertEquals(37, vectors.map { it.first.name }.distinct().size)
        assertTrue(vectors.all { it.first.defaultWidth == 256.dp && it.first.defaultHeight == 256.dp })
    }

    @Test
    fun switch_state_glyphs_use_phosphor_bold_at_thumb_size() {
        assertEquals("PhosphorCheckBold", AqPhosphorIcon.Check.vectorForSize(12.dp).name)
        assertEquals("PhosphorXBold", AqPhosphorIcon.X.vectorForSize(12.dp).name)
    }
}
