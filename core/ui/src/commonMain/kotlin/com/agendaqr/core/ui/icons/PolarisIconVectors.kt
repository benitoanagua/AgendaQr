package com.agendaqr.core.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * Polaris icon vectors converted verbatim from the published package
 * `@shopify/polaris-icons@9.3.1`, directory `dist/svg`. Every [Paths] constant
 * is the exact `d` attribute of one official SVG; `fill-rule="evenodd"` is
 * preserved as [PathFillType.EvenOdd]. The monochrome fill is replaced by
 * `AqIcon`'s semantic tint.
 */
internal object PolarisIconVectors {
    const val SourcePackage = "@shopify/polaris-icons"
    const val SourceVersion = "9.3.1"
    const val ViewBox = "0 0 20 20"

    val Search: ImageVector by lazy { icon("SearchIcon") { fillPath(Paths.Search.Outline, evenOdd = true) } }
    val Plus: ImageVector by lazy { icon("PlusIcon") { fillPath(Paths.Plus.Outline) } }
    val Bill: ImageVector by lazy { icon("BillIcon") { fillPath(Paths.Bill.Outline, evenOdd = true) } }
    val Star: ImageVector by lazy { icon("StarIcon") { fillPath(Paths.Star.Outline, evenOdd = true) } }
    val Folder: ImageVector by lazy { icon("FolderIcon") { fillPath(Paths.Folder.Outline, evenOdd = true) } }
    val ArrowLeft: ImageVector by lazy { icon("ArrowLeftIcon") { fillPath(Paths.ArrowLeft.Outline, evenOdd = true) } }
    val XCircle: ImageVector by lazy { icon("XCircleIcon") {
        fillPath(Paths.XCircle.Cross)
        fillPath(Paths.XCircle.Ring, evenOdd = true)
    } }
    val MenuHorizontal: ImageVector by lazy { icon("MenuHorizontalIcon") {
        fillPath(Paths.MenuHorizontal.First)
        fillPath(Paths.MenuHorizontal.Second)
        fillPath(Paths.MenuHorizontal.Third)
    } }
    val Home: ImageVector by lazy { icon("HomeIcon") { fillPath(Paths.Home.Outline, evenOdd = true) } }
    val Image: ImageVector by lazy { icon("ImageIcon") {
        fillPath(Paths.Image.Circle)
        fillPath(Paths.Image.Frame, evenOdd = true)
    } }
    val Camera: ImageVector by lazy { icon("CameraIcon") {
        fillPath(Paths.Camera.Lens, evenOdd = true)
        fillPath(Paths.Camera.Body, evenOdd = true)
    } }
    val Info: ImageVector by lazy { icon("InfoIcon") {
        fillPath(Paths.Info.Pointer)
        fillPath(Paths.Info.Dot)
        fillPath(Paths.Info.Ring, evenOdd = true)
    } }
    val AlertTriangle: ImageVector by lazy { icon("AlertTriangleIcon") {
        fillPath(Paths.AlertTriangle.Exclamation)
        fillPath(Paths.AlertTriangle.Dot)
        fillPath(Paths.AlertTriangle.Outline, evenOdd = true)
    } }
    val CheckCircle: ImageVector by lazy { icon("CheckCircleIcon") {
        fillPath(Paths.CheckCircle.Check)
        fillPath(Paths.CheckCircle.Ring, evenOdd = true)
    } }
    val Shopcodes: ImageVector by lazy { icon("ShopcodesIcon") {
        fillPath(Paths.Shopcodes.TopLeftDot)
        fillPath(Paths.Shopcodes.TopLeftFrame, evenOdd = true)
        fillPath(Paths.Shopcodes.BottomLeftDot)
        fillPath(Paths.Shopcodes.BottomLeftFrame, evenOdd = true)
        fillPath(Paths.Shopcodes.TopRightDot)
        fillPath(Paths.Shopcodes.TopRightFrame, evenOdd = true)
        fillPath(Paths.Shopcodes.CenterTopDot)
        fillPath(Paths.Shopcodes.CenterBottomDot)
        fillPath(Paths.Shopcodes.RightMiddleTopDot)
        fillPath(Paths.Shopcodes.RightMiddleBottomDot)
        fillPath(Paths.Shopcodes.CenterDot)
    } }
    val AlertCircle: ImageVector by lazy { icon("AlertCircleIcon") {
        fillPath(Paths.AlertCircle.Exclamation)
        fillPath(Paths.AlertCircle.Dot)
        fillPath(Paths.AlertCircle.Ring, evenOdd = true)
    } }
    val Archive: ImageVector by lazy { icon("ArchiveIcon") {
        fillPath(Paths.Archive.Slot)
        fillPath(Paths.Archive.Body, evenOdd = true)
    } }
    val Barcode: ImageVector by lazy { icon("BarcodeIcon") {
        fillPath(Paths.Barcode.TopLeft)
        fillPath(Paths.Barcode.BottomLeft)
        fillPath(Paths.Barcode.TopRight)
        fillPath(Paths.Barcode.BottomRight)
        fillPath(Paths.Barcode.Narrow)
        fillPath(Paths.Barcode.Medium)
        fillPath(Paths.Barcode.Wide)
        fillPath(Paths.Barcode.XWide)
    } }
    val ArrowDown: ImageVector by lazy { icon("ArrowDownIcon") { fillPath(Paths.ArrowDown.Outline, evenOdd = true) } }
    val ArrowUp: ImageVector by lazy { icon("ArrowUpIcon") { fillPath(Paths.ArrowUp.Outline, evenOdd = true) } }
    val Attachment: ImageVector by lazy { icon("AttachmentIcon") { fillPath(Paths.Attachment.Outline, evenOdd = true) } }
    val Store: ImageVector by lazy { icon("StoreIcon") { fillPath(Paths.Store.Outline, evenOdd = true) } }
    val Calendar: ImageVector by lazy { icon("CalendarIcon") { fillPath(Paths.Calendar.Outline, evenOdd = true) } }
    val Clock: ImageVector by lazy { icon("ClockIcon") {
        fillPath(Paths.Clock.Hand)
        fillPath(Paths.Clock.Ring, evenOdd = true)
    } }
    val ChevronRight: ImageVector by lazy { icon("ChevronRightIcon") { fillPath(Paths.ChevronRight.Outline, evenOdd = true) } }
    val ShieldCheckMark: ImageVector by lazy { icon("ShieldCheckMarkIcon") {
        fillPath(Paths.ShieldCheckMark.Check)
        fillPath(Paths.ShieldCheckMark.Shield, evenOdd = true)
    } }
    val Wallet: ImageVector by lazy { icon("WalletIcon") { fillPath(Paths.Wallet.Outline, evenOdd = true) } }
    val Lock: ImageVector by lazy { icon("LockIcon") {
        fillPath(Paths.Lock.Keyhole)
        fillPath(Paths.Lock.Body, evenOdd = true)
    } }
    val Delete: ImageVector by lazy { icon("DeleteIcon") {
        fillPath(Paths.Delete.FirstStroke)
        fillPath(Paths.Delete.SecondStroke)
        fillPath(Paths.Delete.Body, evenOdd = true)
    } }
    val Edit: ImageVector by lazy { icon("EditIcon") { fillPath(Paths.Edit.Outline, evenOdd = true) } }
    val Share: ImageVector by lazy { icon("ShareIcon") {
        fillPath(Paths.Share.Up)
        fillPath(Paths.Share.Box)
    } }
    val Duplicate: ImageVector by lazy { icon("DuplicateIcon") {
        fillPath(Paths.Duplicate.Plus)
        fillPath(Paths.Duplicate.Frames, evenOdd = true)
    } }
    val Maximize: ImageVector by lazy { icon("MaximizeIcon") {
        fillPath(Paths.Maximize.TopRight)
        fillPath(Paths.Maximize.BottomLeft)
    } }
    val X: ImageVector by lazy { icon("XIcon") { fillPath(Paths.X.Outline) } }
    val Backspace: ImageVector by lazy { icon("BackspaceIcon") {
        fillPath(Paths.Backspace.Cross)
        fillPath(Paths.Backspace.Body, evenOdd = true)
    } }
    val Check: ImageVector by lazy { icon("CheckIcon") {
        fillPath(Paths.Check.Outline, evenOdd = true)
    } }

