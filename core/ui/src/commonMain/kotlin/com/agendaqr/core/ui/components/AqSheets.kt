package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqElevation
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.aqElevation

/**
 * Hoja inferior modal equivalente a un bottom sheet nativo (D-04).
 * Usa el modal del sistema con scrim semántico, superficie elevada con
 * tokens y targets táctiles de 48 dp. Todo el copy llega del llamador.
 */
@Composable
fun AqBottomSheet(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissDescription: String,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AqColor.Scrim)
                .clickable(onClick = onDismissRequest),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .aqElevation(AqElevation.Shadow3, AqShapes.BottomSheet)
                    .background(AqColor.Surface, AqShapes.BottomSheet)
                    .verticalScroll(rememberScrollState())
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = AqSpacing.Md)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .width(AqSpacing.Xxxl)
                            .height(AqSpacing.Xs)
                            .background(AqColor.Divider, AqShapes.Pill),
                    )
                }
                AqHeading(
                    text = title,
                    size = com.agendaqr.core.ui.theme.AqType.Title,
                    modifier = Modifier.padding(horizontal = AqSpacing.Lg),
                )
                Spacer(Modifier.height(AqSpacing.Md))
                Box(modifier = Modifier.padding(horizontal = AqSpacing.Lg)) { content() }
                Spacer(Modifier.height(AqSpacing.Lg))
                AqTextAction(
                    label = dismissDescription,
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AqSpacing.Lg)
                        .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                )
                Spacer(Modifier.height(AqSpacing.Lg))
            }
        }
    }
}

/** Overlay modal genérico: reutiliza [AqBottomSheet] para no duplicar scrim ni foco. */
@Composable
fun AqModalSheet(
    title: String,
    dismissDescription: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AqBottomSheet(
        title = title,
        dismissDescription = dismissDescription,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        content = content,
    )
}
