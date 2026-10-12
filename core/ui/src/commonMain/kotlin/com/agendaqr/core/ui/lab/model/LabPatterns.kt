package com.agendaqr.core.ui.lab.model

/**
 * Composiciones recurrentes del catálogo, adaptadas de los patrones
 * canónicos del proyecto de referencia (`WwCanonicalPatterns`). Son datos puros:
 * describen qué contratos componer y en qué orden, sin UI propia.
 *
 * Ningún patrón simula una acción reversible si el modelo no lo permite:
 * los marcados como demostración lo declaran en [demoNote].
 */
data class LabPattern(
    val id: String,
    val title: String,
    val description: String,
    val steps: List<String>,
    /** Ids de contratos del catálogo que componen el patrón. */
    val components: List<String>,
    val demo: Boolean = false,
    val demoNote: String = "",
)

object LabPatterns {

    val all: List<LabPattern> = listOf(
        LabPattern(
            id = "primera-vez",
            title = "Primera vez / sin datos",
            description = "Pantalla vacía con una única salida clara hacia la primera acción.",
            steps = listOf(
                "XauxaEmptyState con título y acción de recuperación.",
                "La acción abre la importación (patrón de incorporación).",
            ),
            components = listOf("xauxa-empty-state", "xauxa-primary-button"),
        ),
        LabPattern(
            id = "fallo-puntual",
            title = "Fallo puntual con notificación",
            description = "Una operación falla sin perder el contexto: Snackbar con reintento.",
            steps = listOf(
                "El contenido ya cargado permanece visible.",
                "Snackbar del SnackbarHostState con tono Danger y acción Reintentar.",
                "Al reintentar con éxito, anuncio accesible de confirmación.",
            ),
            components = listOf("xauxa-status-banner", "xauxa-primary-button"),
        ),
        LabPattern(
            id = "fallo-contenido",
            title = "Fallo de contenido con banner embebido",
            description = "Un bloque de contenido falla dentro de su contexto.",
            steps = listOf(
                "El resto de la pantalla sigue usable.",
                "XauxaStatusBanner con tono Danger, mensaje y acción.",
                "El banner vive dentro del Scaffold, bajo la barra superior.",
            ),
            components = listOf("xauxa-status-banner", "xauxa-text-action"),
        ),
        LabPattern(
            id = "fallo-aplicacion",
            title = "Fallo de aplicación con estado vacío",
            description = "Nada es usable: estado vacío centrado con reintento y salida.",
            steps = listOf(
                "XauxaEmptyState con título, mensaje, Reintentar y salida.",
                "El mensaje explica la causa sin jerga.",
            ),
            components = listOf("xauxa-empty-state", "xauxa-primary-button", "xauxa-text-action"),
        ),
        LabPattern(
            id = "pantalla-completa",
            title = "Mostrar contenido a pantalla completa",
            description = "Lectura o detalle que ocupa todo el viewport.",
            steps = listOf(
                "Bloques de detalle con XauxaSection.",
                "Filas de contenido con XauxaListRow (ListItem de M3).",
            ),
            components = listOf("xauxa-section", "xauxa-list-row"),
        ),
        LabPattern(
            id = "incorporacion",
            title = "Incorporación desde múltiples fuentes",
            description = "Añadir un destino por cámara o captura manual.",
            steps = listOf(
                "XauxaScannerViewport para captura con cámara.",
                "XauxaTextInput para pegado o captura manual.",
                "El resultado se confirma con XauxaStatusBanner.",
            ),
            components = listOf("xauxa-scanner-viewport", "xauxa-text-input", "xauxa-status-banner"),
            demo = true,
            demoNote = "La selección real de cámara y archivos vive en los selectores de plataforma (S02); " +
                "el flujo está compuesto pero no conectado.",
        ),
        LabPattern(
            id = "confirmacion-destructiva",
            title = "Confirmación destructiva con anuncio",
            description = "Eliminar exige confirmación y ofrece reversión honesta.",
            steps = listOf(
                "XauxaDialog con Cancelar y XauxaDangerButton Eliminar.",
                "Al confirmar, Snackbar de confirmación con acción Deshacer.",
                "Deshacer solo se ofrece si el modelo permite revertir; si no, es solo confirmación.",
            ),
            components = listOf("xauxa-dialog", "xauxa-danger-button", "xauxa-status-banner"),
            demo = true,
            demoNote = "El laboratorio no persiste: Deshacer es demostración del patrón, no reversión real.",
        ),
    )

    fun find(id: String): LabPattern? = all.firstOrNull { it.id == id }

    /** Ids de contratos inexistentes referenciados por patrones. */
    fun validate(catalog: List<LabComponentContract> = LabComponentCatalog.all): List<String> {
        val ids = catalog.map { it.id }.toSet()
        return all.flatMap { pattern ->
            pattern.components.filter { it !in ids }.map { "${pattern.id}: contrato inexistente '$it'" }
        }
    }
}
