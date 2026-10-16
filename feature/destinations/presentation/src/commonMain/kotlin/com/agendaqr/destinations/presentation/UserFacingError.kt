package com.agendaqr.destinations.presentation

import com.agendaqr.core.ui.components.AmBannerAction

/**
 * Error listo para mostrar  — spec congelada §10:
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
        if (dataStatus.isBlank()) what else what + "\n" + dataStatus
}

/**
 * Acciones de banner derivadas del error, para usar con [AmStatusBanner].
 *
 * - [ErrorAction.Retry]: dos acciones: reintentar ([onRetry]) y descartar
 *   ([onClear]) con [AppStrings.Descartar].
 * - Cualquier otra acción: una sola acción (la etiqueta de [ErrorAction],
 *   [onClear]) y sin descarte, para no duplicar comportamiento.
 */
fun UserFacingError.bannerActions(
    onRetry: () -> Unit,
    onClear: () -> Unit,
): Pair<AmBannerAction?, AmBannerAction?> = when (action) {
    ErrorAction.Retry -> AmBannerAction(action.label, onRetry) to
        AmBannerAction(AppStrings.Descartar, onClear)
    else -> AmBannerAction(action.label, onClear) to null
}

/** Acciones que un error recuperable puede ofrecer. */
enum class ErrorAction(val label: String) {
    /** Reintentar la operación fallida (o despejar el camino para hacerlo). */
    Retry(AppStrings.ErrorReintentar),

    /** Volver a elegir el archivo/imagen de origen. */
    ChooseAnotherImage(AppStrings.ErrorElegirOtraImagen),

    /**
     * Cerrar: para errores donde reintentar no tiene sentido (el elemento
     * ya no existe). El banner se descarta; el usuario sigue en su flujo.
     */
    Close(AppStrings.ErrorCerrar),
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
    ContextSave,
    CameraPermission,
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
        what = AppStrings.ErrorSaveQrWhat,
        dataStatus = AppStrings.ErrorSaveQrData,
        action = ErrorAction.Retry,
    )
    ErrorFlow.DeleteQr -> UserFacingError(
        what = AppStrings.ErrorDeleteQrWhat,
        dataStatus = AppStrings.ErrorDeleteQrData,
        action = ErrorAction.Retry,
    )
    ErrorFlow.SaveOperation -> UserFacingError(
        what = AppStrings.ErrorSaveOperationWhat,
        dataStatus = AppStrings.ErrorSaveQrData,
        action = ErrorAction.Retry,
    )
    ErrorFlow.DeleteOperation -> UserFacingError(
        what = AppStrings.ErrorDeleteOperationWhat,
        dataStatus = AppStrings.ErrorDeleteOperationData,
        action = ErrorAction.Retry,
    )
    ErrorFlow.SaveReceipt -> UserFacingError(
        what = AppStrings.ErrorSaveReceiptWhat,
        dataStatus = AppStrings.ErrorSaveReceiptData,
        action = ErrorAction.Retry,
    )
    ErrorFlow.DeleteReceipt -> UserFacingError(
        what = AppStrings.ErrorDeleteReceiptWhat,
        dataStatus = AppStrings.ErrorDeleteReceiptData,
        action = ErrorAction.Retry,
    )
    ErrorFlow.ComprobanteOpen -> UserFacingError(
        what = AppStrings.ErrorComprobanteOpenWhat,
        dataStatus = AppStrings.ErrorDeleteReceiptData,
        action = ErrorAction.Retry,
    )
    ErrorFlow.Search -> UserFacingError(
        what = when (error.errorCause()) {
            ErrorCause.Network -> AppStrings.ErrorSearchNetworkWhat
            else -> AppStrings.ErrorSearchWhat
        },
        dataStatus = AppStrings.ErrorSearchData,
        action = ErrorAction.Retry,
    )
    ErrorFlow.SignIn -> UserFacingError(
        what = when (error.errorCause()) {
            ErrorCause.Network -> AppStrings.ErrorSignInNetworkWhat
            else -> AppStrings.ErrorSignInWhat
        },
        dataStatus = AppStrings.ErrorSignInData,
        action = when (error.errorCause()) {
            ErrorCause.Network -> ErrorAction.Retry
            else -> ErrorAction.Close
        },
    )
    ErrorFlow.SignUp -> UserFacingError(
        what = when (error.errorCause()) {
            ErrorCause.Network -> AppStrings.ErrorSignUpNetworkWhat
            else -> AppStrings.ErrorSignUpWhat
        },
        dataStatus = AppStrings.ErrorSignUpData,
        action = when (error.errorCause()) {
            ErrorCause.Network -> ErrorAction.Retry
            else -> ErrorAction.Close
        },
    )
    ErrorFlow.SignOut -> UserFacingError(
        what = AppStrings.ErrorSignOutWhat,
        dataStatus = AppStrings.ErrorSignOutData,
        action = ErrorAction.Retry,
    )
    ErrorFlow.ImportRead -> UserFacingError(
        what = AppStrings.ErrorImportReadWhat,
        dataStatus = AppStrings.ErrorImportReadData,
        action = ErrorAction.ChooseAnotherImage,
    )
    ErrorFlow.ImportBatchSave -> UserFacingError(
        what = AppStrings.ErrorImportBatchSaveWhat,
        dataStatus = AppStrings.ErrorImportBatchSaveData,
        action = ErrorAction.Retry,
    )
    ErrorFlow.ContextSave -> UserFacingError(
        what = AppStrings.ErrorContextSaveWhat,
        dataStatus = AppStrings.ErrorContextSaveData,
        action = ErrorAction.Retry,
    )
    // Reintentar no tiene sentido: el contexto no existe. Cerrar.
    ErrorFlow.ContextOpen -> UserFacingError(
        what = AppStrings.ErrorContextOpenWhat,
        dataStatus = AppStrings.ErrorContextOpenData,
        action = ErrorAction.Close,
    )
    ErrorFlow.CameraPermission -> UserFacingError(
        what = AppStrings.ErrorCameraPermissionWhat,
        dataStatus = AppStrings.ErrorCameraPermissionData,
        action = ErrorAction.Retry,
    )
}

/** Error contextual cuando un comprobante ya existe y se evita duplicar. */
internal fun duplicateReceiptGuardError(): UserFacingError = UserFacingError(
    what = AppStrings.ErrorDuplicateReceiptWhat,
    dataStatus = AppStrings.ErrorDuplicateReceiptData,
    action = ErrorAction.Close,
)

/** Error contextual cuando el comprobante pedido no existe. */
internal fun comprobanteNotFound(): UserFacingError = UserFacingError(
    what = AppStrings.ErrorComprobanteNotFoundWhat,
    dataStatus = AppStrings.ErrorComprobanteNotFoundData,
    action = ErrorAction.Close,
)

/** Error contextual cuando la operación pedida no existe. */
internal fun operationNotFound(): UserFacingError = UserFacingError(
    what = AppStrings.ErrorOperationNotFoundWhat,
    dataStatus = AppStrings.ErrorOperationNotFoundData,
    action = ErrorAction.Close,
)

/** Envoltura para los errores de lectura de import que llegan por canal. */
internal fun importReadError(what: String): UserFacingError = UserFacingError(
    what = what,
    dataStatus = AppStrings.ErrorImportReadData,
    action = ErrorAction.ChooseAnotherImage,
)
