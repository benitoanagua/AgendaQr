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
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmElevation
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmShapes
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.amElevation

/**
 * Hoja inferior modal equivalente a un bottom sheet nativo (D-04).
 * Usa el modal del sistema con scrim semántico, superficie elevada con
 * tokens y targets táctiles de 48 dp. Todo el copy llega del llamador.
 */
@Composable
fun AmBottomSheet(
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
                .background(AmColor.Scrim)
                .clickable(onClick = onDismissRequest),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .amElevation(AmElevation.Shadow3, AmShapes.BottomSheet)
                    .background(AmColor.Surface, AmShapes.BottomSheet)
                    .verticalScroll(rememberScrollState())
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = AmSpacing.Md)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .width(AmSpacing.Xxxl)
                            .height(AmSpacing.Xs)
                            .background(AmColor.Divider, AmShapes.Pill),
                    )
                }
                AmHeading(
                    text = title,
                    size = com.agendaqr.core.ui.theme.AmType.Title,
                    modifier = Modifier.padding(horizontal = AmSpacing.Lg),
                )
                Spacer(Modifier.height(AmSpacing.Md))
                Box(modifier = Modifier.padding(horizontal = AmSpacing.Lg)) { content() }
                Spacer(Modifier.height(AmSpacing.Lg))
                AmTextAction(
                    label = dismissDescription,
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AmSpacing.Lg)
                        .defaultMinSize(minHeight = AmMetrics.ControlMinSize),
                )
                Spacer(Modifier.height(AmSpacing.Lg))
            }
        }
    }
}

/** Overlay modal genérico: reutiliza [AmBottomSheet] para no duplicar scrim ni foco. */
@Composable
fun AmModalSheet(
    title: String,
    dismissDescription: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AmBottomSheet(
        title = title,
        dismissDescription = dismissDescription,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        content = content,
    )
}
