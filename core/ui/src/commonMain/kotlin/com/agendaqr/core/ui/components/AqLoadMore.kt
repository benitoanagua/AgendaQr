package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType

/**
 * Pie de paginación "cargar más" sobre piezas Moon ([AqButton] de texto +
 * [AqCircularLoader]). Sustituye a [AqLoadMoreFooter].
 *
 * - [onLoadMore]: invocada al mostrar el pie (paginación infinita).
 * - [loadMoreLabel]: texto de la acción de carga (copy del llamador).
 * - [loadingLabel]: texto mientras se carga.
 * - [endLabel]: texto cuando no hay más páginas.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqLoadMore(
    onLoadMore: () -> Unit,
    loadMoreLabel: String,
    loadingLabel: String,
    endLabel: String,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    end: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AqSize.X2s),
    ) {
        if (end) {
            AqText(
                text = endLabel,
                size = AqType.Body,
                color = AqColor.TextTertiary,
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (loading) {
                    AqCircularLoader(
                        loaderSize = AqSize.Sm,
                        contentDescription = null,
                    )
                }
                AqButton(
                    label = if (loading) loadingLabel else loadMoreLabel,
                    onClick = { if (!loading) onLoadMore() },
                    enabled = !loading,
                    variant = AqButtonVariant.Text,
                    size = AqButtonSize.Small,
                    modifier = Modifier.defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                )
            }
        }
    }
}
