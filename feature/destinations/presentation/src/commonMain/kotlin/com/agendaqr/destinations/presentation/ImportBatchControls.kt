package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import com.agendaqr.destinations.domain.ImportBatch

/**
 * Botón que abre el selector de imágenes del lote (S12 Idle). La etiqueta
 * es copy del llamador; cada plataforma conecta su propio selector.
 */
@Composable
expect fun ImportBatchPickerButton(
    label: String,
)

@Composable
expect fun ImportBatchControls(onBatch: (ImportBatch) -> Unit)