    private fun icon(sourceName: String, paths: ImageVector.Builder.() -> Unit): ImageVector =
        ImageVector.Builder(
            name = sourceName,
            defaultWidth = 20.dp,
            defaultHeight = 20.dp,
            viewportWidth = 20f,
            viewportHeight = 20f,
        ).apply(paths).build()

    private fun ImageVector.Builder.fillPath(data: String, evenOdd: Boolean = false) {
        addPath(
            pathData = PathParser().parsePathString(data).toNodes(),
            pathFillType = if (evenOdd) PathFillType.EvenOdd else PathFillType.NonZero,
            fill = SolidColor(Color.Black),
        )
    }

    private object Paths {
        object Search {
            const val Outline = "M12.323 13.383a5.5 5.5 0 1 1 1.06-1.06l2.897 2.897a.75.75 0 1 1-1.06 1.06l-2.897-2.897Zm.677-4.383a4 4 0 1 1-8 0 4 4 0 0 1 8 0Z"
        }

        object Plus {
            const val Outline = "M10.75 5.75c0-.414-.336-.75-.75-.75s-.75.336-.75.75v3.5h-3.5c-.414 0-.75.336-.75.75s.336.75.75.75h3.5v3.5c0 .414.336.75.75.75s.75-.336.75-.75v-3.5h3.5c.414 0 .75-.336.75-.75s-.336-.75-.75-.75h-3.5v-3.5Z"
        }

        object Bill {
            const val Outline = "M5.5 3.5a2 2 0 0 0-2 2v3.75c0 .414.336.75.75.75h2v5.769a.85.85 0 0 0 1.433.618l1.442-1.357 1.611 1.516a.75.75 0 0 0 1.028 0l1.611-1.516 1.442 1.357a.85.85 0 0 0 1.433-.618v-10.269a2 2 0 0 0-2-2h-8.494l.005.017a2.02 2.02 0 0 0-.261-.017Zm-.5 2a.5.5 0 0 1 1 0v3h-1v-3Zm2.75-.48-.006-.02h6.506a.5.5 0 0 1 .5.5v8.764l-.69-.649a1 1 0 0 0-1.37 0l-1.44 1.355-1.44-1.355a1 1 0 0 0-1.37 0l-.69.65v-9.245Zm2 1.48a.75.75 0 0 0 0 1.5h3a.75.75 0 0 0 0-1.5h-3Zm-.75 3.75a.75.75 0 0 1 .75-.75h3a.75.75 0 0 1 0 1.5h-3a.75.75 0 0 1-.75-.75Z"
        }

