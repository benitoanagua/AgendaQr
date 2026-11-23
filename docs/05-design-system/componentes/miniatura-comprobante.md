# Ficha de componente: Miniatura de comprobante (`AqThumbnail`, `AqThumbnailFrame`)

- **Nombre:** Miniatura de comprobante (`AqThumbnail`, `AqThumbnailFrame`, `AqThumbnailFallback`, `AqThumbnailSkeleton`)
- **Nivel de origen:** `moon` (Nivel 1 para tokens y apariencia) con referencia `moon-pattern` (Nivel 2 para anatomía de avatar/fallback y estados de carga)
- **Paquete:** `com.agendaqr.core.ui.components` (archivo `AqThumbnail.kt`; decodificación por plataforma en `AqThumbnail.android.kt` y `AqThumbnail.ios.kt`)

## Anatomía

Marco cuadrado de [AqMetrics.ReceiptThumbnailSize] (56 dp) con:
- Superficie de marco: [AqColor.SurfaceContainerHighest], radio [AqShapes.Medium], contenido centrado (`AqThumbnailFrame`).
- Contenido según el estado ([ThumbnailState]):
  - **Loading:** esqueleto estático ([AqThumbnailSkeleton]) de fondo [AqColor.Surface] sobre el mismo radio. Nunca un ícono. Respet «reducir movimiento» por construcción (no anima nunca).
  - **Success:** imagen decodada con [ContentScale.Crop] (recorte sin deformar) sobre radio [AqShapes.Medium]. Imagen decorativa: `contentDescription = null` (el título de la fila describe el archivo).
  - **Failure:** ícono semántico centrado ([AqThumbnailFallback]): [AqIcons.QrCode] para imágenes sin vista previa (decisión de producto: el comprobante sin miniatura se representa con el glifo de QR, nunca el glifo «ALT») y [AqIcons.Attachment] para PDF/archivos. Tinte [AqColor.TextTertiary], tamaño [AqMetrics.IconSizeCompact].

## Estados

| Estado | Qué se muestra | Ícono | Descripción de accesibilidad |
|---|---|---|---|
| `Loading` | Esqueleto estático | — | n/a (bloque decorativo) |
| `Success` | Imagen recortada | — | `null` |
| `Failure` (imagen) | Ícono QR | `AqIcons.QrCode` | Inyectada por el llamador (p. ej. «Vista previa no disponible») |
| `Failure` (PDF/archivo) | Ícono adjunto | `AqIcons.Attachment` | Inyectada por el llamador (p. ej. «PDF adjunto (sin vista previa)») |

La descripción de accesibilidad del fallback solo se anuncia cuando el ícono es el único indicio de estado; el llamador de `feature/` la inyecta según el `mimeType`.

## Tokens Moon

- Superficie del marco: `AqColor.SurfaceContainerHighest`
- Radio: `AqShapes.Medium`
- Esqueleto: `AqColor.Surface` + `AqShapes.Medium`
- Ícono de fallback: `AqColor.TextTertiary`, `AqMetrics.IconSizeCompact` (20 dp)
- Tamaño del marco: `AqMetrics.ReceiptThumbnailSize` (56 dp)

## Accesibilidad

- Objetivo táctil: el marco se compone dentro de filas con objetivo ≥ 48 dp (convención D-02); el ícono en sí es decorativo dentro del marco.
- Reducción de movimiento: el esqueleto no anima (estático por construcción); no hay transición continua.
- Modo oscuro: `SurfaceContainerHighest` y `Surface` se derivan del tema publicado; el contraste ícono/superficie se valida con `AqContrastPairsTest`.

## Comportamiento Claro / Oscuro

Idéntico en ambos temas; los tokens semánticos se adaptan al tema activo.

## Notas de adaptación

- La decodificación del archivo es por plataforma (`expect decodeThumbnailBitmap`): Android usa `BitmapFactory` con `inSampleSize` contra el tamaño real en px (densidad), `ARGB_8888` y caché en memoria acotada ([LruCache]); iOS usa `ImageIO` downsampling (`CGImageSourceCreateThumbnailAtIndex` con `kCGImageSourceThumbnailMaxPixelSize` y `kCGImageSourceCreateThumbnailFromImageAlways`). La decodificación corre fuera del hilo principal y se cancela al salir de la composición (`LaunchedEffect` por `file`).
- iOS no está validado en este entorno (sin macOS/Xcode): se registra como no verificado (ADR-0025).
- No se usa `Material3 Icons` ni glifos propios: todo proviene de los iconos del sistema visual anterior reimplementados en `MoonIconVectors`.

## Pantallas consumidoras

- `UnassociatedScreen` (bandeja de comprobantes sin asociar, `OperationListScreen.kt`): fila de la bandeja, con `showChevron = true` y descripción de accesibilidad del estado de fallo.
