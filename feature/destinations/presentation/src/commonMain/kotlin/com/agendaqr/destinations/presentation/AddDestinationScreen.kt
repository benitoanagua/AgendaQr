package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqMoonIconBadge
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMoonListBody
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType

/**
 * S02 — Añadir.
 *
 * Esta pantalla solo decide cómo traer un recurso existente. No pide nombre,
 * categoría ni contexto antes de que Agenda QR haya entendido el recurso.
 * La revisión ocurre después de la clasificación (S09).
 *
 * Composición de S02 según la especificación UX/UI: héroe centrado y
 * un único grupo de métodos en el orden de la jerarquía que la propia spec
 * declara — Galería → Cámara → Archivo (PDF) — con "Desde otra app" como
 * ayuda breve al pie, solo donde la plataforma soporta compartir hacia
 * Agenda QR. La galería es la acción primaria; el resto son alternativas y
 * no compiten por el peso visual.
 */
@Composable
fun AddDestinationScreen(
    appHosts: AppHosts? = null,
    onImport: (List<com.agendaqr.destinations.domain.QrAsset>) -> Unit,
    onBack: () -> Unit,
) {
    // V1.4 (§12): la flecha atrás vive en la barra superior (la misma en
    // ambas plataformas).
    AqScreenScaffold(
        scrollable = true,
        topBar = {
            AqTopBar(
                title = AppStrings.Anadir,
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Xl),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            AqMoonIconBadge(icon = AqIcons.QrCode)
            AqText(
                AppStrings.TraeAAgendaQrAlgo,
                color = AqColor.TextSecondary,
            )
        }

        // Grupo único de métodos (S02, V1.17): el orden sigue la
        // jerarquía declarada por la spec (Galería → Cámara) y el PDF queda
        // al pie del grupo. Divisores entre filas, ninguno tras la última.
        AqMoonListBody {
            QrGalleryControls(
                onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) },
            )
            QrCameraEntryControl(
                onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) },
                showDivider = true,
            )
            QrPdfPickerControl(showDivider = false)
        }

        if (sharedImportsSupported()) {
            AqText(
                AppStrings.shareFromAnotherAppHint,
                size = AqType.Caption,
                color = AqColor.TextSecondary,
            )
        }
    }
}
