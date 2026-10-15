package com.agendaqr.destinations.data

/**
 * QR de demostración en iOS: imagen válida embebida (PNG generado con
 * `qrencode` para `https://agendaqr.app/demo`).
 *
 * PENDIENTE de verificación con Xcode: generar un PNG por contenido con
 * CoreImage (`CIQRCodeGenerator`) para que cada destino demo tenga su QR
 * propio, como ya hace Android con ZXing.
 */
actual fun demoQrPngBase64(content: String): String = DEMO_QR_FALLBACK_BASE64

private const val DEMO_QR_FALLBACK_BASE64: String =
    "iVBORw0KGgoAAAANSUhEUgAAAK4AAACuAQMAAACVwqStAAAABlBMVEUAAAD///+l2Z/dAAAAAnRSTlP//8i138cAAAAJcEhZcwAACxIAAAsSAdLdfvwAAAEISURBVEiJ5ZbdDcQgDIMjMUBHyuqMxABIudhA70c845MatVX69SWKTVKLbdizcbOMEjWa2xV4MRdhz4JKAPQLCYgIZ0WlWWkOzESJoxqr+wMM1lelKkzRakTdaHkU08jNx/Xj76N4BDr0cdAlmCCLsgvdGuppMCw8ZkyFd5ZoAkwjZ4XDRMxlODh3+wUj5xHPpwbnjSY51INotU8jazCqs/llankeZ5M4ersNK8US7TgeRg64OBnVCw3mkOMGmMNvjpnzeA28W7f+NXoP4rUEYi1GHQbABmjM291BAeYPlG+0lGAaOS3caSUlXqKxW67CFA2tGv+5791wGk8jJ84C2S3X4G08Gr8AnAwuTnfakV8AAAAASUVORK5CYII="