        object Star {
            const val Outline = "M8.872 4.123c.453-.95 1.803-.95 2.256 0l1.39 2.912 3.199.421c1.042.138 1.46 1.422.697 2.146l-2.34 2.222.587 3.172c.192 1.034-.901 1.828-1.825 1.327l-2.836-1.54-2.836 1.54c-.924.501-2.017-.293-1.825-1.327l.587-3.172-2.34-2.222c-.762-.724-.345-2.008.697-2.146l3.2-.421 1.389-2.912Zm1.128 1.119-1.222 2.561a1.25 1.25 0 0 1-.965.701l-2.814.371 2.058 1.954c.307.292.446.718.369 1.134l-.517 2.791 2.495-1.354a1.25 1.25 0 0 1 1.192 0l2.495 1.354-.517-2.79a1.25 1.25 0 0 1 .369-1.135l2.058-1.954-2.814-.37a1.25 1.25 0 0 1-.965-.702l-1.222-2.561Z"
        }

        object Folder {
            const val Outline = "M5.75 5.5c-.69 0-1.25.56-1.25 1.25v6.5c0 .69.56 1.25 1.25 1.25h8.5c.69 0 1.25-.56 1.25-1.25v-4.5c0-.69-.56-1.25-1.25-1.25h-3.382a1.75 1.75 0 0 1-1.565-.967l-.171-.342a1.25 1.25 0 0 0-1.118-.691h-2.264Zm-2.75 1.25a2.75 2.75 0 0 1 2.75-2.75h2.264a2.75 2.75 0 0 1 2.46 1.52l.17.342a.25.25 0 0 0 .224.138h3.382a2.75 2.75 0 0 1 2.75 2.75v4.5a2.75 2.75 0 0 1-2.75 2.75h-8.5a2.75 2.75 0 0 1-2.75-2.75v-6.5Z"
        }

        object ArrowLeft {
            const val Outline = "M16.5 10a.75.75 0 0 1-.75.75h-9.69l2.72 2.72a.75.75 0 0 1-1.06 1.06l-4-4a.75.75 0 0 1 0-1.06l4-4a.75.75 0 1 1 1.06 1.06l-2.72 2.72h9.69a.75.75 0 0 1 .75.75Z"
        }

        object XCircle {
            const val Cross = "M13.03 6.97a.75.75 0 0 1 0 1.06l-1.97 1.97 1.97 1.97a.75.75 0 1 1-1.06 1.06l-1.97-1.97-1.97 1.97a.75.75 0 0 1-1.06-1.06l1.97-1.97-1.97-1.97a.75.75 0 0 1 1.06-1.06l1.97 1.97 1.97-1.97a.75.75 0 0 1 1.06 0Z"
            const val Ring = "M10 17a7 7 0 1 0 0-14 7 7 0 0 0 0 14Zm0-1.5a5.5 5.5 0 1 0 0-11 5.5 5.5 0 0 0 0 11Z"
        }

        object MenuHorizontal {
            const val First = "M6 10a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0Z"
            const val Second = "M11.5 10a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0Z"
            const val Third = "M17 10a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0Z"
        }

        object Home {
            const val Outline = "M8.344 3.692a2.25 2.25 0 0 1 3.312 0l3.854 4.19a3.75 3.75 0 0 1 .99 2.538v3.33a2.75 2.75 0 0 1-2.75 2.75h-1.75a1.5 1.5 0 0 1-1.5-1.5v-2h-1v2a1.5 1.5 0 0 1-1.5 1.5h-1.75a2.75 2.75 0 0 1-2.75-2.75v-3.33c0-.94.353-1.847.99-2.539l3.854-4.189Zm2.208 1.016a.75.75 0 0 0-1.104 0l-3.854 4.189a2.25 2.25 0 0 0-.594 1.523v3.33c0 .69.56 1.25 1.25 1.25h1.75v-2a1.5 1.5 0 0 1 1.5-1.5h1a1.5 1.5 0 0 1 1.5 1.5v2h1.75c.69 0 1.25-.56 1.25-1.25v-3.33a2.25 2.25 0 0 0-.594-1.523l-3.854-4.19Z"
        }

