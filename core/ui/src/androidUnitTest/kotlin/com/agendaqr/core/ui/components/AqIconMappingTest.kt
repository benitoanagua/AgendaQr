package com.agendaqr.core.ui.components

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AqIconMappingTest {

    @Test
    fun product_icon_concepts_resolve_to_polaris_vectors() {
        val vectors = listOf(
            AqIcons.Search to "SearchIcon",
            AqIcons.Add to "PlusIcon",
            AqIcons.Register to "BillIcon",
            AqIcons.Favorite to "StarIcon",
            AqIcons.Context to "FolderIcon",
            AqIcons.Back to "ArrowLeftIcon",
            AqIcons.Cancel to "XCircleIcon",
            AqIcons.Close to "XIcon",
            AqIcons.More to "MenuHorizontalIcon",
            AqIcons.Home to "HomeIcon",
            AqIcons.Gallery to "ImageIcon",
            AqIcons.Camera to "CameraIcon",
            AqIcons.Info to "InfoIcon",
            AqIcons.Warning to "AlertTriangleIcon",
            AqIcons.CircleCheck to "CheckCircleIcon",
            AqIcons.Check to "CheckIcon",
            AqIcons.QrCode to "ShopcodesIcon",
            AqIcons.SearchX to "AlertCircleIcon",
            AqIcons.AlertCircle to "AlertCircleIcon",
            AqIcons.Inbox to "ArchiveIcon",
            AqIcons.ImageOff to "ImageAltIcon",
            AqIcons.ScanLine to "BarcodeIcon",
            AqIcons.Income to "ArrowDownIcon",
            AqIcons.Expense to "ArrowUpIcon",
            AqIcons.Attachment to "AttachmentIcon",
            AqIcons.Store to "StoreIcon",
            AqIcons.Calendar to "CalendarIcon",
            AqIcons.Clock to "ClockIcon",
            AqIcons.ChevronRight to "ChevronRightIcon",
            AqIcons.ShieldCheck to "ShieldCheckMarkIcon",
            AqIcons.Wallet to "WalletIcon",
            AqIcons.Lock to "LockIcon",
            AqIcons.Erase to "BackspaceIcon",
            AqIcons.NoteEdit to "EditIcon",
            AqIcons.Edit to "EditIcon",
            AqIcons.Trash to "DeleteIcon",
            AqIcons.Share to "ShareIcon",
            AqIcons.Copy to "DuplicateIcon",
            AqIcons.Maximize to "MaximizeIcon",
        )

        assertEquals(39, vectors.size)
        assertEquals(37, vectors.map { it.first }.distinct().count())
        assertTrue(vectors.all { (vector, expectedName) -> vector.name == expectedName })
        assertTrue(vectors.all { it.first.defaultWidth == it.first.defaultHeight })
    }
}
