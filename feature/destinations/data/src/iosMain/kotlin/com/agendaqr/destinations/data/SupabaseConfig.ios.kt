package com.agendaqr.destinations.data

import platform.Foundation.NSBundle

actual fun supabaseConfig(): SupabaseConfig {
    val info = NSBundle.mainBundle.infoDictionary
    return SupabaseConfig(
        url = info?.get("SUPABASE_URL") as? String ?: "",
        publishableKey = info?.get("SUPABASE_PUBLISHABLE_KEY") as? String ?: "",
    )
}