        object Image {
            const val Circle = "M12.5 9a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3Z"
            const val Frame = "M9.018 3.5h1.964c.813 0 1.469 0 2 .043.546.045 1.026.14 1.47.366a3.75 3.75 0 0 1 1.64 1.639c.226.444.32.924.365 1.47.043.531.043 1.187.043 2v1.964c0 .813 0 1.469-.043 2-.045.546-.14 1.026-.366 1.47a3.75 3.75 0 0 1-1.639 1.64c-.444.226-.924.32-1.47.365-.531.043-1.187.043-2 .043h-1.964c-.813 0-1.469 0-2-.043-.546-.045-1.026-.14-1.47-.366a3.75 3.75 0 0 1-1.64-1.639c-.226-.444-.32-.924-.365-1.47-.043-.531-.043-1.187-.043-2v-1.964c0-.813 0-1.469.043-2 .045-.546.14-1.026.366-1.47a3.75 3.75 0 0 1 1.639-1.64c.444-.226.924-.32 1.47-.365.531-.043 1.187-.043 2-.043Zm-1.877 1.538c-.454.037-.715.107-.912.207a2.25 2.25 0 0 0-.984.984c-.1.197-.17.458-.207.912-.037.462-.038 1.057-.038 1.909v1.428l.723-.867a1.75 1.75 0 0 1 2.582-.117l2.695 2.695 1.18-1.18a1.75 1.75 0 0 1 2.604.145l.216.27v-2.374c0-.852 0-1.447-.038-1.91-.037-.453-.107-.714-.207-.911a2.25 2.25 0 0 0-.984-.984c-.197-.1-.458-.17-.912-.207-.462-.037-1.056-.038-1.909-.038h-1.9c-.852 0-1.447 0-1.91.038Zm-2.103 7.821a7.12 7.12 0 0 1-.006-.08.746.746 0 0 0 .044-.049l1.8-2.159a.25.25 0 0 1 .368-.016l3.226 3.225a.75.75 0 0 0 1.06 0l1.71-1.71a.25.25 0 0 1 .372.021l1.213 1.516c-.021.06-.045.114-.07.165-.216.423-.56.767-.984.983-.197.1-.458.17-.912.207-.462.037-1.056.038-1.909.038h-1.9c-.852 0-1.447 0-1.91-.038-.453-.037-.714-.107-.911-.207a2.25 2.25 0 0 1-.984-.984c-.1-.197-.17-.458-.207-.912Z"
        }

        object Camera {
            const val Lens = "M10 7.25a3.25 3.25 0 1 0 0 6.5 3.25 3.25 0 0 0 0-6.5Zm-1.75 3.25a1.75 1.75 0 1 1 3.5 0 1.75 1.75 0 0 1-3.5 0Z"
            const val Body = "M8.584 4a2.75 2.75 0 0 0-2.435 1.472l-.346.658a.691.691 0 0 1-.612.37c-1.21 0-2.191.981-2.191 2.191v4.559a2.75 2.75 0 0 0 2.75 2.75h8.5a2.75 2.75 0 0 0 2.75-2.75v-4.559c0-1.21-.981-2.191-2.191-2.191a.691.691 0 0 1-.612-.37l-.346-.658a2.75 2.75 0 0 0-2.435-1.472h-2.832Zm-1.107 2.169a1.25 1.25 0 0 1 1.107-.669h2.832c.465 0 .89.258 1.107.669l.345.658a2.191 2.191 0 0 0 1.94 1.173c.383 0 .692.31.692.691v4.559c0 .69-.56 1.25-1.25 1.25h-8.5c-.69 0-1.25-.56-1.25-1.25v-4.559c0-.381.31-.691.691-.691a2.19 2.19 0 0 0 1.94-1.173l.346-.658Z"
        }

        object Info {
            const val Pointer = "M10 14a.75.75 0 0 1-.75-.75v-3.5a.75.75 0 0 1 1.5 0v3.5a.75.75 0 0 1-.75.75Z"
            const val Dot = "M9 7a1 1 0 1 1 2 0 1 1 0 0 1-2 0Z"
            const val Ring = "M17 10a7 7 0 1 1-14 0 7 7 0 0 1 14 0Zm-1.5 0a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0Z"
        }

        object AlertTriangle {
            const val Exclamation = "M10 6.75a.75.75 0 0 1 .75.75v3.5a.75.75 0 1 1-1.5 0v-3.5a.75.75 0 0 1 .75-.75Z"
            const val Dot = "M11 13.5a1 1 0 1 1-2 0 1 1 0 0 1 2 0Z"
            const val Outline = "M10 3.5c-1.045 0-1.784.702-2.152 1.447a449.26 449.26 0 0 1-2.005 3.847l-.028.052a403.426 403.426 0 0 0-2.008 3.856c-.372.752-.478 1.75.093 2.614.57.863 1.542 1.184 2.464 1.184h7.272c.922 0 1.895-.32 2.464-1.184.57-.864.465-1.862.093-2.614-.21-.424-1.113-2.147-2.004-3.847l-.032-.061a429.497 429.497 0 0 1-2.005-3.847c-.368-.745-1.107-1.447-2.152-1.447Zm-.808 2.112c.404-.816 1.212-.816 1.616 0 .202.409 1.112 2.145 2.022 3.88a418.904 418.904 0 0 1 2.018 3.875c.404.817 0 1.633-1.212 1.633h-7.272c-1.212 0-1.617-.816-1.212-1.633.202-.408 1.113-2.147 2.023-3.883a421.932 421.932 0 0 0 2.017-3.872Z"
        }

        object CheckCircle {
            const val Check = "M13.28 9.03a.75.75 0 0 0-1.06-1.06l-2.97 2.97-1.22-1.22a.75.75 0 0 0-1.06 1.06l1.75 1.75a.75.75 0 0 0 1.06 0l3.5-3.5Z"
            const val Ring = "M17 10a7 7 0 1 1-14 0 7 7 0 0 1 14 0Zm-1.5 0a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0Z"
        }

