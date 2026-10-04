package com.agendaqr.destinations.presentation

/**
 * Error listo para mostrar (T5) — spec congelada §10:
 * "Todo error debe responder: qué ocurrió, qué pasó con los datos y qué
 * puede hacer el usuario."
 *
 * - [what]: qué ocurrió, en español, sin texto técnico.
 * - [dataStatus]: qué pasó con los datos del usuario.
 * - [action]: qué puede hacer (REINTENTAR, ELEGIR OTRA IMAGEN…).
 *
 * No existe un ErrorScreen global como destino de navegación (§10): el
 * error vive en la superficie donde ocurrió.
 */
data class UserFacingError(
    val what: String,
    val dataStatus: String,
    val action: ErrorAction,
) {
    /** Texto visible: qué ocurrió y qué pasó con los datos. */
    fun display(): String =
        if (dataStatus.isBlank()) what else "$what\n$dataStatus"
}

/** Acciones que un error recuperable puede ofrecer. */
enum class ErrorAction(val label: String) {
    /** Reintentar la operación fallida (o despejar el camino para hacerlo). */
    Retry("REINTENTAR"),

    /** Volver a elegir el archivo/imagen de origen. */
    ChooseAnotherImage("ELEGIR OTRA IMAGEN"),
}

/** Flujos que producen errores recuperables en la app. */
enum class ErrorFlow {
    SaveQr,
    DeleteQr,
    SaveOperation,
    DeleteOperation,
    SaveReceipt,
    DeleteReceipt,
    ComprobanteOpen,
    Search,
    SignIn,
    SignUp,
    SignOut,
    ImportRead,
    ImportBatchSave,
    ContextOpen,
}

/**
 * Clasificación portable de la causa de un error. No se importan tipos de
 * red/almacenamiento (-dependencias de `:data`-): se clasifica por la
 * jerarquía de nombres de las excepciones, que es estable entre plataformas
 * y comprobable en tests sin runtime.
 */
enum class ErrorCause { Network, Auth, UriPermission, Storage, Validation, Unknown }

private val NETWORK_SIGNALS = listOf(
    "IOException", "TimeoutException", "Timeout", "ConnectException", "SocketException",
    "UnresolvedAddress", "UnknownHostException", "HttpRequestTimeout", "ConnectTimeout",
)

private val AUTH_SIGNALS = listOf(
    "RestException", "AuthException", "AuthApiError", "UnauthorizedException",
    "BadRequestException", "HttpError",
)

private val URI_SIGNALS = listOf(
    "SecurityException", "FileNotFoundException", "FileNotFound", "PermissionDeniedException",
)

private val STORAGE_SIGNALS = listOf(
    "StorageException", "SQLiteException", "SQLiteFullException", "DiskIOException",
    "PersistenceException", "SerializationException",
)

private val VALIDATION_SIGNALS = listOf(
    "IllegalArgumentException", "ValidationException", "IllegalStateException",
)

fun Throwable.errorCause(): ErrorCause {
    var current: Throwable? = this
    var depth = 0
    while (current != null && depth < 6) {
        val name = current::class.qualifiedName
            ?: current::class.simpleName
            ?: current.toString()
        val signals = when {
            NETWORK_SIGNALS.any { name.contains(it, ignoreCase = true) } -> ErrorCause.Network
            URI_SIGNALS.any { name.contains(it, ignoreCase = true) } -> ErrorCause.UriPermission
            AUTH_SIGNALS.any { name.contains(it, ignoreCase = true) } -> ErrorCause.Auth
            STORAGE_SIGNALS.any { name.contains(it, ignoreCase = true) } -> ErrorCause.Storage
            VALIDATION_SIGNALS.any { name.contains(it, ignoreCase = true) } -> ErrorCause.Validation
            else -> null
        }
        if (signals != null) return signals
        current = current.cause
        depth++
    }
    return ErrorCause.Unknown
}

/**
 * Mapeo centralizado excepción → error mostrable, por flujo.
 *
 * Reglas:
 * - nunca se muestra `error.message` (puede ser inglés/técnico: los mensajes
 *   provienen de bibliotecas de red, Supabase o el SO);
 * - cada error declara qué pasó con los datos (draft en pantalla, guardado
 *   local, nada guardado) según el modelo local-first;
 * - cada error ofrece una acción.
 */
