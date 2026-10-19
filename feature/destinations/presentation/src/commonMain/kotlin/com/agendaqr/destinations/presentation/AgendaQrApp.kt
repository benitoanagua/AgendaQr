package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agendaqr.core.ui.components.AmBannerAction
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmLoading
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.destinations.data.DemoSeeder
import com.agendaqr.destinations.data.LegacyComprobanteBytesMigration
import com.agendaqr.destinations.data.LegacyLocalDataMigration
import com.agendaqr.destinations.data.LocalProfileScope
import com.agendaqr.destinations.data.createLocalProfileRepository
import com.agendaqr.destinations.data.createLocalProtectionRepository
import com.agendaqr.destinations.data.legacyLocalProfileIdOrNull
import com.agendaqr.destinations.data.platformBiometricGate
import com.agendaqr.destinations.domain.BiometricAvailability
import com.agendaqr.destinations.domain.DEMO_PROFILE_ID
import kotlinx.coroutines.launch

/**
 * Raíz de la app — ARRANQUE LOCAL-FIRST.
 *
 * La decisión de entrada NO depende de una sesión remota. El flujo es:
 *
 * 1. Inicializar el almacenamiento local y crear/recuperar el PERFIL LOCAL
 *    (id estable, sin cuenta, sin red).
 * 2. Migrar datos de instalaciones previas al namespace del perfil local.
 * 3. Comprobar la protección local:
 *    - sin PIN configurado → [LocalOnboardingScreen];
 *    - con PIN y bloqueada → [LocalLockScreen];
 *    - desbloqueada → la app local completa ([AgendaQrLocalApp]).
 *
 * La cuenta remota sigue siendo opcional y se gestiona desde
 * [AccountScreen]; su ausencia no bloquea el acceso.
 *
 * Modos transversales:
 * - Biometría: atajo opcional de desbloqueo (puerta de plataforma). El PIN
 *   es siempre la vía principal y el fallback ante cualquier fallo.
 * - Demostración: perfil aislado [DEMO_PROFILE_ID] con datos ficticios.
 *   No toca el perfil real, no requiere PIN ni cuenta y nunca sincroniza.
 */
