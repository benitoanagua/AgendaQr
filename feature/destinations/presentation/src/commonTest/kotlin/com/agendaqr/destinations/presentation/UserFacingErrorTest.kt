package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.AuthRepository
import com.agendaqr.destinations.domain.AuthState
import com.agendaqr.destinations.domain.AuthUser
import com.agendaqr.destinations.domain.Comprobante
import com.agendaqr.destinations.domain.ComprobanteRepository
import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.ContextRepository
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.DestinationRepository
import com.agendaqr.destinations.domain.DeleteDestinationUseCase
import com.agendaqr.destinations.domain.GetDestinationUseCase
import com.agendaqr.destinations.domain.ObserveDestinationsUseCase
import com.agendaqr.destinations.domain.Operation
import com.agendaqr.destinations.domain.OperationRepository
import com.agendaqr.destinations.domain.OperationType
import com.agendaqr.destinations.domain.SaveDestinationUseCase
import com.agendaqr.destinations.domain.SaveOperationUseCase
import com.agendaqr.destinations.domain.SignInUseCase
import com.agendaqr.destinations.domain.SignOutUseCase
import com.agendaqr.destinations.domain.SignUpResult
import com.agendaqr.destinations.domain.SignUpUseCase
import com.agendaqr.destinations.domain.ObserveAuthStateUseCase
import com.agendaqr.destinations.domain.UpdateDestinationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * T5 — Errores recuperables y contextuales (spec §10).
 *
 * Todo error responde: qué ocurrió, qué pasó con los datos y qué puede
 * hacer el usuario. Nunca se muestra `error.message` crudo (inglés,
 * técnico, o directamente `"Operation failed"`).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserFacingErrorTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ------------------------------------------------------------------
    // Clasificación de causas (portable, por jerarquía de nombres).
    // ------------------------------------------------------------------

    @Test
    fun network_exceptions_are_classified_as_network() {
        assertEquals(ErrorCause.Network, ConnectTimeoutLike().errorCause())
        assertEquals(ErrorCause.Network, IOExceptionLike().errorCause())
    }

    @Test
    fun uri_permission_exceptions_are_classified() {
        assertEquals(ErrorCause.UriPermission, SecurityExceptionLike().errorCause())
        assertEquals(ErrorCause.UriPermission, FileNotFoundLike().errorCause())
    }

    @Test
    fun wrapped_causes_are_classified_through_the_chain() {
        val wrapped = RuntimeException("wrapper", SecurityExceptionLike())
        assertEquals(ErrorCause.UriPermission, wrapped.errorCause())
    }

    @Test
    fun unknown_exceptions_fall_back_to_unknown() {
        assertEquals(ErrorCause.Unknown, GenericAppError().errorCause())
    }

    // ------------------------------------------------------------------
    // Mapeo: excepción → mensaje en español, sin texto técnico, con acción.
    // ------------------------------------------------------------------

    @Test
    fun import_read_error_never_leaks_the_technical_message() {
        val error = userFacingError(SecurityExceptionLike("Permission Denial: uid=10190"), ErrorFlow.ImportRead)
        val shown = error.display()
        assertTrue(shown.contains("No se pudo abrir el archivo compartido."))
        assertFalse(shown.contains("Permission"), "El texto técnico no debe mostrarse: $shown")
        assertFalse(shown.contains("uid="))
        assertEquals("ELEGIR OTRA IMAGEN", error.action.label)
        assertTrue(shown.contains("Nada se guardó."), "Debe decir qué pasó con los datos")
    }

    @Test
    fun sign_in_network_error_tells_the_user_to_check_connection() {
        val error = userFacingError(ConnectTimeoutLike(), ErrorFlow.SignIn)
        assertTrue(error.display().contains("Revisa tu conexión"))
        assertEquals("REINTENTAR", error.action.label)
    }

    @Test
    fun sign_in_auth_error_is_never_the_raw_library_message() {
        val error = userFacingError(RestExceptionLike("Invalid login credentials"), ErrorFlow.SignIn)
        val shown = error.display()
        assertTrue(shown.contains("No pudimos iniciar sesión"))
        assertFalse(shown.contains("Invalid login credentials"))
        assertFalse(shown.contains("RestException"))
        assertEquals("REINTENTAR", error.action.label)
    }

    @Test
    fun save_error_says_what_happened_to_the_data() {
        val error = userFacingError(GenericAppError("disk on fire"), ErrorFlow.SaveOperation)
        val shown = error.display()
        assertTrue(shown.contains("No pudimos guardar la operación."))
        assertTrue(shown.contains("Tus cambios siguen en la pantalla."))
        assertFalse(shown.contains("disk on fire"))
        assertEquals("REINTENTAR", error.action.label)
    }

    @Test
    fun every_flow_error_offers_an_action() {
        // "un test por flujo que verifique que el error incluye acción":
        // el mapeo central es exhaustivo sobre los flujos.
        val boom = GenericAppError("boom")
        for (flow in ErrorFlow.entries) {
            val error = userFacingError(boom, flow)
            assertTrue(error.action.label.isNotBlank(), "El flujo $flow no ofrece acción")
            assertTrue(error.what.isNotBlank(), "El flujo $flow no dice qué ocurrió")
            assertTrue(error.dataStatus.isNotBlank(), "El flujo $flow no dice qué pasó con los datos")
        }
    }

    // ------------------------------------------------------------------
    // Flujo por flujo: el estado visible expone el error con acción.
    // ------------------------------------------------------------------

    @Test
    fun destinations_save_failure_shows_actionable_error() = runBlocking {
        val repository = FailingDestinationsRepository()
        val vm = DestinationsViewModel(
            observe = ObserveDestinationsUseCase(repository),
            get = GetDestinationUseCase(repository),
            save = SaveDestinationUseCase(repository),
            update = UpdateDestinationUseCase(repository),
            delete = DeleteDestinationUseCase(repository),
            toggleFavorite = com.agendaqr.destinations.domain.ToggleFavoriteUseCase(repository),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
        vm.onAction(
            DestinationAction.Save(
                Destination(
                    id = "d-1",
                    name = "Carniceria",
                    qr = com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="),
                    createdAt = 1,
                    updatedAt = 1,
                ),
            ),
        )
        waitUntil { vm.state.value.error != null }
        val error = vm.state.value.error
        assertNotNull(error)
        // Regresión del literal inglés "Operation failed".
        assertFalse(error.display().contains("Operation failed"))
        assertFalse(error.display().contains("sql"))
        assertTrue(error.display().contains("No pudimos guardar el QR."))
        assertEquals("REINTENTAR", error.action.label)
    }

    @Test
    fun operations_save_failure_shows_actionable_error() = runBlocking {
        val operations = FailingOperationsRepository()
        val vm = OperationsViewModel(
            observeOperations = com.agendaqr.destinations.domain.ObserveOperationsUseCase(operations),
            observeContexts = com.agendaqr.destinations.domain.ObserveContextsUseCase(EmptyContexts()),
            observeUnassociated = com.agendaqr.destinations.domain.ObserveUnassociatedComprobantesUseCase(EmptyReceipts()),
            observeOperationComprobantes = com.agendaqr.destinations.domain.ObserveOperationComprobantesUseCase(EmptyReceipts()),
            getOperation = com.agendaqr.destinations.domain.GetOperationUseCase(operations),
            saveOperation = SaveOperationUseCase(operations),
            updateOperation = com.agendaqr.destinations.domain.UpdateOperationUseCase(operations),
            deleteOperation = com.agendaqr.destinations.domain.DeleteOperationWithHistoryUseCase(
                operations, EmptyReceipts(), MemoryFiles(), EmptyHistory(),
            ),
            associate = com.agendaqr.destinations.domain.AssociateComprobanteToOperationUseCase(operations, EmptyReceipts()),
            unassociate = com.agendaqr.destinations.domain.UnassociateComprobanteUseCase(EmptyReceipts()),
            saveComprobante = com.agendaqr.destinations.domain.SaveComprobanteUseCase(EmptyReceipts(), MemoryFiles()),
            findDuplicates = com.agendaqr.destinations.domain.FindDuplicateComprobantesUseCase(EmptyReceipts(), MemoryFiles()),
            getComprobante = com.agendaqr.destinations.domain.GetComprobanteUseCase(EmptyReceipts()),
            suggestReceiptAssociation = com.agendaqr.destinations.domain.SuggestReceiptAssociationUseCase(EmptyReceipts(), operations),
            deleteComprobante = com.agendaqr.destinations.domain.DeleteComprobanteUseCase(EmptyReceipts(), MemoryFiles()),
            comprobanteFiles = MemoryFiles(),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined),
        )
        vm.onAction(
            OperationAction.SaveNew(
                type = OperationType.PAGO,
                occurredAt = 1,
                amount = "10",
                currency = null,
                personOrEntity = null,
                destinationId = null,
                concept = null,
                note = null,
                contextId = null,
            ),
        )
        waitUntil { vm.state.value.error != null }
        val error = vm.state.value.error
        assertNotNull(error)
        assertTrue(error.display().contains("No pudimos guardar la operación."))
        assertEquals("REINTENTAR", error.action.label)
    }

    @Test
    fun auth_sign_in_failure_shows_actionable_error_with_retry_target() = runTest(dispatcher.scheduler) {
        val repository = object : AuthRepository {
            override val state: Flow<AuthState> = MutableStateFlow(AuthState.SignedOut)
            override suspend fun signIn(email: String, password: String): AuthUser =
                throw RestExceptionLike("Invalid login credentials")
            override suspend fun signUp(email: String, password: String): SignUpResult =
                throw RestExceptionLike()
            override suspend fun signOut() {}
        }
        val viewModel = AuthViewModel(
            observe = ObserveAuthStateUseCase(repository),
            signIn = SignInUseCase(repository),
            signUp = SignUpUseCase(repository),
            signOutUseCase = SignOutUseCase(repository),
        )
        viewModel.submitSignIn()
        advanceUntilIdle()

        val error = viewModel.state.value.errorMessage
        assertNotNull(error)
        assertFalse(error.display().contains("Invalid login credentials"))
        assertTrue(error.display().contains("No pudimos iniciar sesión"))
        assertEquals("REINTENTAR", error.action.label)
        assertEquals(ErrorFlow.SignIn, viewModel.state.value.errorFlow)
    }

    private suspend fun waitUntil(condition: suspend () -> Boolean) {
        repeat(100) {
            if (condition()) return
            delay(20)
        }
    }

    // ------------------------------------------------------------------
    // Excepciones con nombre representativo (sin dependencias de red).
    // ------------------------------------------------------------------

    private class ConnectTimeoutLike : RuntimeException("ConnectTimeout")
    private class IOExceptionLike : RuntimeException("network IOException while reading")
    private class SecurityExceptionLike(message: String = "denied") : RuntimeException(message)
    private class FileNotFoundLike : RuntimeException("FileNotFoundException: /tmp/x.png")
    private class RestExceptionLike(message: String = "rest error") : RuntimeException(message)
    private class GenericAppError(message: String = "kaboom") : RuntimeException(message)

    private class FailingDestinationsRepository : DestinationRepository {
        private val state = MutableStateFlow<List<Destination>>(emptyList())
        override fun observe(): Flow<List<Destination>> = state
        override suspend fun get(id: String): Destination? = null
        override suspend fun save(destination: Destination) { throw RuntimeException("sqlite_io_error") }
        override suspend fun update(destination: Destination) {}
        override suspend fun delete(id: String) {}
    }

    private class FailingOperationsRepository : OperationRepository {
        private val state = MutableStateFlow<List<Operation>>(emptyList())
        override fun observe(): Flow<List<Operation>> = state
        override suspend fun get(id: String) = state.value.firstOrNull { it.id == id }
        override suspend fun save(operation: Operation) { throw RuntimeException("sqlite_io_error") }
        override suspend fun update(operation: Operation) {}
        override suspend fun delete(id: String) {}
    }

    private class EmptyContexts : ContextRepository {
        private val state = MutableStateFlow<List<Context>>(emptyList())
        override fun observe(): Flow<List<Context>> = state
        override suspend fun get(id: String) = null
        override suspend fun save(context: Context) {}
        override suspend fun update(context: Context) {}
        override suspend fun delete(id: String) {}
    }

    private class EmptyReceipts : ComprobanteRepository {
        private val state = MutableStateFlow<List<Comprobante>>(emptyList())
        override fun observe(): Flow<List<Comprobante>> = state
        override suspend fun get(id: String) = null
        override suspend fun save(comprobante: Comprobante) {}
        override suspend fun update(comprobante: Comprobante) {}
        override suspend fun delete(id: String) {}
    }

    private class MemoryFiles : com.agendaqr.destinations.domain.ComprobanteFileStore {
        override suspend fun save(id: String, bytes: ByteArray, extension: String): String = "file://$id.$extension"
        override suspend fun read(file: String): ByteArray? = null
        override suspend fun delete(file: String) {}
    }

    private class EmptyHistory : com.agendaqr.destinations.domain.DeletedOperationHistoryRepository {
        private val state = MutableStateFlow<List<com.agendaqr.destinations.domain.DeletedOperationHistory>>(emptyList())
        override fun observe(): Flow<List<com.agendaqr.destinations.domain.DeletedOperationHistory>> = state
        override suspend fun save(history: com.agendaqr.destinations.domain.DeletedOperationHistory) {}
    }
}
