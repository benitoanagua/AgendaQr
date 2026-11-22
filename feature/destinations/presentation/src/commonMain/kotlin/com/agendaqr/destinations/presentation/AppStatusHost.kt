package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqMoonBadge
import com.agendaqr.core.ui.components.AqFeedbackHost
import com.agendaqr.core.ui.components.AqBannerAction
import com.agendaqr.core.ui.components.AqMoonStatusBanner
import com.agendaqr.core.ui.components.AqToastHostState
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqSpacing

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
    val feedback: AqFeedbackHost? = null,
    /** Host de toasts (sustituto Moon-only del Snackbar). */
    val toastHost: AqToastHostState? = null,
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
            // contenido): el HOST aplica el margen de pantalla; el banner
            // (organismo) solo pinta su superficie y su relleno interno.
            AqMoonStatusBanner(
                title = error.what,
                message = error.dataStatus,
                tone = AqTone.Danger,
                action = bannerAction,
                dismiss = bannerDismiss,
                modifier = Modifier.padding(horizontal = AqSpacing.ScreenMargin),
            )
        } else if (hasFailed && pendingCount > 0) {
            // Banner completo SOLO con mutaciones FAILED: el usuario debe
            // poder reintentar AHORA (los datos están a salvo localmente).
            AqMoonStatusBanner(
                message = syncPendingLabel(pendingCount, retrying = true),
                tone = AqTone.Danger,
                action = AqBannerAction(AppStrings.ReintentarAhora, onRetryNow),
                modifier = Modifier.padding(horizontal = AqSpacing.ScreenMargin),
            )
        } else if (isOffline || pendingCount > 0) {
            // Indicador compacto: chips con texto (el significado nunca
            // depende solo del color, §11).
            Row(
                modifier = Modifier.padding(
                    start = AqSpacing.ScreenMargin,
                    end = AqSpacing.ScreenMargin,
                    top = AqSpacing.Sm,
                ),
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isOffline) {
                    AqMoonBadge(text = AppStrings.SinConexion)
                }
                if (pendingCount > 0) {
                    AqMoonBadge(
                        text = syncPendingLabel(pendingCount, retrying = false),
                        tone = AqTone.Info,
                        round = true,
                    )
                }
            }
        }
    }
}
