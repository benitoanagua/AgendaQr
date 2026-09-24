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
                "XauxaScreen como contenedor raíz.",
                "XauxaEmptyState con título y acción de recuperación.",
                "La acción abre la importación (patrón de incorporación).",
            ),
            components = listOf("xauxa-screen", "xauxa-empty-state", "xauxa-primary-button"),
        ),
        LabPattern(
            id = "fallo-puntual",
            title = "Fallo puntual con notificación",
            description = "Una operación falla sin perder el contexto: toast con reintento.",
            steps = listOf(
                "El contenido ya cargado permanece visible.",
                "XauxaToast con tono Danger y acción Reintentar.",
                "Al reintentar con éxito, toast Success de confirmación.",
            ),
            components = listOf("xauxa-toast", "xauxa-primary-button"),
        ),
        LabPattern(
            id = "fallo-contenido",
            title = "Fallo de contenido con banner embebido",
            description = "Un bloque de contenido falla dentro de su contexto.",
            steps = listOf(
                "El resto de la pantalla sigue usable.",
                "XauxaInlineResult con tono Danger, título, metadatos y acción.",
                "Para estado persistente de sincronización usar XauxaStatusBanner.",
            ),
            components = listOf("xauxa-inline-result", "xauxa-status-banner", "xauxa-text-action"),
        ),
        LabPattern(
            id = "fallo-aplicacion",
            title = "Fallo de aplicación con página completa",
            description = "Nada es usable: página de error con reintento y salida.",
            steps = listOf(
                "XauxaErrorPage con título, mensaje, Reintentar y Volver.",
                "El mensaje explica la causa sin jerga.",
            ),
            components = listOf("xauxa-error-page", "xauxa-primary-button", "xauxa-text-action"),
        ),
        LabPattern(
            id = "pantalla-completa",
            title = "Mostrar contenido a pantalla completa",
            description = "Lectura o detalle que ocupa todo el viewport (full-bleed en dashboard).",
            steps = listOf(
                "XauxaScreen como contenedor.",
                "XauxaHeroCard con el número grande y footer de estadísticas.",
                "Bloques de detalle con XauxaSection y XauxaListRow.",
            ),
            components = listOf("xauxa-screen", "xauxa-hero-card", "xauxa-section", "xauxa-list-row"),
        ),
        LabPattern(
            id = "incorporacion",
            title = "Incorporación desde múltiples fuentes",
            description = "Añadir un destino por cámara, archivo o captura manual.",
            steps = listOf(
                "XauxaScannerViewport para captura con cámara.",
                "XauxaFileUpload para importar comprobante desde archivo.",
                "XauxaTextInput para pegado o captura manual.",
                "El resultado se confirma con XauxaInlineResult.",
            ),
            components = listOf("xauxa-scanner-viewport", "xauxa-file-upload", "xauxa-text-input", "xauxa-inline-result"),
            demo = true,
            demoNote = "La selección real de cámara y archivo queda pendiente por plataforma; " +
                "el flujo está compuesto pero no conectado.",
        ),
        LabPattern(
            id = "bloqueo-opcional",
            title = "Bloqueo opcional, desactivado por defecto",
            description = "Una protección existe pero nace apagada y se explica sola.",
            steps = listOf(
                "XauxaSettingRow con checked = false inicial.",
                "La descripción explica qué protege al activarse.",
            ),
            components = listOf("xauxa-setting-row"),
        ),
        LabPattern(
            id = "confirmacion-destructiva",
            title = "Confirmación destructiva con Deshacer",
            description = "Eliminar exige confirmación y ofrece reversión honesta.",
            steps = listOf(
                "XauxaDialog con Cancelar y XauxaDangerButton Eliminar.",
                "Al confirmar, XauxaToast Success con acción Deshacer.",
                "Deshacer solo se ofrece si el modelo permite revertir; si no, el toast es solo confirmación.",
            ),
            components = listOf("xauxa-dialog", "xauxa-danger-button", "xauxa-toast"),
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
