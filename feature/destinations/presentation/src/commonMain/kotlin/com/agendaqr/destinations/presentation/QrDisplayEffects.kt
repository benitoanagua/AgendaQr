package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable

/**
 * Efectos de exhibición del QR a pantalla completa (U-05, D4): brillo al
 * máximo y pantalla encendida mientras el QR se muestra; al salir (Back,
 * rotación, bloqueo) se restauran los valores previos. Sin persistencia:
 * todo vive en la composición.
 */
@Composable
expect fun QrDisplayScreenEffects()