        object Shopcodes {
            const val TopLeftDot = "M5.75 6.25a.5.5 0 0 1 .5-.5h.5a.5.5 0 0 1 .5.5v.5a.5.5 0 0 1-.5.5h-.5a.5.5 0 0 1-.5-.5v-.5Z"
            const val TopLeftFrame = "M3.5 5.25c0-.966.784-1.75 1.75-1.75h2.5c.966 0 1.75.784 1.75 1.75v2.5a1.75 1.75 0 0 1-1.75 1.75h-2.5a1.75 1.75 0 0 1-1.75-1.75v-2.5Zm1.75-.25a.25.25 0 0 0-.25.25v2.5c0 .138.112.25.25.25h2.5a.25.25 0 0 0 .25-.25v-2.5a.25.25 0 0 0-.25-.25h-2.5Z"
            const val BottomLeftDot = "M6.25 12.75a.5.5 0 0 0-.5.5v.5a.5.5 0 0 0 .5.5h.5a.5.5 0 0 0 .5-.5v-.5a.5.5 0 0 0-.5-.5h-.5Z"
            const val BottomLeftFrame = "M3.5 12.25c0-.966.784-1.75 1.75-1.75h2.5c.966 0 1.75.784 1.75 1.75v2.5a1.75 1.75 0 0 1-1.75 1.75h-2.5a1.75 1.75 0 0 1-1.75-1.75v-2.5Zm1.75-.25a.25.25 0 0 0-.25.25v2.5c0 .138.112.25.25.25h2.5a.25.25 0 0 0 .25-.25v-2.5a.25.25 0 0 0-.25-.25h-2.5Z"
            const val TopRightDot = "M12.75 6.25a.5.5 0 0 1 .5-.5h.5a.5.5 0 0 1 .5.5v.5a.5.5 0 0 1-.5.5h-.5a.5.5 0 0 1-.5-.5v-.5Z"
            const val TopRightFrame = "M12.25 3.5a1.75 1.75 0 0 0-1.75 1.75v2.5c0 .966.784 1.75 1.75 1.75h2.5a1.75 1.75 0 0 0 1.75-1.75v-2.5a1.75 1.75 0 0 0-1.75-1.75h-2.5Zm-.25 1.75a.25.25 0 0 1 .25-.25h2.5a.25.25 0 0 1 .25.25v2.5a.25.25 0 0 1-.25.25h-2.5a.25.25 0 0 1-.25-.25v-2.5Z"
            const val CenterTopDot = "M11 10.5a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1Z"
            const val CenterBottomDot = "M10.5 15a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5v-1Z"
            const val RightMiddleTopDot = "M15 10.5a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1Z"
            const val RightMiddleBottomDot = "M14.5 15a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-1a.5.5 0 0 1-.5-.5v-1Z"
            const val CenterDot = "M13 12.5a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1Z"
        }

        object AlertCircle {
            const val Exclamation = "M10 6a.75.75 0 0 1 .75.75v3.5a.75.75 0 0 1-1.5 0v-3.5a.75.75 0 0 1 .75-.75Z"
            const val Dot = "M11 13a1 1 0 1 1-2 0 1 1 0 0 1 2 0Z"
            const val Ring = "M17 10a7 7 0 1 1-14 0 7 7 0 0 1 14 0Zm-1.5 0a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0Z"
        }

        object Archive {
            const val Slot = "M8.25 10.75a.75.75 0 0 1 .75-.75h2a.75.75 0 0 1 0 1.5h-2a.75.75 0 0 1-.75-.75Z"
            const val Body = "M5.25 3.5a1.75 1.75 0 0 0-1.75 1.75v2c0 .595.297 1.12.75 1.436v5.064a2.75 2.75 0 0 0 2.75 2.75h6a2.75 2.75 0 0 0 2.75-2.75v-5.064c.453-.316.75-.841.75-1.436v-2a1.75 1.75 0 0 0-1.75-1.75h-9.5Zm9 5.5h-8.5v4.75c0 .69.56 1.25 1.25 1.25h6c.69 0 1.25-.56 1.25-1.25v-4.75Zm-9.25-3.75a.25.25 0 0 1 .25-.25h9.5a.25.25 0 0 1 .25.25v2a.25.25 0 0 1-.25.25h-9.5a.25.25 0 0 1-.25-.25v-2Z"
        }


        object Barcode {
            const val TopLeft = "M4.5 6.25a.75.75 0 0 1 .75-.75h.487a.75.75 0 1 0 0-1.5h-.487a2.25 2.25 0 0 0-2.25 2.25v.5a.75.75 0 0 0 1.5 0v-.5Z"
            const val BottomLeft = "M4.5 13.76c0 .414.336.75.75.75h.5a.75.75 0 0 1 0 1.5h-.5a2.25 2.25 0 0 1-2.25-2.25v-.5a.75.75 0 0 1 1.5 0v.5Z"
            const val TopRight = "M14.75 5.5a.75.75 0 0 1 .75.75v.5a.75.75 0 0 0 1.5 0v-.5a2.25 2.25 0 0 0-2.25-2.25h-.5a.75.75 0 0 0 0 1.5h.5Z"
            const val BottomRight = "M15.5 13.76a.75.75 0 0 1-.75.75h-.5a.75.75 0 0 0 0 1.5h.5a2.25 2.25 0 0 0 2.25-2.25v-.5a.75.75 0 0 0-1.5 0v.5Z"
            const val Narrow = "M6 7.5a.5.5 0 0 1 1 0v5a.5.5 0 0 1-1 0v-5Z"
            const val Medium = "M10.5 7a.5.5 0 0 0-.5.5v5a.5.5 0 0 0 1 0v-5a.5.5 0 0 0-.5-.5Z"
            const val Wide = "M8 7.625a.625.625 0 1 1 1.25 0v4.75a.625.625 0 1 1-1.25 0v-4.75Z"
            const val XWide = "M13 7a1 1 0 0 0-1 1v4a1 1 0 1 0 2 0v-4a1 1 0 0 0-1-1Z"
        }

