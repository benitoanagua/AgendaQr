package com.agendaqr.destinations.presentation

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Ronda 2 (Área B) — política del permiso de cámara (función pura).
 * Debe fallar-primero: con el código anterior NO existía distinción entre
 * denegación normal y permanente (el reintento no hacía nada).
 */
class CameraPermissionPolicyTest {

    @Test
    fun granted_opens_the_capture() {
        assertEquals(
            CameraPermissionAction.Open,
            cameraPermissionAction(granted = true, previouslyRequested = false, shouldShowRationale = false),
        )
        assertEquals(
            CameraPermissionAction.Open,
            cameraPermissionAction(granted = true, previouslyRequested = true, shouldShowRationale = true),
        )
    }

    @Test
    fun first_time_denial_can_ask_again() {
        // Primera denegación: el sistema todavía mostrará el diálogo.
        assertEquals(
            CameraPermissionAction.AskAgain,
            cameraPermissionAction(granted = false, previouslyRequested = false, shouldShowRationale = true),
        )
    }

    @Test
    fun permanent_denial_goes_to_settings() {
        // "No volver a preguntar": se pidió antes y el sistema ya no
        // ofrece justificación → el launcher no muestra NADA; la única
        // salida real es Ajustes.
        assertEquals(
            CameraPermissionAction.OpenSettings,
            cameraPermissionAction(granted = false, previouslyRequested = true, shouldShowRationale = false),
        )
        // Caso límite conservador: segunda petición con rationale aún
        // disponible puede volver a preguntar.
        assertEquals(
            CameraPermissionAction.AskAgain,
            cameraPermissionAction(granted = false, previouslyRequested = true, shouldShowRationale = true),
        )
    }
}
