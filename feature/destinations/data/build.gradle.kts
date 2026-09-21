plugins {
    id("agendaqr.kmp-library")
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.agendaqr.destinations.data"
    buildFeatures { buildConfig = true }
    defaultConfig {
        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"" + (
                providers.gradleProperty("SUPABASE_URL")
                    .orElse(providers.environmentVariable("SUPABASE_URL"))
                    .orNull ?: ""
            ) + "\"",
        )
        buildConfigField(
            "String",
            "SUPABASE_PUBLISHABLE_KEY",
            "\"" + (
                providers.gradleProperty("SUPABASE_PUBLISHABLE_KEY")
                    .orElse(providers.environmentVariable("SUPABASE_PUBLISHABLE_KEY"))
                    .orNull ?: ""
            ) + "\"",
        )
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.jan-tennert.supabase:postgrest-kt:${libs.versions.supabase.get()}")
            implementation("io.github.jan-tennert.supabase:auth-kt:${libs.versions.supabase.get()}")
            implementation("io.github.jan-tennert.supabase:storage-kt:${libs.versions.supabase.get()}")
            implementation(project(":feature:destinations:domain"))
            implementation(libs.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.coroutines.core)
        }
        commonTest.dependencies { implementation(libs.coroutines.test) }
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-android:${libs.versions.ktor.get()}")
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:${libs.versions.ktor.get()}")
        }
    }
}