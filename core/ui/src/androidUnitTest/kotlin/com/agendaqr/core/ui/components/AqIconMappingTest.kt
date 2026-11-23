package com.agendaqr.core.ui.components

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AqIconMappingTest {

    @Test
    fun product_icon_concepts_resolve_to_moon_vectors() {
        val vectors = listOf(
            AqIcons.Search to "GenericSearch",
            AqIcons.Add to "ControlsPlus",
            AqIcons.Register to "TravelBill",
            AqIcons.Favorite to "GenericStar",
            AqIcons.Context to "FilesFolderClosed",
            AqIcons.Back to "ArrowsLeftShort",
            AqIcons.Cancel to "NotificationsError",
            AqIcons.Close to "ControlsClose",
            AqIcons.More to "Other3DotsHorizontal",
            AqIcons.Home to "GenericHome",
            AqIcons.Gallery to "GenericPicture",
            AqIcons.Camera to "MediaPhoto",
            AqIcons.Info to "GenericInfo",
            AqIcons.Warning to "NotificationsAlert",
            AqIcons.CircleCheck to "GenericCheckRounded",
            AqIcons.Check to "GenericCheckAlternative",
            AqIcons.QrCode to "SecurityQrCode",
            AqIcons.AlertCircle to "NotificationsAlert",
            AqIcons.Inbox to "MailBox",
            AqIcons.ScanLine to "FilesScan",
            AqIcons.Income to "ArrowsDown",
            AqIcons.Expense to "ArrowsUp",
            AqIcons.Attachment to "TextAttach",
            AqIcons.Store to "ShopBasket",
            AqIcons.Calendar to "TimeCalendar",
            AqIcons.Clock to "TimeClock",
            AqIcons.ChevronRight to "ControlsChevronRight",
            AqIcons.ShieldCheck to "SecurityShieldSecured",
            AqIcons.Wallet to "ShopWallet",
            AqIcons.Lock to "SecurityLock",
            AqIcons.Erase to "ControlsClear",
            AqIcons.NoteEdit to "GenericEdit",
            AqIcons.Edit to "GenericEdit",
            AqIcons.Trash to "GenericDelete",
            AqIcons.Share to "GenericShareIos",
            AqIcons.Copy to "FilesCopy",
            AqIcons.Maximize to "ControlsFullScreen",
        )

        assertEquals(37, vectors.size)
        assertEquals(34, vectors.map { it.first }.distinct().count())
        assertTrue(vectors.all { (vector, expectedName) -> vector.name == expectedName })
        assertTrue(vectors.all { it.first.defaultWidth == it.first.defaultHeight })
    }
}
