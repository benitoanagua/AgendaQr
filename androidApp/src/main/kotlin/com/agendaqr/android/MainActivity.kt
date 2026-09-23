package com.agendaqr.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.agendaqr.destinations.data.AgendaQrAndroidStorage
import com.agendaqr.destinations.data.initializeNetworkMonitor
import com.agendaqr.shared.AgendaQrSharedApp
import com.agendaqr.destinations.presentation.AgendaQrAndroidShareLauncher
import com.agendaqr.destinations.presentation.AgendaQrAndroidImportLauncher

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
