package com.agendaqr.core.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqChoiceOption
import com.agendaqr.core.ui.components.AqCircularLoader
import com.agendaqr.core.ui.components.AqDropdown
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqProgress
import com.agendaqr.core.ui.components.AqProgressBar
import com.agendaqr.core.ui.components.AqPullToRefresh
import com.agendaqr.core.ui.components.AqSwipeAction
import com.agendaqr.core.ui.components.AqSwipeActions
import com.agendaqr.core.ui.components.AqSwitch
import com.agendaqr.core.ui.components.AqTab
import com.agendaqr.core.ui.components.AqTabBar
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqTagSize
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqTable
import com.agendaqr.core.ui.components.AqTableColumn
import com.agendaqr.core.ui.components.AqTableRow
import com.agendaqr.core.ui.components.AqSyncIndicator
import com.agendaqr.core.ui.components.AqSyncLabels
import com.agendaqr.core.ui.components.AqSyncState
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Galería mínima para revisar los roles semánticos en los tres estados. */
@Composable
private fun AqTokenGallery(darkTheme: Boolean) {
    AqTheme(darkTheme = darkTheme) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AqSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
        ) {
            AqText(text = "Sistema visual", color = AqColor.TextPrimary)
            AqText(text = "Comprobante recibido", color = AqColor.TextSecondary)
            AqAlert(
                tone = AqTone.Info,
                message = "Comprobante recibido",
            )
            AqButton(
                label = "Continuar",
                onClick = {},
                variant = AqButtonVariant.Primary,
                size = AqButtonSize.Large,
            )
            AqButton(
                label = "Cancelar",
                onClick = {},
                variant = AqButtonVariant.Default,
                size = AqButtonSize.Medium,
            )
        }
    }
}

@Preview
@Composable
internal fun AqTokenPreviewLight() {
    AqTokenGallery(darkTheme = false)
}

@Preview
@Composable
internal fun AqTokenPreviewDark() {
    AqTokenGallery(darkTheme = true)
}

@Preview
@Composable
internal fun AqTokenPreviewFont200() {
    CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 2f)) {
        AqTokenGallery(darkTheme = false)
    }
}

/** Galería de la capa aditiva: controles, acciones, datos y extensiones. */
@Composable
private fun AqComponentGallery(darkTheme: Boolean) {
    val options = listOf(
        AqChoiceOption("a", "Opción A", "Descripción A"),
        AqChoiceOption("b", "Opción B", "Descripción B"),
    )
    AqTheme(darkTheme = darkTheme) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AqSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
        ) {
            AqText(text = "Componentes", color = AqColor.TextPrimary)
            AqButton(
                label = "Abrir detalle",
                onClick = {},
                variant = AqButtonVariant.Text,
                size = AqButtonSize.Small,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                AqButton(
                    label = "Cancelar",
                    onClick = {},
                    variant = AqButtonVariant.Default,
                    size = AqButtonSize.Small,
                )
                AqButton(
                    label = "Guardar",
                    onClick = {},
                    variant = AqButtonVariant.Primary,
                    size = AqButtonSize.Small,
                )
            }
            AqDropdown(
                open = false,
                options = listOf("Opción A", "Opción B"),
                selected = 0,
                onOptionClick = {},
                anchor = {
                    AqButton(
                        label = "Selecciona",
                        onClick = {},
                        variant = AqButtonVariant.Outline,
                        size = AqButtonSize.Medium,
                    )
                },
                content = {},
            )
            AqCheckbox(label = "Adjuntar comprobante", checked = true, onCheckedChange = {})
            AqSwitch(label = "Sincronización activa", checked = true, onCheckedChange = {})
            AqProgressBar(progress = AqProgress(0.6f), contentDescription = "Progreso")
            AqCircularLoader(loaderSize = AqSize.Md, contentDescription = "Cargando")
            AqTag(text = "Comprobante recibido", tone = AqTone.Success, size = AqTagSize.Sm)
            AqSyncIndicator(
                state = AqSyncState.Pending,
                labels = AqSyncLabels("Sincronizado", "Pendiente", "Sincronizando", "Error", "Sin servicio"),
            )
            AqTable(
                columns = listOf(
                    AqTableColumn("name", "Concepto"),
                    AqTableColumn("amount", "Importe"),
                ),
                rows = listOf(AqTableRow("1", mapOf("name" to "Cobro", "amount" to "10,00"))),
            )
            AqSwipeActions(
                actions = listOf(
                    AqSwipeAction(
                        label = "Eliminar",
                        icon = AqIcons.Trash,
                        onAction = {},
                    ),
                ),
            ) {
                AqMenuItem(
                    title = "Elemento deslizable",
                    subtitle = "Acciones al deslizar",
                )
            }
            AqPullToRefresh(
                isRefreshing = false,
                onRefresh = {},
                refreshingLabel = "Actualizando",
                content = {
                    AqMenuItem(
                        title = "Lista actualizable",
                        subtitle = "Arrastre vertical",
                    )
                },
            )
        }
    }
}

@Preview
@Composable
internal fun AqComponentPreviewLight() = AqComponentGallery(darkTheme = false)

@Preview
@Composable
internal fun AqComponentPreviewDark() = AqComponentGallery(darkTheme = true)

@Preview
@Composable
internal fun AqComponentPreviewFont200() {
    CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 2f)) {
        AqComponentGallery(darkTheme = false)
    }
}