        object ArrowDown {
            const val Outline = "M10 3.5a.75.75 0 0 1 .75.75v9.69l2.72-2.72a.75.75 0 0 1 1.06 1.06l-4 4a.75.75 0 0 1-1.06 0l-4-4a.75.75 0 0 1 1.06-1.06l2.72 2.72v-9.69a.75.75 0 0 1 .75-.75Z"
        }

        object ArrowUp {
            const val Outline = "M10 16.5a.75.75 0 0 1-.75-.75v-9.69l-2.72 2.72a.75.75 0 1 1-1.06-1.06l4-4a.75.75 0 0 1 1.06 0l4 4a.75.75 0 1 1-1.06 1.06l-2.72-2.72v9.69a.75.75 0 0 1-.75.75Z"
        }

        object Attachment {
            const val Outline = "M4.843 9.854a3.75 3.75 0 0 0 0 5.303l.147.147a3.543 3.543 0 0 0 5.01 0 .75.75 0 0 0-1.06-1.061 2.043 2.043 0 0 1-2.89 0l-.146-.146a2.25 2.25 0 0 1 0-3.182l5.015-5.015a2.244 2.244 0 0 1 3.173 3.172l-2.286 2.286a.817.817 0 1 1-1.155-1.155l2.25-2.25a.75.75 0 1 0-1.06-1.061l-2.25 2.25a2.317 2.317 0 0 0 3.275 3.277l2.286-2.286a3.744 3.744 0 0 0-5.294-5.294l-5.015 5.015Z"
        }

        object Store {
            const val Outline = "M13.257 3h-6.514a1.25 1.25 0 0 0-.983.478l-2.386 3.037a1.75 1.75 0 0 0-.374 1.08v.655a2.75 2.75 0 0 0 1.5 2.45v4.55c0 .966.784 1.75 1.75 1.75h7.5a1.75 1.75 0 0 0 1.75-1.75v-4.55a2.75 2.75 0 0 0 1.5-2.45v-.481c0-.504-.17-.994-.48-1.39l-2.28-2.901a1.25 1.25 0 0 0-.983-.478Zm-.257 12.5h.75a.25.25 0 0 0 .25-.25v-4.25a2.742 2.742 0 0 1-2-.863 2.742 2.742 0 0 1-2 .863 2.742 2.742 0 0 1-2-.863 2.742 2.742 0 0 1-2 .863v4.25c0 .138.112.25.25.25h3.75v-2.5a1 1 0 0 1 1-1h1a1 1 0 0 1 1 1v2.5Zm-7-6h-.25c-.69 0-1.25-.56-1.25-1.25v-.654a.25.25 0 0 1 .053-.155l2.312-2.941h6.27l2.205 2.805a.75.75 0 0 1 .16.464v.481c0 .69-.56 1.25-1.25 1.25h-.25c-.69 0-1.25-.56-1.25-1.25v-.5a.75.75 0 0 0-1.5 0v.5a1.25 1.25 0 1 1-2.5 0v-.5a.75.75 0 0 0-1.5 0v.5c0 .69-.56 1.25-1.25 1.25Z"
        }

        object Calendar {
            const val Outline = "M7.75 3.5a.75.75 0 0 0-1.5 0v.407a3.075 3.075 0 0 0-.702.252 3.75 3.75 0 0 0-1.64 1.639c-.226.444-.32.924-.365 1.47-.043.531-.043 1.187-.043 2v1.464c0 .813 0 1.469.043 2 .045.546.14 1.026.366 1.47a3.75 3.75 0 0 0 1.639 1.64c.444.226.924.32 1.47.365.531.043 1.187.043 2 .043h3.383c.323 0 .542 0 .735-.02a3.75 3.75 0 0 0 3.344-3.344c.02-.193.02-.412.02-.735v-2.883c0-.813 0-1.469-.043-2-.045-.546-.14-1.026-.366-1.47a3.75 3.75 0 0 0-1.639-1.64 3.076 3.076 0 0 0-.702-.251v-.407a.75.75 0 0 0-1.5 0v.259c-.373-.009-.794-.009-1.268-.009h-1.964c-.474 0-.895 0-1.268.009v-.259Zm-1.521 1.995c.197-.1.458-.17.912-.207.462-.037 1.057-.038 1.909-.038h1.9c.853 0 1.447 0 1.91.038.453.037.714.107.912.207.423.216.767.56.983.984.1.197.17.458.207.912.014.18.024.38.029.609h-9.982c.006-.228.015-.429.03-.61.036-.453.106-.714.206-.911a2.25 2.25 0 0 1 .984-.984Zm-1.229 4.005v1.2c0 .853 0 1.447.038 1.91.037.453.107.714.207.912.216.423.56.767.984.983.197.1.458.17.912.207.462.037 1.057.038 1.909.038h3.306c.385 0 .52-.001.626-.012a2.25 2.25 0 0 0 2.006-2.006c.011-.106.012-.241.012-.626v-2.606h-10Z"
        }

        object Clock {
            const val Hand = "M10.75 6a.75.75 0 0 0-1.5 0v4c0 .199.079.39.22.53l2 2a.75.75 0 1 0 1.06-1.06l-1.78-1.78v-3.69Z"
            const val Ring = "M17 10a7 7 0 1 1-14 0 7 7 0 0 1 14 0Zm-1.5 0a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0Z"
        }

