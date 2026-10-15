package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.destinations.data.LegacyLocalDataMigration
import com.agendaqr.destinations.data.LocalProfileScope
import com.agendaqr.destinations.data.createLocalProfileRepository
import com.agendaqr.destinations.data.createLocalProtectionRepository
import com.agendaqr.destinations.data.legacyLocalProfileIdOrNull

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
                    LegacyLocalDataMigration().migrate(legacyId, created.id)
                }
            }
            created
        }
        val protection = remember { createLocalProtectionRepository() }
        val accessViewModel = remember(profile, protection) {
            LocalAccessViewModel(profile, protection)
        }
        val accessState by accessViewModel.state.collectAsStateWithLifecycle()

        // Política de bloqueo: la app se bloquea al pasar a segundo plano.
        // Sin PIN configurado no hay nada que proteger, así que el bloqueo
        // no altera el primer arranque.
        LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
            accessViewModel.lock()
        }

        when (accessState.mode) {
            LocalAccessMode.Onboarding -> LocalOnboardingScreen(
                state = accessState,
                onPinChanged = accessViewModel::onPinChanged,
                onConfirmationChanged = accessViewModel::onConfirmationChanged,
                onSubmit = accessViewModel::submitOnboarding,
            )
            LocalAccessMode.Locked -> LocalLockScreen(
                state = accessState,
                onPinChanged = accessViewModel::onPinChanged,
                onSubmit = accessViewModel::submitUnlock,
            )
            LocalAccessMode.Unlocked -> AgendaQrLocalApp(
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
