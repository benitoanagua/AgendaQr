package com.agendaqr.android.lab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.agendaqr.core.ui.lab.AgendaQrComponentLab

/**
 * Debug-only host for the AgendaQr component lab.
 *
 * This activity exists only in debug builds (src/debug source set) and is not
 * exported, so release builds and the launcher stay untouched. Open it with:
 *
 *   adb shell am start -n com.agendaqr.app/com.agendaqr.android.lab.ComponentLabActivity
 *
 * The lab renders inside AgendaQrTheme and touches no production navigation,
 * repositories or platform services.
 */
class ComponentLabActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AgendaQrComponentLab() }
    }
}
