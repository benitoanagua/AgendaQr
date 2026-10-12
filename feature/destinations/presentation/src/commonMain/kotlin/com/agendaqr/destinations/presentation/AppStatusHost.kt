package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaBadge
import com.agendaqr.core.ui.components.XauxaFeedbackHost
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.theme.XauxaSpacing

/**
 * Hosts compartidos de la sesión (§12 V1.2): el estado persistente que se
 * renderiza bajo la barra superior de cada superficie y el host del
 * Snackbar para eventos transitorios. La superficie activa monta ambos en
 * su Scaffold.
 */
class AppHosts(
    /** Estado persistente (banners/indicadores compactos) bajo la barra. */
    val status: (@Composable () -> Unit)? = null,
    /** Host del Snackbar para eventos transitorios. */
    val feedback: XauxaFeedbackHost? = null,
)

/**
 * AppStatusHost (§12 V1.2): el ÚNICO punto donde viven los estados
 * persistentes transversales, dentro del Scaffold, bajo la barra
 * superior (insets resueltos por el contenedor).
 *
 * Prioridad: el error de importación recuperable (con acción) manda;
 * después, el banner completo de mutaciones FAILED con "Reintentar
 * ahora"; "Sin conexión" y "N pendientes" son indicadores COMPACTOS
 * (badges), no banners a ancho completo.
 */
@Composable
internal fun AppStatusHost(
    importError: String?,
    isOffline: Boolean,
    pendingCount: Int,
    hasFailed: Boolean,
    onDismissImportError: () -> Unit,
    onRetryNow: () -> Unit,
) {
    Column {
        val error = importError?.let { importReadError(it) }
        if (error != null) {
            // Error recuperable de importación (spec §10): la acción despeja
            // el error para volver a intentarlo desde la pantalla de origen.
            XauxaStatusBanner(
                error.display(),
                tone = XauxaTone.Danger,
                actionLabel = error.action.label,
                onAction = onDismissImportError,
                onDismiss = onDismissImportError,
                dismissLabel = AppStrings.Descartar,
            )
        } else if (hasFailed && pendingCount > 0) {
            // Banner completo SOLO con mutaciones FAILED: el usuario debe
            // poder reintentar AHORA (los datos están a salvo localmente).
            XauxaStatusBanner(
                syncPendingLabel(pendingCount, retrying = true),
                tone = XauxaTone.Danger,
                actionLabel = AppStrings.ReintentarAhora,
                onAction = onRetryNow,
            )
        } else if (isOffline || pendingCount > 0) {
            // Indicador compacto: chips con texto (el significado nunca
            // depende solo del color, §11).
            Row(
                modifier = Modifier.padding(
                    start = XauxaSpacing.ScreenMargin,
                    end = XauxaSpacing.ScreenMargin,
                    top = XauxaSpacing.Sm,
                ),
                horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isOffline) {
                    XauxaBadge(text = AppStrings.SinConexion, tone = XauxaTone.Warning)
                }
                if (pendingCount > 0) {
                    XauxaBadge(
                        text = syncPendingLabel(pendingCount, retrying = false),
                        tone = XauxaTone.Info,
                    )
                }
            }
        }
    }
}
