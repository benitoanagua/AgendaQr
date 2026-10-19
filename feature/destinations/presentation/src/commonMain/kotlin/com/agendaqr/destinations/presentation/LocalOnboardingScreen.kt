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
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.AmBottomAction
import com.agendaqr.core.ui.components.AmIconBadge
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.components.AmKeypad
import com.agendaqr.core.ui.components.AmPinDots
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType
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

    AmScreenScaffold(
        scrollable = true,
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        // La acción del paso queda fija al pie, sobre la barra del sistema.
        bottomAction = {
            if (confirming) {
                AmBottomAction(label = AppStrings.GuardarPin, onClick = onSubmit)
            } else {
                AmBottomAction(
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
        AmIconBadge(icon = AmIcons.Lock, tone = AmTone.Info)
        Column(
            verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AmText(
                if (confirming) AppStrings.ConfirmaElPin else AppStrings.ConfiguraTuPin,
                size = AmType.Title,
                fontWeight = FontWeight.SemiBold,
            )
            if (!confirming) {
                AmText(
                    AppStrings.ConfiguraTuPinDescripcion,
                    size = AmType.Body,
                    color = AmColor.TextSecondary,
                )
            }
        }
        AmPinDots(
            filled = active.length,
            total = active.length
                .coerceAtLeast(PinPolicy.MIN_LENGTH)
                .coerceAtMost(PinPolicy.MAX_LENGTH),
        )
        errorText?.let { message ->
            AmText(
                message,
                size = AmType.Body,
                color = AmColor.Danger,
            )
        }
        AmKeypad(
            onDigit = ::typeDigit,
            onBackspace = ::backspace,
            backspaceDescription = AppStrings.BorrarDigito,
        )
        if (onExploreDemo != null) {
            AmText(
                AppStrings.ExplorarDemostracionDescripcion,
                size = AmType.Body,
                color = AmColor.TextSecondary,
            )
            AmSecondaryButton(
                AppStrings.ExplorarDemostracion,
                onClick = onExploreDemo,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
