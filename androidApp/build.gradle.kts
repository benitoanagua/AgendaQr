plugins { id("agendaqr.android-application") }

android {
    namespace = "com.agendaqr.android"
    defaultConfig {
        buildConfigField("String", "SUPABASE_URL", "\"" + "${providers.gradleProperty("SUPABASE_URL").orNull ?: ""}" + "\"")
        buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", "\"" + "${providers.gradleProperty("SUPABASE_PUBLISHABLE_KEY").orNull ?: ""}" + "\"")
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
}
