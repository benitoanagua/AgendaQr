package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaHeading
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaAppBar
import com.agendaqr.core.ui.components.XauxaAppBarAction
import com.agendaqr.core.ui.components.XauxaIcons
import com.agendaqr.core.ui.components.XauxaPageTitle
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.destinations.domain.QrAsset

@Composable
fun ImportReviewScreen(
    assets: List<QrAsset>,
    onSaveAll: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isSaving: Boolean = false,
    error: UserFacingError? = null,
    onClearError: () -> Unit = {},
) {
    // V1.1 (M6/M7 + defecto T7): título ligero; Guardar como acción
    // principal de la app bar (nunca recortada) y Volver en la flecha.
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding(),
    ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = XauxaSpacing.ScreenMargin),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        XauxaPageTitle(text = AppStrings.RevisarQr)
        error?.let { err ->
            XauxaStatusBanner(
                err.display(),
                tone = XauxaTone.Danger,
                actionLabel = err.action.label,
                // REINTENTAR guarda los importados de nuevo (el lote sigue
                // retenido en el VM); el resto de acciones solo descarta.
                onAction = { if (err.action == ErrorAction.Retry) onSaveAll() else onClearError() },
                onDismiss = onClearError,
            )
        }
        if (assets.isEmpty()) {
            XauxaEmptyState(
                title = AppStrings.NoHayQrParaRevisar,
                subtitle = AppStrings.VuelveEImportaDesdeCamara,
                actionLabel = AppStrings.Volver,
                onAction = onBack,
            )
        } else {
            XauxaText(
                if (assets.size == 1) "1 QR listo para guardar" else "${assets.size} QR listos para guardar",
                size = XauxaType.Label,
                color = XauxaColor.TextSecondary,
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
            ) {
                itemsIndexed(assets) { _, asset ->
                    // S09/T7: el código va centrado (antes quedaba pegado a
                    // la izquierda) y sin depender de métricas técnicas.
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        XauxaQrPreview(asset.encoded)
                    }
                    // S09: evidencia de lo que Agenda QR entendió — el texto
                    // decodificado, sin exponer payload técnico.
                    asset.content?.takeIf { it.isNotBlank() }?.let { content ->
                        XauxaText(
                            AppStrings.Contenido + ": " + content,
                            size = XauxaType.Label,
                            color = XauxaColor.TextSecondary,
                        )
                    }
                }
            }
        }
    }
    XauxaAppBar(
        modifier = Modifier.navigationBarsPadding(),
        actions = listOf(
            XauxaAppBarAction(
                label = when {
                    isSaving -> AppStrings.Guardando
                    assets.size == 1 -> "Guardar"
                    else -> "Guardar todo"
                },
                icon = XauxaIcons.Save,
                primary = true,
                enabled = !isSaving && assets.isNotEmpty(),
                onClick = onSaveAll,
            ),
        ),
        onBack = if (isSaving) null else onBack,
        backLabel = AppStrings.Volver,
    )
    }
}
