plugins {
    id("agendaqr.kmp-library")
    alias(libs.plugins.kotlinSerialization)
}

android { namespace = "com.agendaqr.destinations.domain" }

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.serialization.json)
            implementation(libs.coroutines.core)
        }
    }
}
