package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import com.agendaqr.destinations.domain.ImportBatch

@Composable
expect fun ImportBatchControls(onBatch: (ImportBatch) -> Unit)
