package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AmBadge
import com.agendaqr.core.ui.components.AmFeedbackHost
import com.agendaqr.core.ui.components.AmBannerAction
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.theme.AmSpacing

/**
 * Hosts compartidos de la sesión (§12 V1.4): el estado persistente que se
 * renderiza bajo la barra superior de cada superficie y el host del
 * Snackbar para eventos transitorios. La superficie activa monta ambos en
 * su Scaffold.
 */
class AppHosts(
    /** Estado persistente (banners/indicadores compactos) bajo la barra. */
    val status: (@Composable () -> Unit)? = null,
    /** Host del Snackbar para eventos transitorios. */
    val feedback: AmFeedbackHost? = null,
)

/**
 * AppStatusHost (§12 V1.4): el ÚNICO punto donde viven los estados
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
            val (bannerAction, bannerDismiss) = error.bannerActions(
                onRetry = onDismissImportError,
                onClear = onDismissImportError,
            )
            // Host edge-to-edge (slot statusContent, sin margen del
            // contenido): el banner pide SU margen de pantalla para quedar
            // al ancho útil con un solo margen, igual que los badges.
            AmStatusBanner(
                title = error.what,
                message = error.dataStatus,
                tone = AmTone.Danger,
                action = bannerAction,
                dismiss = bannerDismiss,
                screenMargin = AmSpacing.ScreenMargin,
            )
        } else if (hasFailed && pendingCount > 0) {
            // Banner completo SOLO con mutaciones FAILED: el usuario debe
            // poder reintentar AHORA (los datos están a salvo localmente).
            AmStatusBanner(
                message = syncPendingLabel(pendingCount, retrying = true),
                tone = AmTone.Danger,
                action = AmBannerAction(AppStrings.ReintentarAhora, onRetryNow),
                screenMargin = AmSpacing.ScreenMargin,
            )
        } else if (isOffline || pendingCount > 0) {
            // Indicador compacto: chips con texto (el significado nunca
            // depende solo del color, §11).
            Row(
                modifier = Modifier.padding(
                    start = AmSpacing.ScreenMargin,
                    end = AmSpacing.ScreenMargin,
                    top = AmSpacing.Sm,
                ),
                horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isOffline) {
                    AmBadge(text = AppStrings.SinConexion, tone = AmTone.Warning, round = true)
                }
                if (pendingCount > 0) {
                    AmBadge(
                        text = syncPendingLabel(pendingCount, retrying = false),
                        tone = AmTone.Info,
                        round = true,
                    )
                }
            }
        }
    }
}