        object ChevronRight {
            const val Outline = "M7.72 14.53a.75.75 0 0 1 0-1.06l3.47-3.47-3.47-3.47a.75.75 0 0 1 1.06-1.06l4 4a.75.75 0 0 1 0 1.06l-4 4a.75.75 0 0 1-1.06 0Z"
        }

        object ShieldCheckMark {
            const val Check = "M8.28 8.683a.75.75 0 0 0-1.06 1.06l1.548 1.548a.75.75 0 0 0 1.06 0l2.963-2.962a.75.75 0 0 0-1.06-1.06l-2.433 2.431-1.018-1.017Z"
            const val Shield = "M11.093 2.914a1.75 1.75 0 0 0-2.186 0l-.317.253a15.25 15.25 0 0 1-3.217 1.976l-.847.384a1.71 1.71 0 0 0-1.01 1.628c.28 6.25 4.38 9.048 5.732 9.802.47.262 1.034.262 1.503 0 1.352-.753 5.454-3.55 5.734-9.783a1.71 1.71 0 0 0-1.002-1.623l-.9-.416a15.249 15.249 0 0 1-3.136-1.938l-.354-.283Zm-1.25 1.171a.25.25 0 0 1 .313 0l.354.283a16.749 16.749 0 0 0 3.445 2.129l.9.415a.213.213 0 0 1 .131.195c-.246 5.489-3.827 7.906-4.965 8.54a.042.042 0 0 1-.02.006c-.005 0-.012 0-.022-.006-1.136-.634-4.718-3.053-4.965-8.56-.003-.066.037-.15.133-.194l.846-.385a16.75 16.75 0 0 0 3.534-2.17l.317-.253Z"
        }

        object Wallet {
            const val Outline = "M3.5 5.75v8.5a2.25 2.25 0 0 0 2.25 2.25h8.5a2.25 2.25 0 0 0 2.25-2.25v-1c.304-.228.5-.591.5-1v-2c0-.409-.196-.772-.5-1v-.75a2.25 2.25 0 0 0-2.25-2.25h-.75v-.5a2.25 2.25 0 0 0-2.25-2.25h-5.5a2.25 2.25 0 0 0-2.25 2.25Zm2.25-.75a.75.75 0 0 0-.75.75v.5h7v-.5a.75.75 0 0 0-.75-.75h-5.5Zm9.75 5.5h-3.75v1.5h3.75v-1.5Zm-.5-1.5v-.5a.75.75 0 0 0-.75-.75h-9.25v6.5c0 .414.336.75.75.75h8.5a.75.75 0 0 0 .75-.75v-.75h-3.5c-.69 0-1.25-.56-1.25-1.25v-2c0-.69.56-1.25 1.25-1.25h3.5Z"
        }

        object Lock {
            const val Keyhole = "M10.75 13.05a1.5 1.5 0 1 0-1.5 0v.45a.75.75 0 0 0 1.5 0v-.45Z"
            const val Body = "M6.25 7.095v-.345a3.75 3.75 0 1 1 7.5 0v.345a3.001 3.001 0 0 1 2.25 2.905v4a3 3 0 0 1-3 3h-6a3 3 0 0 1-3-3v-4a3 3 0 0 1 2.25-2.905Zm1.5-.345a2.25 2.25 0 0 1 4.5 0v.25h-4.5v-.25Zm-2.25 3.25a1.5 1.5 0 0 1 1.5-1.5h6a1.5 1.5 0 0 1 1.5 1.5v4a1.5 1.5 0 0 1-1.5 1.5h-6a1.5 1.5 0 0 1-1.5-1.5v-4Z"
        }

        object Delete {
            const val FirstStroke = "M11.5 8.25a.75.75 0 0 1 .75.75v4.25a.75.75 0 0 1-1.5 0v-4.25a.75.75 0 0 1 .75-.75Z"
            const val SecondStroke = "M9.25 9a.75.75 0 0 0-1.5 0v4.25a.75.75 0 0 0 1.5 0v-4.25Z"
            const val Body = "M7.25 5.25a2.75 2.75 0 0 1 5.5 0h3a.75.75 0 0 1 0 1.5h-.75v5.45c0 1.68 0 2.52-.327 3.162a3 3 0 0 1-1.311 1.311c-.642.327-1.482.327-3.162.327h-.4c-1.68 0-2.52 0-3.162-.327a3 3 0 0 1-1.311-1.311c-.327-.642-.327-1.482-.327-3.162v-5.45h-.75a.75.75 0 0 1 0-1.5h3Zm1.5 0a1.25 1.25 0 1 1 2.5 0h-2.5Zm-2.25 1.5h7v5.45c0 .865-.001 1.423-.036 1.848-.033.408-.09.559-.128.633a1.5 1.5 0 0 1-.655.655c-.074.038-.225.095-.633.128-.425.035-.983.036-1.848.036h-.4c-.865 0-1.423-.001-1.848-.036-.408-.033-.559-.09-.633-.128a1.5 1.5 0 0 1-.656-.655c-.037-.074-.094-.225-.127-.633-.035-.425-.036-.983-.036-1.848v-5.45Z"
        }

