package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    AmScreenScaffold(
        scrollable = true,
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        // Desbloquear fijo al pie: el teclado propio no desplaza la acción
        // primaria y esta nunca queda bajo la barra del sistema.
        bottomAction = {
            AmBottomAction(label = AppStrings.Desbloquear, onClick = onSubmit)
        },
    ) {
        // Héroe centrado: escudo de acceso con el glifo del candado.
        AmIconBadge(icon = AmIcons.Lock, tone = AmTone.Info)
        Column(
            verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AmText(
                AppStrings.DesbloqueaAgendaQr,
                size = AmType.Title,
                fontWeight = FontWeight.SemiBold,
            )
            AmText(
                AppStrings.DesbloqueaAgendaQrDescripcion,
                size = AmType.Body,
                color = AmColor.TextSecondary,
            )
        }
        // Puntos enmascarados: crecen con la entrada hasta el máximo.
        AmPinDots(
            filled = state.pin.length,
            total = state.pin.length
                .coerceAtLeast(PinPolicy.MIN_LENGTH)
                .coerceAtMost(PinPolicy.MAX_LENGTH),
        )
        // §10/§11: el error y los intentos son texto, nunca solo color.
        state.error.pinFieldMessage()?.let { message ->
            AmText(
                message,
                size = AmType.Body,
                color = AmColor.Danger,
            )
        }
        if (state.remainingAttempts > 0) {
            AmText(
                AppStrings.IntentosRestantes + state.remainingAttempts,
                size = AmType.BodySmall,
                color = AmColor.TextSecondary,
            )
        }
        AmKeypad(
            onDigit = { digit -> onPinChanged(state.pin + digit) },
            onBackspace = {
                if (state.pin.isNotEmpty()) onPinChanged(state.pin.dropLast(1))
            },
            backspaceDescription = AppStrings.BorrarDigito,
        )
        if (state.biometricAvailability == BiometricAvailability.Available && onBiometric != null) {
            // U-20/D11: solicitud automática UNA vez por bloqueo (clave por
            // bloqueo, no por recomposición); Cancelled no relanza (el
            // estado no cambia) y el botón queda como respaldo.
            LaunchedEffect(state.lockSequence) {
                onBiometric()
            }
            AmSecondaryButton(
                AppStrings.UsarBiometria,
                onClick = onBiometric,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (state.biometricFallbackVisible) {
            AmText(
                AppStrings.BiometriaNoDisponible,
                size = AmType.Body,
                color = AmColor.TextSecondary,
            )
        }
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
