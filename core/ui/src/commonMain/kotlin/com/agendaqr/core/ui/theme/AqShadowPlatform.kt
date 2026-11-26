package com.agendaqr.core.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

/**
 * Pintado de plataforma de las capas de [AqShadow]. En Android se dibuja con
 * `BlurMaskFilter` sobre el canvas nativo para reproducir los `BoxShadow` de
 * Moon (blur/offset/spread reales); en el resto de plataformas se conserva la
 * aproximación por elevación de `Modifier.shadow`.
 */
internal expect fun Modifier.aqShadowPlatform(
    layers: List<AqShadowLayer>,
    shape: Shape,
): Modifier
