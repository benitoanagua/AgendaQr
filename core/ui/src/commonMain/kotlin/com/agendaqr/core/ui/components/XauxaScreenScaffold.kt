package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.XauxaSpacing

/**
 * Contenedor de pantalla con Safe Areas (notch / gestos / teclado).
 * Uso: envolver el contenido de cada pantalla para alinear Android
 * edge-to-edge e iOS Safe Area sin `expect/actual`.
 */
@Composable
fun XauxaScreenColumn(
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val base = modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .safeDrawingPadding()
        .imePadding()
        // Ronda 2 (Área I): margen de pantalla unificado (antes Xxl).
        .padding(XauxaSpacing.ScreenMargin)
    if (scrollable) {
        Column(
            modifier = base.verticalScroll(rememberScrollState()),
            content = content,
        )
    } else {
        Column(modifier = base, content = content)
    }
}

/** Insets compartidos para listas que gestionan su propio padding. */
fun Modifier.xauxaSafeContent(): Modifier =
    this
        .statusBarsPadding()
        .navigationBarsPadding()
        .safeDrawingPadding()
        .imePadding()
