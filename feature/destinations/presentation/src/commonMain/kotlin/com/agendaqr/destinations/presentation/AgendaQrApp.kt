package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqBannerAction
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTheme
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

@Composable
fun AgendaQrApp() {
    AqTheme {
        val profile = remember {
            val repository = createLocalProfileRepository()
            val created = repository.getOrCreate()
            LocalProfileScope.activate(created.id)
            legacyLocalProfileIdOrNull()?.let { legacyId ->
                if (legacyId != created.id) {
                    val collections = LegacyLocalDataMigration().migrate(legacyId, created.id)
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

        val externalFlows = remember { ExternalFlowTracker() }
        SideEffect { ExternalFlowGates.tracker = externalFlows }
        val externalFlowPolicy = remember(externalFlows) { ExternalFlowLockPolicy(externalFlows) }
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
        LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
            externalFlowPolicy.onStop(demoShowing) { accessViewModel.lock() }
        }
        LifecycleEventEffect(Lifecycle.Event.ON_START) {
            externalFlowPolicy.onStart(demoShowing) { accessViewModel.lock() }
        }
        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            externalFlowPolicy.onResume()
        }

        when {
            demoActive && !demoReady -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                ) {
                    com.agendaqr.core.ui.components.AqCircularLoader(
                        loaderSize = com.agendaqr.core.ui.theme.AqSize.Md,
                        contentDescription = null,
                    )
                    AqText(AppStrings.Cargando, color = com.agendaqr.core.ui.theme.AqColor.TextSecondary)
                }
            }
            demoShowing -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    AqAlert(
                        title = AppStrings.ModoDemostracion,
                        message = AppStrings.DemostracionAislada,
                        tone = com.agendaqr.core.ui.theme.AqTone.Info,
                        dismissLabel = AppStrings.SalirDeDemostracion,
                        onDismiss = ::exitDemo,
                    )
                    AgendaQrLocalApp(
                        profileId = DEMO_PROFILE_ID,
                        onLock = ::exitDemo,
                    )
                }
            }
            accessState.mode == LocalAccessMode.Onboarding -> LocalOnboardingScreen(
                state = accessState,
                viewModel = LocalAccessViewModel(profile, protection),
                appHosts = null,
                onSubmit = { },
            )
            accessState.mode == LocalAccessMode.Locked -> LocalLockScreen(
                state = accessState,
                viewModel = LocalAccessViewModel(profile, protection),
                appHosts = null,
                onSubmit = { },
                onCancel = { },
            )
            else -> AgendaQrLocalApp(
                profileId = profile.id,
                onLock = { accessViewModel.lock() },
            )
        }
    }
}