fun userFacingError(error: Throwable, flow: ErrorFlow): UserFacingError = when (flow) {
    ErrorFlow.SaveQr -> UserFacingError(
        what = "No pudimos guardar el QR.",
        dataStatus = "Tus cambios siguen en la pantalla.",
        action = ErrorAction.Retry,
    )
    ErrorFlow.DeleteQr -> UserFacingError(
        what = "No pudimos eliminar el QR.",
        dataStatus = "El QR sigue guardado.",
        action = ErrorAction.Retry,
    )
    ErrorFlow.SaveOperation -> UserFacingError(
        what = "No pudimos guardar la operación.",
        dataStatus = "Tus cambios siguen en la pantalla.",
        action = ErrorAction.Retry,
    )
    ErrorFlow.DeleteOperation -> UserFacingError(
        what = "No pudimos eliminar la operación.",
        dataStatus = "La operación sigue guardada.",
        action = ErrorAction.Retry,
    )
    ErrorFlow.SaveReceipt -> UserFacingError(
        what = "No pudimos guardar el comprobante.",
        dataStatus = "El comprobante no se guardó.",
        action = ErrorAction.Retry,
    )
    ErrorFlow.DeleteReceipt -> UserFacingError(
        what = "No pudimos eliminar el comprobante.",
        dataStatus = "El comprobante sigue guardado.",
        action = ErrorAction.Retry,
    )
    ErrorFlow.ComprobanteOpen -> UserFacingError(
        what = "No pudimos abrir el comprobante.",
        dataStatus = "El comprobante sigue guardado.",
        action = ErrorAction.Retry,
    )
    ErrorFlow.Search -> UserFacingError(
        what = when (error.errorCause()) {
            ErrorCause.Network -> "No pudimos buscar. Revisa tu conexión."
            else -> "No pudimos buscar."
        },
        dataStatus = "Tu información está a salvo.",
        action = ErrorAction.Retry,
    )
    ErrorFlow.SignIn -> UserFacingError(
        what = when (error.errorCause()) {
            ErrorCause.Network -> "No pudimos conectarnos. Revisa tu conexión."
            else -> "No pudimos iniciar sesión. Revisa tu correo y contraseña."
        },
        dataStatus = "No se perdió nada.",
        action = ErrorAction.Retry,
    )
    ErrorFlow.SignUp -> UserFacingError(
        what = when (error.errorCause()) {
            ErrorCause.Network -> "No pudimos conectarnos. Revisa tu conexión."
            else -> "No pudimos crear la cuenta. Revisa tus datos."
        },
        dataStatus = "No se creó ninguna cuenta.",
        action = ErrorAction.Retry,
    )
    ErrorFlow.SignOut -> UserFacingError(
        what = "No pudimos cerrar sesión.",
        dataStatus = "Tu información está guardada en el dispositivo.",
        action = ErrorAction.Retry,
    )
    ErrorFlow.ImportRead -> UserFacingError(
        what = "No se pudo abrir el archivo compartido.",
        dataStatus = "Nada se guardó.",
        action = ErrorAction.ChooseAnotherImage,
    )
    ErrorFlow.ImportBatchSave -> UserFacingError(
        what = "No pudimos guardar la importación.",
        dataStatus = "Los elementos reconocidos siguen pendientes de guardar.",
        action = ErrorAction.Retry,
    )
    ErrorFlow.ContextOpen -> UserFacingError(
        what = "No encontramos este contexto.",
        dataStatus = "Tus demás datos están bien.",
        action = ErrorAction.Retry,
    )
}

/** Error contextual cuando un comprobante ya existe y se evita duplicar. */
internal fun duplicateReceiptGuardError(): UserFacingError = UserFacingError(
    what = "Ya existe un comprobante igual. Puedes conservarlo sin crear otra copia.",
    dataStatus = "No se creó ninguna copia.",
    action = ErrorAction.Retry,
)

/** Error contextual cuando el comprobante pedido no existe. */
internal fun comprobanteNotFound(): UserFacingError = UserFacingError(
    what = "No encontramos este comprobante.",
    dataStatus = "Pudo haber sido eliminado.",
    action = ErrorAction.Retry,
)

/** Error contextual cuando la operación pedida no existe. */
internal fun operationNotFound(): UserFacingError = UserFacingError(
    what = "No encontramos esta operación.",
    dataStatus = "Pudo haber sido eliminada.",
    action = ErrorAction.Retry,
)

/** Envoltura para los errores de lectura de import que llegan por canal. */
internal fun importReadError(what: String): UserFacingError = UserFacingError(
    what = what,
    dataStatus = "Nada se guardó.",
    action = ErrorAction.ChooseAnotherImage,
)
