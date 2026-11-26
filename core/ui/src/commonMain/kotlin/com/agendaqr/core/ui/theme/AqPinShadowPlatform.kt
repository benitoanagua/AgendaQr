package com.agendaqr.core.ui.theme

import androidx.compose.ui.Modifier

/**
 * Sombra de la silueta pin+flecha de [AqProgressPin] (`drawShadow(path,
 * popo, shadowElevation)` de Flutter). Se dibuja desde el contenedor padre
 * del pin: cuando el pin queda fuera de pantalla (p. ej. `MoonProgressPin`
 * envolviendo un hijo alto sin `thumbSize`) su propio nodo se descarta por
 * visibilidad y la sombra no sangraría al lienzo, a diferencia del painter
 * único de Moon.
 *
 * Todas las medidas llegan en px del espacio de coordenadas del padre.
 */
internal expect fun Modifier.aqPinShadowPlatform(
    pinCenterX: Float,
    pinCenterY: Float,
    pinRadius: Float,
    arrowHalfWidth: Float,
    arrowBaseY: Float,
    arrowHeight: Float,
    blur: Float,
    offsetY: Float,
    alpha: Float,
): Modifier
