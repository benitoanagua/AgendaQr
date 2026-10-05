package com.agendaqr.android

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import kotlin.test.Test
import kotlin.test.assertTrue
import org.junit.runner.RunWith

/**
 * D4 — contratos de entrada Android, instrumentados (emulador). Job de CI
 * no bloqueante hasta 5 corridas estables (documentado en docs).
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class ShareIntentsTest {

    private val target: Context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun activity_is_singleTask_and_answers_send_intents() {
        val info = target.packageManager.getActivityInfo(
            ComponentName(target, MainActivity::class.java),
            0,
        )
        // singleTask = 2 (ActivityInfo.LAUNCH_MODE_SINGLE_TASK es API oculta);
        // el badging del APK lo confirma: launchMode="singleTask".
        assertTrue(info.launchMode == 2)
        assertTrue(
            target.packageManager
                .queryIntentActivities(Intent(Intent.ACTION_SEND).setType("image/png").apply { `package` = target.packageName }, 0)
                .isNotEmpty(),
        )
        assertTrue(
            target.packageManager
                .queryIntentActivities(Intent(Intent.ACTION_SEND).setType("application/pdf").apply { `package` = target.packageName }, 0)
                .isNotEmpty(),
        )
    }

    @Test
    fun action_send_with_granted_uri_launches_without_crash() {
        val file = java.io.File(target.cacheDir, "share-test.png").apply {
            writeBytes(byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte()))
        }
        val uri = FileProvider.getUriForFile(target, target.packageName + ".fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setClassName(target, "com.agendaqr.android.MainActivity")
        }
        ActivityScenario.launch<MainActivity>(intent)
        Thread.sleep(1500)
    }

    @Test
    fun revoked_uri_does_not_crash() {
        val noGrant = Uri.parse("content://${target.packageName}.fileprovider/share-test-no-existe.png")
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, noGrant)
            setClassName(target, "com.agendaqr.android.MainActivity")
        }
        ActivityScenario.launch<MainActivity>(intent)
        Thread.sleep(1500)
    }
}
