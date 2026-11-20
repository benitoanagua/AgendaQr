package com.agendaqr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester

/**
 * Contrato de validación en línea de formularios: foco al primer error.
 *
 * Los campos se registran en el ORDEN VISUAL del formulario. Al intentar
 * enviar, el llamador calcula qué campos tienen error y pide el foco para
 * el primero. La lógica de decisión es pura ([firstErrorKey]) para poder
 * probarse sin Compose; [AqFormFocus] solo asocia claves con
 * [FocusRequester].
 *
 * Nivel de convención (ADR-0029): Nivel 1 Polaris (`TextField` con error) y
 * Nivel 2 `MoonErrorMessage` para el mensaje; el foco automático al primer
 * error es convención nativa (Nivel 3), estilizada sin cambios visuales.
 */
internal fun firstErrorKey(order: List<String>, errors: Set<String>): String? =
    order.firstOrNull { it in errors }

/**
 * Registro de solicitantes de foco por clave de campo.
 *
 * Uso:
 * ```
 * val focus = rememberAqFormFocus("email", "password")
 * AqTextField(..., focusRequester = focus.requesterFor("email"))
 * // al enviar:
 * focus.focusFirstError(errors = setOf("password"))
 * ```
 */
class AqFormFocus internal constructor(private val order: List<String>) {
    private val requesters: Map<String, FocusRequester> =
        order.associateWith { FocusRequester() }

    fun requesterFor(key: String): FocusRequester =
        requireNotNull(requesters[key]) { "AqFormFocus: campo no registrado: $key" }

    /**
     * Pide el foco para el primer campo (en orden visual) con error.
     * Devuelve la clave enfocada, o `null` si no hay errores.
     */
    fun focusFirstError(errors: Set<String>): String? {
        val key = firstErrorKey(order, errors) ?: return null
        // El campo puede no estar compuesto todavía (p. ej. fuera de la
        // pantalla en una lista perezosa): un fallo de foco nunca debe
        // romper el envío.
        runCatching { requesterFor(key).requestFocus() }
        return key
    }
}

/** Crea y recuerda un [AqFormFocus] para las claves dadas, en orden visual. */
@Composable
fun rememberAqFormFocus(vararg order: String): AqFormFocus {
    val keys = order.toList()
    require(keys.distinct().size == keys.size) { "rememberAqFormFocus: claves duplicadas" }
    return remember(keys) { AqFormFocus(keys) }
}
