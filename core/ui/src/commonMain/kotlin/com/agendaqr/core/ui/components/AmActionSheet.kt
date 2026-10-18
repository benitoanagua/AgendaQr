package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.agendaqr.core.ui.theme.AmColor

@Stable
internal data class AmActionSheetRequest(
    val actions: List<AmOverflowAction>,
    val dismissLabel: String,
)

@Stable
internal class AmActionSheetController {
    var request by mutableStateOf<AmActionSheetRequest?>(null)
        private set

    fun show(actions: List<AmOverflowAction>, dismissLabel: String) {
        require(actions.isNotEmpty()) { "Action-sheet necesita al menos una acción" }
        require(dismissLabel.isNotBlank()) { "Action-sheet necesita una etiqueta para cerrar" }
        request = AmActionSheetRequest(actions, dismissLabel)
    }

    fun dismiss() {
        request = null
    }
}

internal val LocalAmActionSheetController =
    compositionLocalOf<AmActionSheetController?> { null }

@Composable
internal fun AmActionSheet(
    request: AmActionSheetRequest,
    onDismissRequest: () -> Unit,
) {
    AmDialog(
        title = request.dismissLabel,
        confirmLabel = request.dismissLabel,
        onConfirm = onDismissRequest,
        onDismiss = onDismissRequest,
        confirmAsText = true,
        content = {
            AmDialogList {
                request.actions.forEach { action ->
                    AmListRow(
                        title = action.label,
                        leading = action.icon?.let { icon ->
                            {
                                AmIcon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = AmColor.Accent,
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
