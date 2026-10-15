package com.agendaqr.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.agendaqr.destinations.data.AgendaQrAndroidStorage
import com.agendaqr.destinations.data.BiometricContextHolder
import com.agendaqr.destinations.data.BiometricPromptHost
import com.agendaqr.destinations.data.initializeNetworkMonitor
import com.agendaqr.shared.AgendaQrSharedApp
import com.agendaqr.destinations.presentation.AgendaQrAndroidShareLauncher
import com.agendaqr.destinations.presentation.AgendaQrAndroidImportLauncher

/**
 * FragmentActivity (no ComponentActivity): `BiometricPrompt` exige un
 * dueño de ciclo de vida fragment-based para el diálogo biométrico. El
 * resto del host es idéntico (edge-to-edge, launchers, share).
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // V1.4 (§12): edge-to-edge; el Scaffold consume los insets y el
        // estilo de las barras del sistema sigue al tema del sistema.
        enableEdgeToEdge()
        AgendaQrAndroidStorage.initialize(applicationContext)
        BiometricContextHolder.context = applicationContext
        BiometricPromptHost.activity = this
        initializeNetworkMonitor(applicationContext)
        AgendaQrAndroidShareLauncher.initialize(this)
        AgendaQrAndroidImportLauncher.initialize(this)
        AgendaQrAndroidImportLauncher.handleShare(this, intent)
        setContent { AgendaQrSharedApp() }
    }

    override fun onDestroy() {
        // Solo si la referencia sigue apuntando a ESTA instancia: durante
        // una recreación, la nueva Activity ya se registró en onCreate.
        BiometricPromptHost.release(this)
        super.onDestroy()
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        AgendaQrAndroidImportLauncher.handleShare(this, intent)
    }
}
