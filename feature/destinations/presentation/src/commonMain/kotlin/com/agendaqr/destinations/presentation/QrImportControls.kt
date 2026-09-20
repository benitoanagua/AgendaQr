package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import com.agendaqr.destinations.domain.QrAsset

data class QrImportResult(val assets: List<QrAsset>)

@Composable
expect fun QrImportControls(onResult: (QrImportResult) -> Unit)
