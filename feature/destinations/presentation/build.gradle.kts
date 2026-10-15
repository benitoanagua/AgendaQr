plugins {
    id("agendaqr.compose-library")
    // Capturas de referencia con Roborazzi (ADR-0007) sobre la suite
    // Robolectric existente.
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.agendaqr.destinations.presentation"
    testOptions {
        // Robolectric necesita los recursos del módulo para las pruebas
        // de UI Compose.
        unitTests.isIncludeAndroidResources = true
    }
}

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
            // collectAsStateWithLifecycle en commonMain.
            implementation(libs.lifecycle.runtime.compose)
            implementation("org.jetbrains.compose.ui:ui-backhandler:${libs.versions.compose.get()}")
            implementation(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(libs.activity.compose)
            implementation(libs.core.ktx)
            implementation(libs.zxing.core)
            // S03: captura con CameraX + análisis continuo.
            implementation(libs.camera.core)
            implementation(libs.camera.camera2)
            implementation(libs.camera.lifecycle)
            implementation(libs.camera.view)
        }
        commonTest.dependencies {
            implementation(libs.coroutines.test)
        }
        androidUnitTest.dependencies {
            implementation(kotlin("test"))
            // Pruebas de UI Compose sobre Robolectric (JVM): corren en el
            // gate existente (testDebugUnitTest) sin emulador en CI.
            implementation("androidx.compose.ui:ui-test-junit4:${libs.versions.androidx.compose.get()}")
            implementation("androidx.compose.ui:ui-test-manifest:${libs.versions.androidx.compose.get()}")
            implementation("org.robolectric:robolectric:${libs.versions.robolectric.get()}")
            // Capturas de referencia (ADR-0007).
            implementation(libs.roborazzi)
            implementation(libs.roborazzi.compose)
            implementation(libs.roborazzi.junit.rule)
        }
    }
}
