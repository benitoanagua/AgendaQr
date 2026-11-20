package com.agendaqr.core.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.agendaqr.core.ui.components.AqBlockStack
import com.agendaqr.core.ui.components.AqButtonGroup
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqChoiceOption
import com.agendaqr.core.ui.components.AqEvidenceBadge
import com.agendaqr.core.ui.components.AqGroupedAction
import com.agendaqr.core.ui.components.AqHeading
import com.agendaqr.core.ui.components.AqIndexTable
import com.agendaqr.core.ui.components.AqLink
import com.agendaqr.core.ui.components.AqProgressBar
import com.agendaqr.core.ui.components.AqProgress
import com.agendaqr.core.ui.components.AqPrimaryButton
import com.agendaqr.core.ui.components.AqPullToRefresh
import com.agendaqr.core.ui.components.AqSecondaryButton
import com.agendaqr.core.ui.components.AqSelect
import com.agendaqr.core.ui.components.AqSpinner
import com.agendaqr.core.ui.components.AqStatusBanner
import com.agendaqr.core.ui.components.AqSwipeAction
import com.agendaqr.core.ui.components.AqSwipeActions
import com.agendaqr.core.ui.components.AqSwitch
import com.agendaqr.core.ui.components.AqSyncIndicator
import com.agendaqr.core.ui.components.AqSyncLabels
import com.agendaqr.core.ui.components.AqSyncState
import com.agendaqr.core.ui.components.AqTableColumn
import com.agendaqr.core.ui.components.AqTableRow
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.components.AqIcons
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
            AqHeading("Sistema visual")
            AqText(
                text = "Comprobante recibido",
                color = AqColor.TextSecondary,
            )
            AqStatusBanner(
                message = "Comprobante recibido",
                tone = AqTone.Info,
            )
            AqPrimaryButton(label = "Continuar", onClick = {})
            AqSecondaryButton(label = "Cancelar", onClick = {})
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
        AqBlockStack(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AqSpacing.Lg),
        ) {
            AqHeading("Componentes")
            AqLink(label = "Abrir detalle", onClick = {})
            AqButtonGroup(
                actions = listOf(
                    AqGroupedAction("cancel", "Cancelar", onClick = {}),
                    AqGroupedAction("save", "Guardar", onClick = {}, emphasized = true),
                ),
            )
            AqSelect(
                options = options,
                selectedId = "a",
                onSelected = {},
                label = "Selecciona",
                dialogTitle = "Opciones",
                confirmLabel = "Aceptar",
                dismissLabel = "Cerrar",
            )
            AqCheckbox(label = "Adjuntar comprobante", checked = true, onCheckedChange = {})
            AqSwitch(label = "Sincronización activa", checked = true, onCheckedChange = {})
            AqProgressBar(progress = AqProgress(0.6f), contentDescription = "Progreso")
            AqSpinner(contentDescription = "Cargando")
            AqEvidenceBadge(text = "Comprobante recibido", icon = AqIcons.Attachment)
            AqSyncIndicator(
                state = AqSyncState.Pending,
                labels = AqSyncLabels("Sincronizado", "Pendiente", "Sincronizando", "Error", "Sin servicio"),
            )
            AqIndexTable(
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
                        icon = com.agendaqr.core.ui.components.AqIcons.Trash,
                        onAction = {},
                    ),
                ),
            ) {
                com.agendaqr.core.ui.components.AqListRow(
                    title = "Elemento deslizable",
                    subtitle = "Acciones al deslizar",
                )
            }
            AqPullToRefresh(
                isRefreshing = false,
                onRefresh = {},
                refreshingLabel = "Actualizando",
            ) {
                com.agendaqr.core.ui.components.AqListRow(
                    title = "Lista actualizable",
                    subtitle = "Arrastre vertical",
                )
            }
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
