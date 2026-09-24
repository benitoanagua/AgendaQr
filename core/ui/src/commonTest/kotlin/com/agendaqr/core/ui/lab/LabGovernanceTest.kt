package com.agendaqr.core.ui.lab

import com.agendaqr.core.ui.lab.model.LabAccessibilityGate
import com.agendaqr.core.ui.lab.model.LabApiAudit
import com.agendaqr.core.ui.lab.model.LabApiFreeze
import com.agendaqr.core.ui.lab.model.LabCategory
import com.agendaqr.core.ui.lab.model.LabComponentCatalog
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Governance tests adapted from the reference project (WwAccessibilityGate,
 * WwApiAudit, WwApiFreeze): the catalog cannot document contracts the APIs
 * and inventory changes must be deliberate.
 */
class LabGovernanceTest {

    @Test
    fun api_freeze_matches_the_declared_inventory() {
        assertTrue(LabApiFreeze.validate().isEmpty(), LabApiFreeze.validate().joinToString("\n"))
    }

    @Test
    fun api_audit_has_no_errors_on_the_real_catalog() {
        val errors = LabApiAudit.auditAll().filter {
            it.severity == com.agendaqr.core.ui.lab.model.LabApiAuditSeverity.Error
        }
        assertTrue(errors.isEmpty(), errors.joinToString("\n"))
    }

    @Test
    fun accessibility_gate_passes_for_every_catalog_entry() {
        val failures = LabComponentCatalog.all.flatMap { contract ->
            LabAccessibilityGate.evaluate(contract).failures.map { "${contract.id}: $it" }
        }
        assertTrue(failures.isEmpty(), failures.joinToString("\n"))
    }

    @Test
    fun actions_with_events_declare_the_48dp_token() {
        LabComponentCatalog.all
            .filter { it.category == LabCategory.ACTIONS && it.events.isNotEmpty() }
            .forEach { contract ->
                assertTrue(
                    contract.tokens.any { token -> token.token == "XauxaMetrics.ControlMinSize" },
                    "${contract.id} must reference the 48dp touch-target token",
                )
            }
    }

    @Test
    fun every_contract_documents_usage_mapping_and_platforms() {
        LabComponentCatalog.components.forEach { contract ->
            assertTrue(contract.whenToUse.isNotBlank(), "${contract.id}: whenToUse")
            assertTrue(contract.androidMapping.isNotBlank(), "${contract.id}: androidMapping")
            assertTrue(contract.iosMapping.isNotBlank(), "${contract.id}: iosMapping")
            assertTrue(contract.platforms.isNotEmpty(), "${contract.id}: platforms")
        }
    }
}
