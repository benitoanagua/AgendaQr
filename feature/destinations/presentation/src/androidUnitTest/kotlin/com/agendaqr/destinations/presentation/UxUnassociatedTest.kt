package com.agendaqr.destinations.presentation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.destinations.domain.AssociateComprobanteToOperationUseCase
import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteFileStore
import com.agendaqr.destinations.domain.ComprobanteRepository
import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.ContextRepository
import com.agendaqr.destinations.domain.DeleteComprobanteUseCase
import com.agendaqr.destinations.domain.DeleteOperationWithHistoryUseCase
import com.agendaqr.destinations.domain.DeletedOperationHistory
import com.agendaqr.destinations.domain.DeletedOperationHistoryRepository
import com.agendaqr.destinations.domain.FindDuplicateComprobantesUseCase
import com.agendaqr.destinations.domain.GetComprobanteUseCase
import com.agendaqr.destinations.domain.GetOperationUseCase
import com.agendaqr.destinations.domain.ObserveContextsUseCase
import com.agendaqr.destinations.domain.ObserveOperationComprobantesUseCase
import com.agendaqr.destinations.domain.ObserveOperationsUseCase
import com.agendaqr.destinations.domain.ObserveUnassociatedComprobantesUseCase
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.SaveComprobanteUseCase
import com.agendaqr.destinations.domain.SaveOperationUseCase
import com.agendaqr.destinations.domain.SuggestReceiptAssociationUseCase
import com.agendaqr.destinations.domain.UnassociateComprobanteUseCase
import com.agendaqr.destinations.domain.UpdateOperationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertTrue

/**
 * U-15: una sola acción primaria por pantalla en comprobantes sin
 * asociar. La fila entera abre el flujo de asociación; "Crear actividad
 * desde esto" es acción de texto.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UxUnassociatedTest {

    @get:Rule
    val compose = createComposeRule()

    private fun render(vm: OperationsViewModel) {
        compose.setContent {
            AmTheme {
                val state by vm.state.collectAsState()
                UnassociatedScreen(state = state, viewModel = vm)
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun tray_has_no_primary_buttons_and_row_opens_association() {
        val vm = viewModel()
        render(vm)
        // Ningún botón primario por fila.
        compose.onNodeWithText(AppStrings.associateToExistingOperation).assertDoesNotExist()
        // "Crear actividad desde esto" como acción de texto.
        compose.onNodeWithText(AppStrings.createOperationFromThis).assertExists()
        // La fila abre el flujo de asociación (diálogo con candidatas).
        compose.onNodeWithText(AppStrings.receiptReceivedTitle).performClick()
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.noAutomaticMatch).assertExists()
    }

    @Test
    fun association_dialog_creates_operation_as_text_action() {
        val vm = viewModel()
        render(vm)
        compose.onNodeWithText(AppStrings.receiptReceivedTitle).performClick()
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.createOperationFromThis).assertExists()
    }

    private fun viewModel(): OperationsViewModel {
        val operations = FakeOperations()
        val receipts = FakeReceipts(
            listOf(
                Comprobante(
                    id = "r-1",
                    file = "file://r-1.bin",
                    createdAt = 1_000,
                    updatedAt = 1_000,
                ),
            ),
        )
        val contexts = FakeContexts()
        val files = NoopFiles()
        val history = FakeHistory()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        return OperationsViewModel(
            observeOperations = ObserveOperationsUseCase(operations),
            observeContexts = ObserveContextsUseCase(contexts),
            observeUnassociated = ObserveUnassociatedComprobantesUseCase(receipts),
            observeOperationComprobantes = ObserveOperationComprobantesUseCase(receipts),
            getOperation = GetOperationUseCase(operations),
            saveOperation = SaveOperationUseCase(operations),
            updateOperation = UpdateOperationUseCase(operations),
            deleteOperation = DeleteOperationWithHistoryUseCase(operations, receipts, files, history),
            associate = AssociateComprobanteToOperationUseCase(operations, receipts),
            unassociate = UnassociateComprobanteUseCase(receipts),
            saveComprobante = SaveComprobanteUseCase(receipts, files),
            findDuplicates = FindDuplicateComprobantesUseCase(receipts, files),
            getComprobante = GetComprobanteUseCase(receipts),
            suggestReceiptAssociation = SuggestReceiptAssociationUseCase(receipts, operations),
            deleteComprobante = DeleteComprobanteUseCase(receipts, files),
            comprobanteFiles = files,
            scope = scope,
        )
    }

    private class FakeOperations : OperationRepository {
        private val state = MutableStateFlow<List<Operation>>(emptyList())
        override fun observe(): Flow<List<Operation>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(operation: Operation) {
            state.value = state.value + operation
        }
        override suspend fun update(operation: Operation) {
            state.value = state.value.map { if (it.id == operation.id) operation else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class FakeReceipts(initial: List<Comprobante> = emptyList()) : ComprobanteRepository {
        private val state = MutableStateFlow(initial)
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(comprobante: Comprobante) {
            state.value = state.value + comprobante
        }
        override suspend fun update(comprobante: Comprobante) {
            state.value = state.value.map { if (it.id == comprobante.id) comprobante else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class FakeContexts : ContextRepository {
        private val state = MutableStateFlow<List<Context>>(emptyList())
        override fun observe(): Flow<List<Context>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(context: Context) {
            state.value = state.value + context
        }
        override suspend fun update(context: Context) {
            state.value = state.value.map { if (it.id == context.id) context else it }
        }
        override suspend fun delete(id: String) {
            state.value = state.value.filterNot { it.id == id }
        }
    }

    private class NoopFiles : ComprobanteFileStore {
        override suspend fun save(id: String, bytes: ByteArray, extension: String) = "file://$id.$extension"
        override suspend fun read(file: String): ByteArray? = null
        override suspend fun delete(file: String) = Unit
    }

    private class FakeHistory : DeletedOperationHistoryRepository {
        private val state = MutableStateFlow<List<DeletedOperationHistory>>(emptyList())
        override fun observe(): Flow<List<DeletedOperationHistory>> = state
        override suspend fun save(history: DeletedOperationHistory) {
            state.value = state.value + history
        }
    }
}
