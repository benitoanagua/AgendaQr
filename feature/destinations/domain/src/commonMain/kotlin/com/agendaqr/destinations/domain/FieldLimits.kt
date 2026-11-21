package com.agendaqr.destinations.domain

/**
 * Límites de longitud de los campos de texto libre del dominio.
 *
 * Se definen aquí (capa domain, sin dependencias de UI) para que la
 * validación no dependa de la pantalla y los ViewModels la reutilicen.
 *
 * Los valores ya guardados que superen el límite se conservan: el límite
 * solo se aplica al escribir/editar, nunca bloquea datos existentes.
 */
object FieldLimits {
    val Nombre = 80
    val Categoria = 40
    val Nota = 500
    val Concepto = 120
    val PersonaOEntidad = 80
    /** Estándar RFC para direcciones de correo. */
    val Correo = 254
    /** Tope razonable para contraseñas de acceso local. */
    val Contrasena = 128
    /** Importe de actividad: cifras + opcional separador decimal. */
    val Importe = 20
}
