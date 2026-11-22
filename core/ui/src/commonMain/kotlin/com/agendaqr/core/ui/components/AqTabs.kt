package com.agendaqr.core.ui.components

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Pestaña de [AqTabBar] / [AqPillTab]: etiqueta visible SIEMPRE (es la
 * señal de texto) y conteo opcional en badge.
 */
data class AqTab(
    val label: String,
    /** Glifo opcional (p. ej. navegación raíz con icono + etiqueta). */
    val icon: ImageVector? = null,
    /** Conteo real junto a la etiqueta; null = sin badge. */
    val count: Int? = null,
)
