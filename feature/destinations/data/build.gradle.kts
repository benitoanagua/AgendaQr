plugins {
    id("agendaqr.kmp-library")
    alias(libs.plugins.kotlinSerialization)
}

android { namespace = "com.agendaqr.destinations.data" }

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:destinations:domain"))
            implementation(libs.serialization.json)
            implementation(libs.coroutines.core)
        }
        commonTest.dependencies { implementation(libs.coroutines.test) }
        androidMain.dependencies { }
        iosMain.dependencies { }
    }
}
