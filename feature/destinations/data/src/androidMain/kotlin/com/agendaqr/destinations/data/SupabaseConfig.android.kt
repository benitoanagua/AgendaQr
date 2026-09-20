package com.agendaqr.destinations.data

import com.agendaqr.android.BuildConfig

actual fun supabaseConfig(): SupabaseConfig =
    SupabaseConfig(
        url = BuildConfig.SUPABASE_URL,
        publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY,
    )
