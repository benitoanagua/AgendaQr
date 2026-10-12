package com.agendaqr.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.agendaqr.destinations.data.AgendaQrAndroidStorage
import com.agendaqr.destinations.data.initializeNetworkMonitor
import com.agendaqr.shared.AgendaQrSharedApp
import com.agendaqr.destinations.presentation.AgendaQrAndroidShareLauncher
import com.agendaqr.destinations.presentation.AgendaQrAndroidImportLauncher

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // V1.2 (§12): edge-to-edge; el Scaffold consume los insets y el
        // estilo de las barras del sistema sigue al tema del sistema.
        enableEdgeToEdge()
        AgendaQrAndroidStorage.initialize(applicationContext)
        initializeNetworkMonitor(applicationContext)
        AgendaQrAndroidShareLauncher.initialize(this)
        AgendaQrAndroidImportLauncher.initialize(this)
        AgendaQrAndroidImportLauncher.handleShare(this, intent)
        setContent { AgendaQrSharedApp() }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        AgendaQrAndroidImportLauncher.handleShare(this, intent)
    }
}
