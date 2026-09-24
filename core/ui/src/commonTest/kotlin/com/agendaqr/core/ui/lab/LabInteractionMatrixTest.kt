package com.agendaqr.core.ui.lab

import com.agendaqr.core.ui.lab.model.LabComponentCatalog
import com.agendaqr.core.ui.lab.model.LabInteractionLens
import com.agendaqr.core.ui.lab.model.LabInteractionMatrix
import com.agendaqr.core.ui.lab.model.LabLensVerdict
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Interaction matrix honesty tests: a lens may only be REAL when the current
 * API offers the state, and documented-but-missing states must never upgrade
 * a lens.
 */
class LabInteractionMatrixTest {

    private fun contract(id: String) = requireNotNull(LabComponentCatalog.find(id))

    @Test
    fun normal_lens_is_real_for_every_contract() {
        LabComponentCatalog.all.forEach { contract ->
            assertEquals(LabLensVerdict.REAL, LabInteractionMatrix.verdict(contract, LabInteractionLens.NORMAL))
        }
    }

    @Test
    fun disabled_lens_is_real_only_when_the_api_offers_it() {
        // PrimaryButton exposes enabled = false.
        assertEquals(
            LabLensVerdict.REAL,
            LabInteractionMatrix.verdict(contract("xauxa-primary-button"), LabInteractionLens.DISABLED),
        )
        assertEquals(
            "Deshabilitado",
            LabInteractionMatrix.exactStateFor(contract("xauxa-primary-button"), LabInteractionLens.DISABLED)?.name,
        )
        // TextAction has no enabled parameter: the declared Disabled state is
        // NOT_SUPPORTED and the lens must stay honestly not applicable.
        assertEquals(
            LabLensVerdict.NOT_APPLICABLE,
            LabInteractionMatrix.verdict(contract("xauxa-text-action"), LabInteractionLens.DISABLED),
        )
    }

    @Test
    fun loading_lens_is_real_when_the_api_offers_it() {
        assertEquals(
            LabLensVerdict.REAL,
            LabInteractionMatrix.verdict(contract("xauxa-loading"), LabInteractionLens.LOADING),
        )
        // Buttons expose isLoading: the declared Carga state is offered by
        // the API and the lens is honestly real.
        assertEquals(
            LabLensVerdict.REAL,
            LabInteractionMatrix.verdict(contract("xauxa-primary-button"), LabInteractionLens.LOADING),
        )
        assertEquals(
            "Carga",
            LabInteractionMatrix.exactStateFor(contract("xauxa-primary-button"), LabInteractionLens.LOADING)?.name,
        )
        // TextAction offers no loading state: the lens stays not applicable.
        assertEquals(
            LabLensVerdict.NOT_APPLICABLE,
            LabInteractionMatrix.verdict(contract("xauxa-text-action"), LabInteractionLens.LOADING),
        )
    }

    @Test
    fun focus_and_pressed_are_applicable_only_for_interactive_components() {
        val interactive = contract("xauxa-primary-button")
        assertEquals(LabLensVerdict.SIMULATED, LabInteractionMatrix.verdict(interactive, LabInteractionLens.FOCUS))
        assertEquals(LabLensVerdict.SIMULATED, LabInteractionMatrix.verdict(interactive, LabInteractionLens.PRESSED))

        val passive = contract("xauxa-screen")
        assertEquals(
            LabLensVerdict.NOT_APPLICABLE,
            LabInteractionMatrix.verdict(passive, LabInteractionLens.FOCUS),
        )
        assertEquals(
            LabLensVerdict.NOT_APPLICABLE,
            LabInteractionMatrix.verdict(passive, LabInteractionLens.PRESSED),
        )
    }

    @Test
    fun tile_without_onclick_still_declares_interactivity_through_its_event() {
        val tile = contract("xauxa-tile")
        assertTrue(LabInteractionMatrix.applicableLenses(tile).contains(LabInteractionLens.PRESSED))
        assertTrue(LabInteractionMatrix.applicableLenses(tile).contains(LabInteractionLens.FOCUS))
    }

    @Test
    fun not_supported_states_never_resolve_to_a_real_state() {
        // Every exact state resolved by the matrix must be offered by the API.
        LabComponentCatalog.all.forEach { contract ->
            LabInteractionLens.entries.forEach { lens ->
                LabInteractionMatrix.exactStateFor(contract, lens)?.let { state ->
                    assertTrue(
                        state.status != com.agendaqr.core.ui.lab.model.LabReviewStatus.NOT_SUPPORTED,
                        "${contract.id}: lens ${lens.label} resolved to NOT_SUPPORTED state ${state.name}",
                    )
                }
            }
        }
    }

    @Test
    fun foundations_have_no_transient_lenses() {
        val color = contract("xauxa-color")
        assertEquals(
            LabLensVerdict.NOT_APPLICABLE,
            LabInteractionMatrix.verdict(color, LabInteractionLens.PRESSED),
        )
        assertEquals(
            LabLensVerdict.NOT_APPLICABLE,
            LabInteractionMatrix.verdict(color, LabInteractionLens.DISABLED),
        )
    }
}
