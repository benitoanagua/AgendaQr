package com.agendaqr.destinations.presentation

/**
 * Ronda 2 (Área B) — política del permiso de cámara como función PURA
 * (testeable sin plataforma). La detección de "denegado permanentemente"
 * es la heurística estándar de Android: se pidió antes y el sistema ya
 * no ofrece justificación (`shouldShowRequestPermissionRationale` =
 * false tras una denegación previa) → el launcher ya no mostrará el
 * diálogo; la única salida real es Ajustes.
 */
sealed interface CameraPermissionAction {
    /** Abre la captura: permiso concedido. */
    data object Open : CameraPermissionAction

    /** Vuelve a pedir el permiso: aún se puede preguntar. */
    data object AskAgain : CameraPermissionAction

    /** "No volver a preguntar": la única recuperación es Ajustes. */
    data object OpenSettings : CameraPermissionAction
}

fun cameraPermissionAction(
    granted: Boolean,
    previouslyRequested: Boolean,
    shouldShowRationale: Boolean,
): CameraPermissionAction = when {
    granted -> CameraPermissionAction.Open
    previouslyRequested && !shouldShowRationale -> CameraPermissionAction.OpenSettings
    else -> CameraPermissionAction.AskAgain
}
