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
            // BackHandler/PredictiveBackHandler multiplataforma (CMP 1.8.x):
            // en Android delega en OnBackPressedDispatcher (gesto/back real),
            // en iOS/web en el dispatcher de la plataforma.
            implementation("org.jetbrains.compose.ui:ui-backhandler:${libs.versions.compose.get()}")
            implementation(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(libs.activity.compose)
            implementation(libs.core.ktx)
            implementation(libs.zxing.core)
        }
        commonTest.dependencies {
            implementation(libs.coroutines.test)
        }
    }
}
