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
import com.agendaqr.core.ui.components.AmBlockStack
import com.agendaqr.core.ui.components.AmButtonGroup
import com.agendaqr.core.ui.components.AmCheckbox
import com.agendaqr.core.ui.components.AmChoiceOption
import com.agendaqr.core.ui.components.AmEvidenceBadge
import com.agendaqr.core.ui.components.AmGroupedAction
import com.agendaqr.core.ui.components.AmHeading
import com.agendaqr.core.ui.components.AmIndexTable
import com.agendaqr.core.ui.components.AmLink
import com.agendaqr.core.ui.components.AmProgressBar
import com.agendaqr.core.ui.components.AmProgress
import com.agendaqr.core.ui.components.AmPrimaryButton
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmSelect
import com.agendaqr.core.ui.components.AmSpinner
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmSwitch
import com.agendaqr.core.ui.components.AmSyncIndicator
import com.agendaqr.core.ui.components.AmSyncLabels
import com.agendaqr.core.ui.components.AmSyncState
import com.agendaqr.core.ui.components.AmTableColumn
import com.agendaqr.core.ui.components.AmTableRow
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.components.AmIcons
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Galería mínima para revisar los roles semánticos en los tres estados. */
@Composable
private fun AmTokenGallery(darkTheme: Boolean) {
    AmTheme(darkTheme = darkTheme) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AmSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(AmSpacing.Md),
        ) {
            AmHeading("Sistema visual")
            AmText(
                text = "Comprobante recibido",
                color = AmColor.TextSecondary,
            )
            AmStatusBanner(
                message = "Comprobante recibido",
                tone = AmTone.Info,
            )
            AmPrimaryButton(label = "Continuar", onClick = {})
            AmSecondaryButton(label = "Cancelar", onClick = {})
        }
    }
}

@Preview
@Composable
internal fun AmTokenPreviewLight() {
    AmTokenGallery(darkTheme = false)
}

@Preview
@Composable
internal fun AmTokenPreviewDark() {
    AmTokenGallery(darkTheme = true)
}

@Preview
@Composable
internal fun AmTokenPreviewFont200() {
    CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 2f)) {
        AmTokenGallery(darkTheme = false)
    }
}

/** Galería de la capa aditiva: controles, acciones, datos y extensiones. */
@Composable
private fun AmComponentGallery(darkTheme: Boolean) {
    val options = listOf(
        AmChoiceOption("a", "Opción A", "Descripción A"),
        AmChoiceOption("b", "Opción B", "Descripción B"),
    )
    AmTheme(darkTheme = darkTheme) {
        AmBlockStack(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AmSpacing.Lg),
        ) {
            AmHeading("Componentes")
            AmLink(label = "Abrir detalle", onClick = {})
            AmButtonGroup(
                actions = listOf(
                    AmGroupedAction("cancel", "Cancelar", onClick = {}),
                    AmGroupedAction("save", "Guardar", onClick = {}, emphasized = true),
                ),
            )
            AmSelect(
                options = options,
                selectedId = "a",
                onSelected = {},
                label = "Selecciona",
                dialogTitle = "Opciones",
                confirmLabel = "Aceptar",
                dismissLabel = "Cerrar",
            )
            AmCheckbox(label = "Adjuntar comprobante", checked = true, onCheckedChange = {})
            AmSwitch(label = "Sincronización activa", checked = true, onCheckedChange = {})
            AmProgressBar(progress = AmProgress(0.6f), contentDescription = "Progreso")
            AmSpinner(contentDescription = "Cargando")
            AmEvidenceBadge(text = "Comprobante recibido", icon = AmIcons.Attachment)
            AmSyncIndicator(
                state = AmSyncState.Pending,
                labels = AmSyncLabels("Sincronizado", "Pendiente", "Sincronizando", "Error", "Sin servicio"),
            )
            AmIndexTable(
                columns = listOf(
                    AmTableColumn("name", "Concepto"),
                    AmTableColumn("amount", "Importe"),
                ),
                rows = listOf(AmTableRow("1", mapOf("name" to "Cobro", "amount" to "10,00"))),
            )
        }
    }
}

@Preview
@Composable
internal fun AmComponentPreviewLight() = AmComponentGallery(darkTheme = false)

@Preview
@Composable
internal fun AmComponentPreviewDark() = AmComponentGallery(darkTheme = true)

@Preview
@Composable
internal fun AmComponentPreviewFont200() {
    CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 2f)) {
        AmComponentGallery(darkTheme = false)
    }
}
