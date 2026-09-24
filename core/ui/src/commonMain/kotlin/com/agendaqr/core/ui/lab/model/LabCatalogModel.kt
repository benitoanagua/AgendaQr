package com.agendaqr.core.ui.lab.model

/**
 * Pure catalog model for the AgendaQr component lab.
 *
 * This layer has no Compose dependency on purpose: the inventory, its search
 * filters and its integrity rules can be unit-tested without a UI runtime.
 * The lab UI derives everything it shows from these declarations, so there is
 * a single source of descriptive metadata per component.
 */

/**
 * Honest review classification used across the lab.
 *
 * - [VERIFIED]: comprobado en código o pruebas.
 * - [DOCUMENTED]: especificado en Xauxa o en documentación del proyecto,
 *   pero sin implementar todavía.
 * - [PENDING]: requiere revisión visual, definición de diseño o implementación.
 * - [NOT_SUPPORTED]: no existe en la API actual.
 */
enum class LabReviewStatus(val label: String, val description: String) {
    VERIFIED("Verificado", "Comprobado en código o pruebas."),
    DOCUMENTED("Documentado", "Especificado en Xauxa o en la documentación, sin implementar todavía."),
    PENDING("Pendiente", "Requiere revisión visual, definición de diseño o implementación."),
    NOT_SUPPORTED("No soportado", "No existe en la API actual."),
}

/**
 * Dark-theme support is tracked as its own axis because "no aplica" is a
 * legitimate honest answer for non-chromatic tokens.
 */
enum class LabDarkThemeSupport(val label: String) {
    VERIFIED("Tema oscuro verificado"),
    DOCUMENTED("Tema oscuro solo documentado"),
    PENDING("Tema oscuro pendiente"),
    NOT_SUPPORTED("Tema oscuro no soportado"),
    NOT_APPLICABLE("Tema oscuro no aplica"),
}

/**
 * Catalog categories derived from the responsibilities of the components that
 * really exist in core:ui. No category is registered for components that are
 * only mentioned in documentation.
 */
enum class LabCategory(val label: String) {
    FOUNDATIONS("Fundamentos"),
    SURFACES("Superficies"),
    ACTIONS("Acciones"),
    FEEDBACK("Feedback"),
    DATA("Datos"),
    QR("QR y comprobantes"),
}

/** Public API parameter of a component. */
data class LabProp(
    val name: String,
    val type: String,
    val default: String? = null,
)

/**
 * A declared state of a component. [status] records whether the state is
 * actually offered by the current implementation, only documented, pending
 * review, or explicitly absent from the API.
 */
data class LabState(
    val name: String,
    val description: String,
    val status: LabReviewStatus,
)

/** Callback exposed by a component. */
data class LabEvent(
    val name: String,
    val description: String,
)

/** Reference to a real Xauxa token consumed by a component. */
data class LabTokenRef(
    val token: String,
    val role: String,
)

/** Estado de implementación real por plataforma. */
data class LabPlatformStatus(
    val platform: String,
    val implemented: Boolean,
    val note: String,
)

/**
 * Contract of a catalog entry: the descriptive source of truth for the
 * inspector. It describes the public contract; it does not replace the
 * production composable API.
 */
data class LabComponentContract(
    val id: String,
    val name: String,
    val category: LabCategory,
    val purpose: String,
    val description: String,
    val props: List<LabProp> = emptyList(),
    val states: List<LabState> = emptyList(),
    val events: List<LabEvent> = emptyList(),
    val tokens: List<LabTokenRef> = emptyList(),
    val usage: List<String> = emptyList(),
    val notes: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val darkThemeSupport: LabDarkThemeSupport,
    val darkThemeNote: String,
    /** Cuándo usarlo y cuándo preferir otro componente. */
    val whenToUse: String = "",
    /** Mapeo al componente nativo en Compose/Android. */
    val androidMapping: String = "",
    /** Mapeo al componente nativo en SwiftUI/iOS. */
    val iosMapping: String = "",
    /** Estado de implementación real por plataforma. */
    val platforms: List<LabPlatformStatus> = emptyList(),
) {
    val isInteractive: Boolean get() = events.isNotEmpty()
}
