package com.agendaqr.core.ui.lab.model

/**
 * Auditoría de API de contratos, adaptada del proyecto de referencia
 * (`WwApiAudit`).
 * Detecta olores de contrato antes de que una variante se convierta en un
 * componente nuevo: props duplicadas, sobrecarga de estados y knobs de
 * color crudo que deberían ser tonos semánticos.
 */
data class LabApiAuditIssue(
    val componentId: String,
    val code: String,
    val message: String,
    val severity: LabApiAuditSeverity,
)

enum class LabApiAuditSeverity { Warning, Error }

object LabApiAudit {

    fun audit(contract: LabComponentContract): List<LabApiAuditIssue> = buildList {
        val propNames = contract.props.map { it.name.lowercase() }
        if (propNames.size != propNames.toSet().size) {
            add(LabApiAuditIssue(contract.id, "DUPLICATE_PROP", "El contrato declara props duplicadas.", LabApiAuditSeverity.Error))
        }
        if (contract.states.size > 8) {
            add(LabApiAuditIssue(contract.id, "STATE_OVERLOAD", "Preferir variantes/props antes de más de ocho estados.", LabApiAuditSeverity.Warning))
        }
        val rawColorKnob = contract.props.any {
            (it.name.equals("color", ignoreCase = true) || it.name.equals("containerColor", ignoreCase = true)) &&
                (it.type == "Color" || it.type == "Color?")
        }
        if (rawColorKnob) {
            add(LabApiAuditIssue(contract.id, "COLOR_DUPLICATION", "Preferir tono semántico antes de exponer color crudo.", LabApiAuditSeverity.Warning))
        }
    }

    fun auditAll(catalog: List<LabComponentContract> = LabComponentCatalog.all): List<LabApiAuditIssue> =
        catalog.flatMap(::audit)
}
