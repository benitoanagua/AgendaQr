package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqBottomAction
import com.agendaqr.core.ui.components.AqIconBadge
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqKeypad
import com.agendaqr.core.ui.components.AqPinDots
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqSecondaryButton
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.destinations.domain.PinPolicy

/**
 * Paso de la configuración del PIN.
 */
private enum class PinSetupStep { Create, Confirm }

/**
 * Primer arranque: el perfil local ya existe (se crea sin conexión) y aquí el
 * usuario configura su PIN. No es un login: no hay correo, contraseña ni red.
 *
 * Composición del desbloqueo (S00, spec V1.16): héroe,
 * puntos enmascarados y teclado propio en dos pasos — crear el código y
 * confirmarlo — con la acción de cada paso fija al pie. El paso de creación
 * exige la longitud mínima de [PinPolicy] antes de continuar; borrar con la
 * confirmación vacía vuelve al paso anterior sin perder el código.
 *
 * Desde aquí también se puede entrar al modo de demostración (datos
 * ficticios aislados) sin configurar PIN ni cuenta.
 */
@Composable
internal fun LocalOnboardingScreen(
    state: LocalAccessUiState,
    onPinChanged: (String) -> Unit,
    onConfirmationChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onExploreDemo: (() -> Unit)? = null,
) {
    var step by remember { mutableStateOf(PinSetupStep.Create) }
    var shortPinReported by remember { mutableStateOf(false) }
    val confirming = step == PinSetupStep.Confirm
    val active = if (confirming) state.confirmation else state.pin
    val onActiveChanged = if (confirming) onConfirmationChanged else onPinChanged

    fun typeDigit(digit: Char) {
        shortPinReported = false
        onActiveChanged(active + digit)
    }

    fun backspace() {
        shortPinReported = false
        if (active.isNotEmpty()) {
            onActiveChanged(active.dropLast(1))
        } else if (confirming) {
            // Borrar con la confirmación vacía vuelve a crear el código.
            step = PinSetupStep.Create
        }
    }

    // §11: el error vive como texto — el corto local del paso de creación o
    // el error del gate (no coinciden, bloqueo...).
    val errorText: String? = when {
        shortPinReported -> AppStrings.PinErrorCorto
        state.error == LocalAccessError.Mismatch -> AppStrings.PinErrorNoCoinciden
        else -> state.error.pinFieldMessage()
    }

    AqScreenScaffold(
        scrollable = true,
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        // La acción del paso queda fija al pie, sobre la barra del sistema.
        bottomAction = {
            if (confirming) {
                AqBottomAction(label = AppStrings.GuardarPin, onClick = onSubmit)
            } else {
                AqBottomAction(
                    label = AppStrings.Continuar,
                    onClick = {
                        if (state.pin.length < PinPolicy.MIN_LENGTH) {
                            // La longitud mínima es regla del dominio; el gate
                            // vuelve a validar en el envío.
                            shortPinReported = true
                        } else {
                            step = PinSetupStep.Confirm
                        }
                    },
                )
            }
        },
    ) {
        AqIconBadge(icon = AqIcons.Lock, tone = AqTone.Info)
        Column(
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AqText(
                if (confirming) AppStrings.ConfirmaElPin else AppStrings.ConfiguraTuPin,
                size = AqType.Title,
                fontWeight = AqWeight.SemiBold,
            )
            if (!confirming) {
                AqText(
                    AppStrings.ConfiguraTuPinDescripcion,
                    size = AqType.Body,
                    color = AqColor.TextSecondary,
                )
            }
        }
        AqPinDots(
            filled = active.length,
            total = active.length
                .coerceAtLeast(PinPolicy.MIN_LENGTH)
                .coerceAtMost(PinPolicy.MAX_LENGTH),
        )
        errorText?.let { message ->
            AqText(
                message,
                size = AqType.Body,
                color = AqColor.Danger,
            )
        }
        AqKeypad(
            onDigit = ::typeDigit,
            onBackspace = ::backspace,
            backspaceDescription = AppStrings.BorrarDigito,
        )
        if (onExploreDemo != null) {
            AqText(
                AppStrings.ExplorarDemostracionDescripcion,
                size = AqType.Body,
                color = AqColor.TextSecondary,
            )
            AqSecondaryButton(
                AppStrings.ExplorarDemostracion,
                onClick = onExploreDemo,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