@Composable
fun AgendaQrApp() {
    AmTheme {
        val profile = remember {
            val repository = createLocalProfileRepository()
            val created = repository.getOrCreate()
            // El scope de almacenamiento se activa ANTES de construir cualquier
            // repositorio local: todas las claves quedan namespaced por él.
            LocalProfileScope.activate(created.id)
            // Instalaciones previas: se detectan ESCANEANDO las claves locales
            // (sin crear el cliente Supabase ni tocar la red) y se copian al
            // namespace del perfil local de forma idempotente, sin borrar el
            // origen. Se hace ANTES de construir el grafo porque los
            // repositorios locales cargan su estado al construirse.
            legacyLocalProfileIdOrNull()?.let { legacyId ->
                if (legacyId != created.id) {
                    val collections = LegacyLocalDataMigration().migrate(legacyId, created.id)
                    // Bytes de comprobantes heredados: los ARCHIVOS físicos de
                    // la ruta antigua también se copian (idempotente, sin
                    // borrar el origen, fallo parcial sin aborto).
                    if (collections.hadLegacyData) {
                        LegacyComprobanteBytesMigration().migrate(legacyId, created.id)
                    }
                }
            }
            created
        }
        val protection = remember { createLocalProtectionRepository() }
        val accessViewModel = remember(profile, protection) {
            LocalAccessViewModel(profile, protection)
        }
        val accessState by accessViewModel.state.collectAsStateWithLifecycle()

        // Rastreador de flujos externos (U-02, D1): la app lo publica para
        // que los lanzadores de plataforma marquen "justo antes de lanzar";
        // la política de bloqueo lo consulta al pasar a segundo plano. La
        // publicación es SÍNCRONA (SideEffect): un LaunchedEffect podía
        // asignarlo después del primer lanzamiento.
        val externalFlows = remember { ExternalFlowTracker() }
        SideEffect { ExternalFlowGates.tracker = externalFlows }
        val externalFlowPolicy = remember(externalFlows) { ExternalFlowLockPolicy(externalFlows) }
        // Biometría: disponibilidad real de plataforma (fail-closed si falla).
        val biometricGate = remember { platformBiometricGate() }
        LaunchedEffect(biometricGate) {
            val availability = runCatching { biometricGate.availability() }
                .getOrDefault(BiometricAvailability.Unavailable)
            accessViewModel.setBiometricAvailability(availability)
        }
        val biometricScope = rememberCoroutineScope()
        fun requestBiometric() {
            biometricScope.launch {
                val result = runCatching { biometricGate.authenticate() }
                    .getOrDefault(com.agendaqr.destinations.domain.BiometricAuthResult.Unavailable)
                accessViewModel.onBiometricResult(result)
            }
        }

        // Modo de demostración: perfil aislado con datos ficticios.
        var demoActive by rememberSaveable { mutableStateOf(false) }
        var demoReady by rememberSaveable { mutableStateOf(false) }
        LaunchedEffect(demoActive) {
            if (demoActive) {
                val report = runCatching { DemoSeeder().seedIfEmpty() }.getOrNull()
                if (report != null) demoReady = true else demoActive = false
            } else {
                demoReady = false
            }
        }
        // El scope efectivo se activa de forma síncrona (mismo patrón que el
        // arranque): el grafo de abajo siempre lee el namespace correcto.
        val effectiveProfileId = remember(profile.id, demoActive, demoReady) {
            val id = if (demoActive && demoReady) DEMO_PROFILE_ID else profile.id
            LocalProfileScope.activate(id)
            id
        }
        val demoShowing = demoActive && demoReady && effectiveProfileId == DEMO_PROFILE_ID
        fun exitDemo() {
            demoActive = false
            accessViewModel.lock()
        }

        // Política de bloqueo (U-02, D1): la app se bloquea al pasar a
        // segundo plano, SALVO cuando el propio usuario lanzó un flujo
        // externo desde la app (gracia acotada de 120 s). Sin PIN
        // configurado no hay nada que proteger; en demo no se bloquea.
        LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
            externalFlowPolicy.onStop(demoShowing) { accessViewModel.lock() }
        }
        // Al volver con una marca pendiente: gracia vencida → bloquear;
        // la marca se consume siempre (no se acumula).
        LifecycleEventEffect(Lifecycle.Event.ON_START) {
            externalFlowPolicy.onStart(demoShowing) { accessViewModel.lock() }
        }
        // ON_RESUME: regreso real a primer plano. Un flujo translúcido puede
        // no producir ON_STOP; la marca se limpia siempre para que un paso a
        // segundo plano posterior vuelva a bloquear (fail-closed).
        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            externalFlowPolicy.onResume()
        }

        when {
            demoActive && !demoReady -> AmLoading(message = AppStrings.Cargando)
            demoShowing -> Column {
                // Demo edge-to-edge: el margen de pantalla lo pide el
                // banner (ScreenMargin, una sola vez); el host solo pone
                // el separador vertical hacia la app de abajo.
                AmStatusBanner(
                    message = AppStrings.DemostracionAislada,
                    title = AppStrings.ModoDemostracion,
                    tone = AmTone.Info,
                    action = AmBannerAction(AppStrings.SalirDeDemostracion, ::exitDemo),
                    modifier = Modifier.fillMaxWidth().padding(vertical = AmSpacing.Sm),
                    screenMargin = AmSpacing.ScreenMargin,
                )
                AgendaQrLocalApp(
                    profileId = DEMO_PROFILE_ID,
                    onLock = ::exitDemo,
                )
            }
            accessState.mode == LocalAccessMode.Onboarding -> LocalOnboardingScreen(
                state = accessState,
                onPinChanged = accessViewModel::onPinChanged,
                onConfirmationChanged = accessViewModel::onConfirmationChanged,
                onSubmit = accessViewModel::submitOnboarding,
                onExploreDemo = { demoActive = true },
            )
            accessState.mode == LocalAccessMode.Locked -> LocalLockScreen(
                state = accessState,
                onPinChanged = accessViewModel::onPinChanged,
                onSubmit = accessViewModel::submitUnlock,
                onBiometric = if (accessState.biometricAvailability == BiometricAvailability.Available) {
                    ::requestBiometric
                } else {
                    null
                },
                // U-20/D11: la demostración solo existe en el primer
                // arranque (onboarding); en desbloqueo no se ofrece.
            )
            accessState.mode == LocalAccessMode.Unlocked -> AgendaQrLocalApp(
                profileId = profile.id,
                onLock = accessViewModel::lock,
            )
        }
    }
}

/**
 * App local desbloqueada. El grafo de dependencias se construye ligado al
 * PERFIL LOCAL (no a un usuario remoto) y la navegación se entrega a
 * [AuthenticatedAppRoot].
 */
@Composable
private fun AgendaQrLocalApp(profileId: String, onLock: () -> Unit) {
    val graph = rememberAuthenticatedSessionGraph(profileId)
    AuthenticatedAppRoot(graph = graph, onLock = onLock)
}
