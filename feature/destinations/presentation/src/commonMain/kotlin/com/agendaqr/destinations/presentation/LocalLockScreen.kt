package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqBottomAction
import com.agendaqr.core.ui.components.AqMoonIconBadge
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
import com.agendaqr.destinations.domain.BiometricAvailability
import com.agendaqr.destinations.domain.PinPolicy

/**
 * Desbloqueo local (composición S00, spec V1.16).
 * Sustituye al login remoto como puerta de entrada: se muestra cuando ya
 * existe un PIN configurado y la app está bloqueada.
 *
 * La entrada es propia (puntos + teclado numérico), sin IME del sistema; el
 * envío sigue siendo explícito porque el PIN es de longitud variable
 * (4–12). Los errores y los intentos restantes viven como texto (§11).
 *
 * La biometría es un atajo opcional (solo cuando está disponible e inscrita)
 * y permanece como acción de texto (spec S00): el PIN es siempre la vía
 * principal y el fallback ante cualquier fallo.
 */
@Composable
internal fun LocalLockScreen(
    state: LocalAccessUiState,
    onPinChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onBiometric: (() -> Unit)? = null,
    onExploreDemo: (() -> Unit)? = null,
) {
    AqScreenScaffold(
        scrollable = true,
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        // Desbloquear fijo al pie: el teclado propio no desplaza la acción
        // primaria y esta nunca queda bajo la barra del sistema.
        bottomAction = {
            AqBottomAction(label = AppStrings.Desbloquear, onClick = onSubmit)
        },
    ) {
        // Héroe centrado: escudo de acceso con el glifo del candado.
        AqMoonIconBadge(icon = AqIcons.Lock)
        Column(
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AqText(
                AppStrings.DesbloqueaAgendaQr,
                size = AqType.Title,
                fontWeight = AqWeight.SemiBold,
            )
            AqText(
                AppStrings.DesbloqueaAgendaQrDescripcion,
                size = AqType.Body,
                color = AqColor.TextSecondary,
            )
        }
        // Puntos enmascarados: crecen con la entrada hasta el máximo.
        AqPinDots(
            filled = state.pin.length,
            total = state.pin.length
                .coerceAtLeast(PinPolicy.MIN_LENGTH)
                .coerceAtMost(PinPolicy.MAX_LENGTH),
            error = state.error.isPinFieldError(),
        )
        // §10/§11: el error y los intentos son texto, nunca solo color.
        state.error.pinFieldMessage()?.let { message ->
            AqText(
                message,
                size = AqType.Body,
                color = AqColor.Danger,
            )
        }
        if (state.remainingAttempts > 0) {
            AqText(
                AppStrings.IntentosRestantes + state.remainingAttempts,
                size = AqType.BodySmall,
                color = AqColor.TextSecondary,
            )
        }
        AqKeypad(
            onDigit = { digit -> onPinChanged(state.pin + digit) },
            onBackspace = {
                if (state.pin.isNotEmpty()) onPinChanged(state.pin.dropLast(1))
            },
            backspaceDescription = AppStrings.BorrarDigito,
        )
        if (state.biometricAvailability == BiometricAvailability.Available && onBiometric != null) {
            // Solicitud automática UNA vez por bloqueo (clave por
            // bloqueo, no por recomposición); Cancelled no relanza (el
            // estado no cambia) y el botón queda como respaldo.
            LaunchedEffect(state.lockSequence) {
                onBiometric()
            }
            AqSecondaryButton(
                AppStrings.UsarBiometria,
                onClick = onBiometric,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (state.biometricFallbackVisible) {
            AqText(
                AppStrings.BiometriaNoDisponible,
                size = AqType.Body,
                color = AqColor.TextSecondary,
            )
        }
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
