package com.agendaqr.destinations.presentation

/**
 * Traducción de [LocalAccessError] a copy. La copia vive en [AppStrings]
 * (única fuente) y este módulo solo la selecciona: la pantalla nunca escribe
 * literales de UI.
 */
internal fun LocalAccessError?.pinFieldMessage(): String? = when (this) {
    LocalAccessError.TooShort -> AppStrings.PinErrorCorto
    LocalAccessError.TooLong -> AppStrings.PinErrorLargo
    LocalAccessError.NonDigits -> AppStrings.PinErrorSoloDigitos
    LocalAccessError.Incorrect -> AppStrings.PinErrorIncorrecto
    LocalAccessError.LockedOut -> AppStrings.PinErrorBloqueado
    LocalAccessError.Mismatch, LocalAccessError.NotConfigured, null -> null
}

internal fun LocalAccessError?.isPinFieldError(): Boolean = pinFieldMessage() != null