        object Edit {
            const val Outline = "M15.655 4.344a2.695 2.695 0 0 0-3.81 0l-.599.599-.009-.009-1.06 1.06.008.01-5.88 5.88a2.75 2.75 0 0 0-.805 1.944v1.922a.75.75 0 0 0 .75.75h1.922a2.75 2.75 0 0 0 1.944-.806l7.54-7.539a2.695 2.695 0 0 0 0-3.81Zm-4.409 2.72-5.88 5.88a1.25 1.25 0 0 0-.366.884v1.172h1.172c.331 0 .65-.132.883-.366l5.88-5.88-1.689-1.69Zm2.75.629.599-.599a1.195 1.195 0 1 0-1.69-1.689l-.598.599 1.69 1.689Z"
        }

        object Share {
            const val Up = "M10.75 12.75a.75.75 0 0 1-1.5 0v-6.69l-1.22 1.22a.75.75 0 0 1-1.06-1.06l2.5-2.5a.75.75 0 0 1 1.06 0l2.5 2.5a.75.75 0 0 1-1.06 1.06l-1.22-1.22v6.69Z"
            const val Box = "M16.5 10.25a2.25 2.25 0 0 0-2.25-2.25h-.07a.75.75 0 0 0 0 1.5h.07a.75.75 0 0 1 .75.75v4a.75.75 0 0 1-.75.75h-8.5a.75.75 0 0 1-.75-.75v-4a.75.75 0 0 1 .75-.75h.07a.75.75 0 0 0 0-1.5h-.07a2.25 2.25 0 0 0-2.25 2.25v4a2.25 2.25 0 0 0 2.25 2.25h8.5a2.25 2.25 0 0 0 2.25-2.25v-4Z"
        }

        object Duplicate {
            const val Plus = "M11.25 8.5c-.414 0-.75.336-.75.75v1.25h-1.25c-.414 0-.75.336-.75.75s.336.75.75.75h1.25v1.25c0 .414.336.75.75.75s.75-.336.75-.75v-1.25h1.25c.414 0 .75-.336.75-.75s-.336-.75-.75-.75h-1.25v-1.25c0-.414-.336-.75-.75-.75Z"
            const val Frames = "M8.75 16.5c-1.438 0-2.618-1.104-2.74-2.51-1.406-.122-2.51-1.302-2.51-2.74v-5c0-1.519 1.231-2.75 2.75-2.75h5c1.438 0 2.618 1.104 2.74 2.51 1.406.122 2.51 1.302 2.51 2.74v5c0 1.519-1.231 2.75-2.75 2.75h-5Zm0-10.5c-1.519 0-2.75 1.231-2.75 2.75v3.725c-.57-.116-1-.62-1-1.225v-5c0-.69.56-1.25 1.25-1.25h5c.605 0 1.11.43 1.225 1h-3.725Zm0 1.5c-.69 0-1.25.56-1.25 1.25v5c0 .69.56 1.25 1.25 1.25h5c.69 0 1.25-.56 1.25-1.25v-5c0-.69-.56-1.25-1.25-1.25h-5Z"
        }

        object Maximize {
            const val TopRight = "M12.75 3.5a.75.75 0 0 0 0 1.5h1.19l-3.22 3.22a.75.75 0 0 0 1.06 1.06l3.22-3.22v1.19a.75.75 0 0 0 1.5 0v-3a.75.75 0 0 0-.75-.75h-3Z"
            const val BottomLeft = "M7.25 16.5a.75.75 0 0 0 0-1.5h-1.19l3.22-3.22a.75.75 0 1 0-1.06-1.06l-3.22 3.22v-1.19a.75.75 0 0 0-1.5 0v3c0 .414.336.75.75.75h3Z"
        }

        object X {
            const val Outline = "M13.97 15.03a.75.75 0 1 0 1.06-1.06l-3.97-3.97 3.97-3.97a.75.75 0 0 0-1.06-1.06l-3.97 3.97-3.97-3.97a.75.75 0 0 0-1.06 1.06l3.97 3.97-3.97 3.97a.75.75 0 1 0 1.06 1.06l3.97-3.97 3.97 3.97Z"
        }

        object Backspace {
            const val Cross = "M14.03 7.47a.75.75 0 0 1 0 1.06l-1.47 1.47 1.47 1.47a.75.75 0 1 1-1.06 1.06l-1.47-1.47-1.47 1.47a.75.75 0 1 1-1.06-1.06l1.47-1.47-1.47-1.47a.75.75 0 0 1 1.06-1.06l1.47 1.47 1.47-1.47a.75.75 0 0 1 1.06 0Z"
            const val Body = "M5.553 5.083a2.75 2.75 0 0 1 2.188-1.083h6.509a2.75 2.75 0 0 1 2.75 2.75v6.5a2.75 2.75 0 0 1-2.75 2.75h-6.51a2.75 2.75 0 0 1-2.187-1.083l-2.476-3.25a2.75 2.75 0 0 1 0-3.334l2.476-3.25Zm2.188.417c-.39 0-.758.182-.995.492l-2.476 3.25a1.25 1.25 0 0 0 0 1.516l2.476 3.25c.237.31.604.492.995.492h6.509c.69 0 1.25-.56 1.25-1.25v-6.5c0-.69-.56-1.25-1.25-1.25h-6.51Z"
        }

        object Check {
            const val Outline = "M15.78 5.97a.75.75 0 0 1 0 1.06l-6.5 6.5a.75.75 0 0 1-1.06 0l-3.25-3.25a.75.75 0 1 1 1.06-1.06l2.72 2.72 5.97-5.97a.75.75 0 0 1 1.06 0Z"
        }
    }
}
