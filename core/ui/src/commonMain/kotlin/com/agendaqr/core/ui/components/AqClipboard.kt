package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable

/**
 * Puerto de escritura al portapapeles (contrato multiplataforma):
 * devuelve una función capturada en composición que copia texto plano.
 * Solo escritura: Agenda QR copia contenido decodificado; el pegado
 * pertenece a otras apps.
 */
@Composable
expect fun rememberAmClipboard(): (String) -> Unit
