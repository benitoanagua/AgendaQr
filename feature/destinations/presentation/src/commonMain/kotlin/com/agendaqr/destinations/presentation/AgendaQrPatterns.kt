package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText

@Composable
internal fun AgendaQrPatterns() {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        AqText(text = AppStrings.Patrones, size = com.agendaqr.core.ui.theme.AqType.Body)
    }
}
