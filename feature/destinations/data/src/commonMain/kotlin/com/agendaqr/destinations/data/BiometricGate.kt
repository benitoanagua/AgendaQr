package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.BiometricGate

/**
 * Puerta biométrica de plataforma.
 *
 * Android: disponibilidad real vía framework `BiometricManager`; el prompt
 * (`BiometricPrompt` ligado a la Activity en primer plano) requiere
 * dispositivo y queda pendiente de verificación (ver actual Android).
 * iOS: `LocalAuthentication` pendiente de Xcode/dispositivo.
 */
expect fun platformBiometricGate(): BiometricGate
