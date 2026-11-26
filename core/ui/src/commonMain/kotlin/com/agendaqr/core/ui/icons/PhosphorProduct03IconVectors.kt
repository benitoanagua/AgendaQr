package com.agendaqr.core.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** Phosphor Icons official SVG paths, @phosphor-icons/core v2.0.8, MIT. */
internal object PhosphorProductVectors03 {
    // Source: assets/regular/tray.svg (Phosphor core v2.0.8, MIT).
    val Tray: ImageVector by lazy { icon("PhosphorTray", listOf(
        """M208,32H48A16,16,0,0,0,32,48V208a16,16,0,0,0,16,16H208a16,16,0,0,0,16-16V48A16,16,0,0,0,208,32Zm0,16V152h-28.7A15.86,15.86,0,0,0,168,156.69L148.69,176H107.31L88,156.69A15.86,15.86,0,0,0,76.69,152H48V48Zm0,160H48V168H76.69L96,187.31A15.86,15.86,0,0,0,107.31,192h41.38A15.86,15.86,0,0,0,160,187.31L179.31,168H208v40Z""",
    )) }
    // Source: assets/regular/scan.svg (Phosphor core v2.0.8, MIT).
    val Scan: ImageVector by lazy { icon("PhosphorScan", listOf(
        """M224,40V80a8,8,0,0,1-16,0V48H176a8,8,0,0,1,0-16h40A8,8,0,0,1,224,40ZM80,208H48V176a8,8,0,0,0-16,0v40a8,8,0,0,0,8,8H80a8,8,0,0,0,0-16Zm136-40a8,8,0,0,0-8,8v32H176a8,8,0,0,0,0,16h40a8,8,0,0,0,8-8V176A8,8,0,0,0,216,168ZM40,88a8,8,0,0,0,8-8V48H80a8,8,0,0,0,0-16H40a8,8,0,0,0-8,8V80A8,8,0,0,0,40,88Zm128,96H88a16,16,0,0,1-16-16V88A16,16,0,0,1,88,72h80a16,16,0,0,1,16,16v80A16,16,0,0,1,168,184ZM88,168h80V88H88Z""",
    )) }
    // Source: assets/regular/arrow-down.svg (Phosphor core v2.0.8, MIT).
    val ArrowDown: ImageVector by lazy { icon("PhosphorArrowDown", listOf(
        """M205.66,149.66l-72,72a8,8,0,0,1-11.32,0l-72-72a8,8,0,0,1,11.32-11.32L120,196.69V40a8,8,0,0,1,16,0V196.69l58.34-58.35a8,8,0,0,1,11.32,11.32Z""",
    )) }
    // Source: assets/regular/arrow-up.svg (Phosphor core v2.0.8, MIT).
    val ArrowUp: ImageVector by lazy { icon("PhosphorArrowUp", listOf(
        """M205.66,117.66a8,8,0,0,1-11.32,0L136,59.31V216a8,8,0,0,1-16,0V59.31L61.66,117.66a8,8,0,0,1-11.32-11.32l72-72a8,8,0,0,1,11.32,0l72,72A8,8,0,0,1,205.66,117.66Z""",
    )) }
    // Source: assets/regular/paperclip.svg (Phosphor core v2.0.8, MIT).
    val Paperclip: ImageVector by lazy { icon("PhosphorPaperclip", listOf(
        """M209.66,122.34a8,8,0,0,1,0,11.32l-82.05,82a56,56,0,0,1-79.2-79.21L147.67,35.73a40,40,0,1,1,56.61,56.55L105,193A24,24,0,1,1,71,159L154.3,74.38A8,8,0,1,1,165.7,85.6L82.39,170.31a8,8,0,1,0,11.27,11.36L192.93,81A24,24,0,1,0,159,47L59.76,147.68a40,40,0,1,0,56.53,56.62l82.06-82A8,8,0,0,1,209.66,122.34Z""",
    )) }
    // Source: assets/regular/basket.svg (Phosphor core v2.0.8, MIT).
    val Basket: ImageVector by lazy { icon("PhosphorBasket", listOf(
        """M136,120v56a8,8,0,0,1-16,0V120a8,8,0,0,1,16,0ZM239.86,98.11,226,202.12A16,16,0,0,1,210.13,216H45.87A16,16,0,0,1,30,202.12l-13.87-104A16,16,0,0,1,32,80H68.37L122,18.73a8,8,0,0,1,12,0L187.63,80H224a16,16,0,0,1,15.85,18.11ZM89.63,80h76.74L128,36.15ZM224,96H32L45.87,200H210.13Zm-51.16,23.2-5.6,56A8,8,0,0,0,174.4,184a7.44,7.44,0,0,0,.81,0,8,8,0,0,0,7.95-7.2l5.6-56a8,8,0,0,0-15.92-1.6Zm-89.68,0a8,8,0,0,0-15.92,1.6l5.6,56a8,8,0,0,0,8,7.2,7.44,7.44,0,0,0,.81,0,8,8,0,0,0,7.16-8.76Z""",
    )) }
    // Source: assets/regular/calendar-blank.svg (Phosphor core v2.0.8, MIT).
    val CalendarBlank: ImageVector by lazy { icon("PhosphorCalendarBlank", listOf(
        """M208,32H184V24a8,8,0,0,0-16,0v8H88V24a8,8,0,0,0-16,0v8H48A16,16,0,0,0,32,48V208a16,16,0,0,0,16,16H208a16,16,0,0,0,16-16V48A16,16,0,0,0,208,32ZM72,48v8a8,8,0,0,0,16,0V48h80v8a8,8,0,0,0,16,0V48h24V80H48V48ZM208,208H48V96H208V208Z""",
    )) }
    // Source: assets/regular/clock.svg (Phosphor core v2.0.8, MIT).
    val Clock: ImageVector by lazy { icon("PhosphorClock", listOf(
        """M128,24A104,104,0,1,0,232,128,104.11,104.11,0,0,0,128,24Zm0,192a88,88,0,1,1,88-88A88.1,88.1,0,0,1,128,216Zm64-88a8,8,0,0,1-8,8H128a8,8,0,0,1-8-8V72a8,8,0,0,1,16,0v48h48A8,8,0,0,1,192,128Z""",
    )) }
    // Source: assets/regular/caret-right.svg (Phosphor core v2.0.8, MIT).
    val CaretRight: ImageVector by lazy { icon("PhosphorCaretRight", listOf(
        """M181.66,133.66l-80,80a8,8,0,0,1-11.32-11.32L164.69,128,90.34,53.66a8,8,0,0,1,11.32-11.32l80,80A8,8,0,0,1,181.66,133.66Z""",
    )) }

    private fun icon(name: String, paths: List<String>): ImageVector = ImageVector.Builder(
        name = name, defaultWidth = 256.dp, defaultHeight = 256.dp,
        viewportWidth = 256f, viewportHeight = 256f,
    ).apply {
        paths.forEach { data -> addPath(
            pathData = PathParser().parsePathString(data).toNodes(),
            pathFillType = PathFillType.NonZero, fill = SolidColor(Color.Black),
        ) }
    }.build()
}