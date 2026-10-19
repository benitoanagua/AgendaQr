package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AmIconBadge
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.components.AmListBody
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType

/**
 * S02 — Añadir (V1.3, ADR-0013; composición V1.17).
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
    AmScreenScaffold(
        scrollable = true,
        topBar = {
            AmTopBar(
                title = AppStrings.Anadir,
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Xl),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
        ) {
            AmIconBadge(icon = AmIcons.QrCode, tone = AmTone.Info)
            AmText(
                AppStrings.TraeAAgendaQrAlgo,
                color = AmColor.TextSecondary,
            )
        }

        // Grupo único de métodos (S02, V1.17): el orden sigue la
        // jerarquía declarada por la spec (Galería → Cámara) y el PDF queda
        // al pie del grupo. Divisores entre filas, ninguno tras la última.
        AmListBody {
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
            AmText(
                AppStrings.shareFromAnotherAppHint,
                size = AmType.Caption,
                color = AmColor.TextSecondary,
            )
        }
    }
}
