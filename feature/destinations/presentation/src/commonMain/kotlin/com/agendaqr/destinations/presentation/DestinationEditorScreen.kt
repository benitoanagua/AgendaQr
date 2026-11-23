package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSwitch
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.QrAsset
import com.agendaqr.destinations.domain.nowMillis
import com.agendaqr.destinations.presentation.UserFacingError

/** Editor de destino (crear/editar). Guarda a través de [onSave] y expone
 *  estados de carga/error. Todo el copy vive en [AppStrings]. */
@Composable
fun DestinationEditorScreen(
    existing: Destination?,
    appHosts: AppHosts? = null,
    onSave: () -> Unit,
    onImportMany: () -> Unit = {},
    onBack: () -> Unit = {},
    isSaving: Boolean = false,
    error: UserFacingError? = null,
    onClearError: () -> Unit = {},
    onRetryError: () -> Unit = {},
    onCreateContext: (String, String?) -> Unit = { _, _ -> },
    justCreatedContextId: String? = null,
) {
    val isNew = existing == null
    val name = existing?.name.orEmpty()
    val note = existing?.note.orEmpty()
    val favorite = existing?.favorite ?: false
    val contextId = existing?.contextId
    val toastHost = appHosts?.toastHost
    val saveLabel = if (isSaving) AppStrings.Guardando else AppStrings.Guardar
    AqScreen(
        title = if (isNew) AppStrings.CrearDestino else AppStrings.EditarDestino,
        onBack = onBack,
        backLabel = AppStrings.Volver,
        statusContent = appHosts?.status,
        bottomAction = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AqButton(
                    label = saveLabel,
                    onClick = onSave,
                    variant = AqButtonVariant.Primary,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        error?.let { err ->
            AqAlert(
                tone = AqTone.Danger,
                message = err.display(),
                dismissLabel = AppStrings.Cerrar,
                onDismiss = onClearError,
            )
        }
        AqTextInput(
            label = AppStrings.LabelNombre,
            value = name,
            onValueChange = { },
        )
        AqTextInput(
            label = AppStrings.LabelNota,
            value = note,
            onValueChange = { },
        )
        AqMenuItem(
            title = contextId?.takeIf { it.isNotEmpty() }?.let { AppStrings.CtxPrefix + it } ?: AppStrings.SinContexto,
            subtitle = AppStrings.Opcional,
            onClick = { onCreateContext(contextId.orEmpty(), justCreatedContextId) },
            leading = {
                AqTag(text = contextId?.take(1)?.uppercase() ?: AppStrings.UnknownMarker)
            },
        )
        AqSwitch(
            label = AppStrings.MarcarComoFavorito,
            checked = favorite,
            onCheckedChange = { },
        )
        if (isNew) {
            AqButton(
                label = AppStrings.CrearContexto,
                onClick = { onCreateContext("", null) },
                variant = AqButtonVariant.Text,
            )
        }
    }
}
