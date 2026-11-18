package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.agendaqr.core.ui.theme.AqColor

@Stable
internal data class AqActionSheetRequest(
    val actions: List<AqOverflowAction>,
    val dismissLabel: String,
)

@Stable
internal class AqActionSheetController {
    var request by mutableStateOf<AqActionSheetRequest?>(null)
        private set

    fun show(actions: List<AqOverflowAction>, dismissLabel: String) {
        require(actions.isNotEmpty()) { "Action-sheet necesita al menos una acción" }
        require(dismissLabel.isNotBlank()) { "Action-sheet necesita una etiqueta para cerrar" }
        request = AqActionSheetRequest(actions, dismissLabel)
    }

    fun dismiss() {
        request = null
    }
}

internal val LocalAmActionSheetController =
    compositionLocalOf<AqActionSheetController?> { null }

@Composable
internal fun AqActionSheet(
    request: AqActionSheetRequest,
    onDismissRequest: () -> Unit,
) {
    AqDialog(
        title = request.dismissLabel,
        confirmLabel = request.dismissLabel,
        onConfirm = onDismissRequest,
        onDismiss = onDismissRequest,
        confirmAsText = true,
        content = {
            AqDialogList {
                request.actions.forEach { action ->
                    AqListRow(
                        title = action.label,
                        leading = action.icon?.let { icon ->
                            {
                                AqIcon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = AqColor.EmphasisIcon,
                                )
                            }
                        },
                        onClick = {
                            action.onClick()
                            onDismissRequest()
                        },
                    )
                }
            }
        },
    )
}
