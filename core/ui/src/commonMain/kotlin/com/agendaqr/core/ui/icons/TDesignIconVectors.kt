package com.agendaqr.core.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** TDesign Mobile outline vectors, sourced from tdesign-icons-svg@0.4.7. */
internal object TDesignIconVectors {
    val Search: ImageVector by lazy { icon("search") {
        strokePath("M15.8027 15.8037L21.106 21.107", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M15.8033 15.8033C12.8744 18.7322 8.12563 18.7322 5.1967 15.8033C2.26777 12.8744 2.26777 8.12563 5.1967 5.1967C8.12563 2.26777 12.8744 2.26777 15.8033 5.1967C18.7322 8.12563 18.7322 12.8744 15.8033 15.8033Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Add: ImageVector by lazy { icon("add") {
        strokePath("M12 5L12 19M19 12L5 12", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Register: ImageVector by lazy { icon("bill") {
        strokePath("M5 3H19M5 3V20L7.5 18L9.75 20L12 18L14.25 20L16.5 18L19 20V3M5 3H3M19 3H21", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M9 8H15M10 12H14", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Favorite: ImageVector by lazy { icon("star") {
        strokePath("M12.0001 3.67603L14.1867 9.96648L20.8449 10.1022L15.538 14.1256L17.4665 20.4999L12.0001 16.696L6.5337 20.4999L8.46217 14.1256L3.15527 10.1022L9.81354 9.96648L12.0001 3.67603Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Context: ImageVector by lazy { icon("folder") {
        strokePath("M2 3.5H9L11 6H22V20H2V3.5Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
    } }
    val Back: ImageVector by lazy { icon("arrow-left") {
        strokePath("M11 6.5L5.5 12L11 17.5M6.75 12H19.75", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Cancel: ImageVector by lazy { icon("close") {
        strokePath("M16.9503 7.05029L12.0005 12M12.0005 12L7.05078 16.9498M12.0005 12L16.9503 16.9498M12.0005 12L7.05078 7.05029", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val More: ImageVector by lazy { icon("more") {
        strokePath("M11.5 4H12.5V5H11.5V4Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M11.5 11.5H12.5V12.5H11.5V11.5Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M11.5 19H12.5V20H11.5V19Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Home: ImageVector by lazy { icon("home") {
        strokePath("M9 14H15V21H9V14Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M3 10L12 2.5L21 10V21H3V10Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Gallery: ImageVector by lazy { icon("image") {
        strokePath("M3 16V3H21V21H20M3 16V21H20M3 16L9 10L20 21", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        strokePath("M 13.75 8.25 A 2.0 2.0 0 1 0 17.75 8.25 A 2.0 2.0 0 1 0 13.75 8.25 Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
    } }
    val Camera: ImageVector by lazy { icon("camera") {
        strokePath("M15.5 3H8.5L7 6H2V20H22V6H17L15.5 3Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        strokePath("M16 12.5C16 14.7091 14.2091 16.5 12 16.5C9.79086 16.5 8 14.7091 8 12.5C8 10.2909 9.79086 8.5 12 8.5C14.2091 8.5 16 10.2909 16 12.5Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
    } }
    val Info: ImageVector by lazy { icon("info-circle") {
        strokePath("M2 12C2 6.47715 6.47715 2 12 2C17.5228 2 22 6.47715 22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M12 16.5L12 11M12 7.5L11.9961 7.5L11.9961 7.49609L12 7.49609L12 7.5Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Warning: ImageVector by lazy { icon("error-triangle") {
        strokePath("M12.0003 3L22.2194 20.7H1.78125L12.0003 3Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M12 10.5V14M12 17.5H12.0039V17.5039H12V17.5Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val CircleCheck: ImageVector by lazy { icon("check-circle") {
        strokePath("M2 12C2 6.47715 6.47715 2 12 2C17.5228 2 22 6.47715 22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M16.5 9L10.5 15L7.5 12", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val QrCode: ImageVector by lazy { icon("qrcode") {
        strokePath("M3 14H10V21H3V14Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        strokePath("M3 3H10V10H3V3Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        strokePath("M14 3H21V10H14V3Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        strokePath("M6.5 6.5H6.50391V6.50391H6.5V6.5Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        strokePath("M6.5 17.5H6.50391V17.5039H6.5V17.5Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        strokePath("M17.5 6.5H17.5039V6.50391H17.5V6.5Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        strokePath("M13.9961 13.9961H14V14H13.9961V13.9961Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        strokePath("M16.9961 16.9961H17V17H16.9961V16.9961Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        strokePath("M18.9961 18.9961H19V19H18.9961V18.9961Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        strokePath("M20.9961 13.9961H21V14H20.9961V13.9961Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        strokePath("M20.9961 20.9961H21V21H20.9961V20.9961Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        strokePath("M13.9961 20.9961H14V21H13.9961V20.9961Z", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
    } }
    val SearchX: ImageVector by lazy { icon("search-error") {
        strokePath("M15.8033 15.8033C18.7322 12.8744 18.7322 8.12563 15.8033 5.1967C12.8744 2.26777 8.12563 2.26777 5.1967 5.1967C3.73124 6.66216 2.99901 8.58319 3 10.5039M15.8033 15.8033L21.1066 21.1066M15.8033 15.8033C14.3388 17.2678 12.4194 18 10.5 18", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M1.67188 14.6716L4.5003 17.5001M4.5003 17.5001L7.32873 20.3285M4.5003 17.5001L1.67188 20.3285M4.5003 17.5001L7.32873 14.6716", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Inbox: ImageVector by lazy { icon("queue") {
        strokePath("M6.5 14H10M10 14H13.5M10 14V17.5M10 14V10.5M3 7H17V21H3V7Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M8.00098 3.00195L21 3.00098V17.001M21 17.001V18.001L21.001 18.002L21 17.001Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val ImageOff: ImageVector by lazy { icon("image-off") {
        strokePath("M17.4144 6.58594C17.7763 6.94787 18.0002 7.44787 18.0002 8.00015C18.0002 9.10472 17.1047 10.0002 16.0002 10.0002C15.4479 10.0002 14.9479 9.77629 14.5859 9.41436M3.7 21H21V3.6M12.8315 3.00183L14.9978 3.00183L15 3L12.8315 3.00183ZM12.8315 3.00183L3.00215 3.00004L3.00215 13.6652M3.00215 13.6652L3.00215 15.4982L3 15.5L3.00215 13.6652ZM19.5 20.5L11.5 12.5", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M2 22L22 2", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val ScanLine: ImageVector by lazy { icon("scan") {
        strokePath("M16 21H21V16M8 3H3V8M16 3H21V8M8 21H3V16", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M21 12H3", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Income: ImageVector by lazy { icon("arrow-left-down") {
        strokePath("M15.7782 16.0257H8L8 8.24748M8.87946 15.1462L16.0257 8", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Expense: ImageVector by lazy { icon("arrow-right-up") {
        strokePath("M8.2719 8L16.0501 8V15.7782M15.1706 8.87946L8.02441 16.0257", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Attachment: ImageVector by lazy { icon("attach") {
        strokePath("M20.5062 12.3131L12.728 20.0913C10.3848 22.4344 6.58586 22.4344 4.24271 20.0913C1.89957 17.7481 1.89956 13.9491 4.24271 11.606L12.0209 3.82782C13.583 2.26573 16.1156 2.26573 17.6777 3.82782C19.2398 5.38992 19.2398 7.92258 17.6777 9.48468L9.89957 17.2628C9.11852 18.0439 7.85219 18.0439 7.07114 17.2628C6.29009 16.4818 6.29009 15.2155 7.07114 14.4344L14.1422 7.36336", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Store: ImageVector by lazy { icon("store") {
        strokePath("M4.5 3.5H19.5M20 12.5V20.5M3.5 6.5H20.5L21.5 11.5V12.5H2.5V11.5L3.5 6.5ZM13 12.5V20.5H4V12.5H13Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Calendar: ImageVector by lazy { icon("calendar") {
        strokePath("M3 10H21M3 10V5H21V10M3 10V21H21V10M7 5V1.5M17 5V1.5", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
    } }
    val Clock: ImageVector by lazy { icon("time") {
        strokePath("M2 12C2 6.47715 6.47715 2 12 2C17.5228 2 22 6.47715 22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M12 6.5L12 12L15 15", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val ChevronRight: ImageVector by lazy { icon("chevron-right") {
        strokePath("M9.5 17.5L15 12L9.5 6.5", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val ShieldCheck: ImageVector by lazy { icon("secured") {
        strokePath("M3 4.65V12C3 19.35 12 22.5 12 22.5C12 22.5 21 19.35 21 12V4.65L12 1.5L3 4.65Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M8.17285 11.1716L11.0013 14L16.6581 8.34314", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Wallet: ImageVector by lazy { icon("wallet") {
        strokePath("M19 3H5.00002C3.89546 3 3.00003 3.89542 3.00002 4.99998C3.00001 6.10456 3.89544 7 5.00002 7H18M3 7V6M3 7H19C20.1045 7 21 7.89543 21 9V19C21 20.1046 20.1045 21 19 21H3V7ZM16 14H17", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Lock: ImageVector by lazy { icon("lock-on") {
        strokePath("M4.49951 11H19.4995V21H4.49951V11Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M6.99951 7C6.99951 4.23858 9.23809 2 11.9995 2C14.7609 2 16.9995 4.23858 16.9995 7V11H6.99951V7Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M9.99951 16H13.9995", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Erase: ImageVector by lazy { icon("delete") {
        strokePath("M21 5H3M5 5H19L18.5 22H5.5L5 5ZM8.5 2H15.5V5H8.5V2Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        strokePath("M12 9V18", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val NoteEdit: ImageVector by lazy { icon("edit") {
        strokePath("M14.1048 6.00427L4.78744 15.3216L3.99805 20.0001L8.67652 19.2107L17.9939 9.89336M14.1048 6.00427L17.9939 9.89336M14.1048 6.00427L17.1615 2.94727L21.0506 6.83635L17.9939 9.89336", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Share: ImageVector by lazy { icon("share") {
        strokePath("M7.59863 13.501L15.3929 18.001M7.59863 10.499L15.3929 5.99902M15.3927 5.99855C14.5642 4.56367 15.0559 2.7289 16.4907 1.90047C17.9256 1.07204 19.7604 1.56367 20.5888 2.99855C21.4172 4.43343 20.9256 6.2682 19.4907 7.09662C18.0559 7.92505 16.2211 7.43342 15.3927 5.99855ZM15.3927 18.0005C16.2211 16.5656 18.0559 16.074 19.4907 16.9024C20.9256 17.7309 21.4172 19.5656 20.5888 21.0005C19.7604 22.4354 17.9256 22.927 16.4907 22.0986C15.0559 21.2701 14.5642 19.4354 15.3927 18.0005ZM7.59858 10.4985C8.42701 11.9334 7.93538 13.7682 6.5005 14.5966C5.06562 15.4251 3.23085 14.9334 2.40242 13.4985C1.574 12.0637 2.06562 10.2289 3.5005 9.40047C4.93538 8.57204 6.77015 9.06367 7.59858 10.4985ZM7.59858 13.5004C6.77015 14.9353 4.93538 15.4269 3.5005 14.5985C2.06562 13.7701 1.574 11.9353 2.40242 10.5004C3.23085 9.06553 5.06562 8.5739 6.5005 9.40233C7.93538 10.2308 8.42701 12.0655 7.59858 13.5004Z", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Copy: ImageVector by lazy { icon("file-copy") {
        strokePath("M14 2V8H20M14 2H15L20 7V8M14 2H7V18H20V8", width = 2f, cap = StrokeCap.Butt, join = StrokeJoin.Miter)
        strokePath("M3 6L3 22H14", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }
    val Maximize: ImageVector by lazy { icon("fullscreen") {
        strokePath("M6.34315 17.6569L17.6569 6.34314M18.5 11V5.5H13M5.5 13V18.5H11", width = 2f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    } }

    private fun icon(name: String, paths: ImageVector.Builder.() -> Unit): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply(paths).build()

    private fun ImageVector.Builder.fillPath(data: String, evenOdd: Boolean = false) {
        addPath(
            pathData = PathParser().parsePathString(data).toNodes(),
            pathFillType = if (evenOdd) PathFillType.EvenOdd else PathFillType.NonZero,
            fill = SolidColor(Color.Black),
        )
    }

    private fun ImageVector.Builder.strokePath(
        data: String,
        width: Float,
        cap: StrokeCap,
        join: StrokeJoin,
    ) {
        addPath(
            pathData = PathParser().parsePathString(data).toNodes(),
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = width,
            strokeLineCap = cap,
            strokeLineJoin = join,
        )
    }
}
