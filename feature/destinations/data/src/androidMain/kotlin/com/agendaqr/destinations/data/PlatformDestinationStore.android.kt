package com.agendaqr.destinations.data

import android.content.Context
import android.content.SharedPreferences

object AgendaQrAndroidStorage {
    private var preferences: SharedPreferences? = null
    private var applicationFilesDir: java.io.File? = null

    fun initialize(context: Context) {
        val applicationContext = context.applicationContext
        preferences = applicationContext.getSharedPreferences("agendaqr", Context.MODE_PRIVATE)
        applicationFilesDir = applicationContext.filesDir
    }

    internal fun requirePreferences(): SharedPreferences =
        requireNotNull(preferences) { "AgendaQrAndroidStorage must be initialized before creating the repository." }

    internal fun requireFilesDir(): java.io.File =
        requireNotNull(applicationFilesDir) {
            "AgendaQrAndroidStorage must be initialized before creating the repository."
        }


}

private object AndroidDestinationStore : DestinationStore {
    override fun read(key: String): String? = AgendaQrAndroidStorage.requirePreferences().getString(key, null)
    override fun write(key: String, value: String) { AgendaQrAndroidStorage.requirePreferences().edit().putString(key, value).apply() }
}

actual fun platformDestinationStore(): DestinationStore = AndroidDestinationStore

actual fun createDestinationRepository(): com.agendaqr.destinations.domain.DestinationRepository = LocalDestinationRepository()
