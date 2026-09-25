plugins { id("agendaqr.compose-library") }

android { namespace = "com.agendaqr.destinations.presentation" }

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:destinations:domain"))
            implementation(project(":feature:destinations:data"))
            implementation(project(":core:ui"))
            implementation(compose.material3)
            implementation(compose.animation)
        }
        androidMain.dependencies {
            implementation(libs.activity.compose)
            implementation(libs.core.ktx)
            implementation(libs.zxing.core)
        }
    }
}
