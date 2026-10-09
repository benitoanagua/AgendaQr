plugins { id("agendaqr.compose-library") }

// T9 — tipografía Xauxa: los recursos de fuente (Archivo, OFL 1.1) viven en
// composeResources y se exponen solo vía XauxaType.
compose {
    resources {
        publicResClass = true
        packageOfResClass = "com.agendaqr.core.ui"
    }
}

android {
    namespace = "com.agendaqr.core.ui"
    testOptions {
        // Pruebas de UI Compose sobre Robolectric en androidUnitTest
        // (XauxaAccessibilityContractTest, XauxaResponsiveLayoutContractTest):
        // necesitan el manifest de test (ComponentActivity) y los recursos.
        unitTests.isIncludeAndroidResources = true
    }
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
    // WebAssembly host of the component lab. It is a development tool that
    // runs in the browser: the entrypoint lives in wasmJsMain and nothing in
    // the Android/iOS production targets can reach it.
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "agendaqr-component-lab.js"
            }
            // Executing the wasmJs commonTest suite in this environment requires
            // a headless browser (Karma + Chrome), which is not part of the
            // build contract of this repository. The suite still compiles for
            // wasmJs and the same 26 model tests execute on the JVM in
            // testDebugUnitTest / testReleaseUnitTest.
            testTask {
                enabled = false
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)
            // T9 — recursos Compose (fuentes Xauxa empaquetadas).
            implementation(compose.components.resources)
            // Xauxa motion tokens (Easing) and the LiveTile / turnstile-nav
            // components consume androidx.compose.animation(.core) directly.
            implementation(compose.animation)
            // T2/V1.1 (ADR-0005): iconografía Lucide (ISC) consumida solo a
            // través de XauxaIcon. Objetivos: android/ios/wasmJs verificados
            // por compilación en esta tarea.
            implementation(libs.icons.lucide)
        }
        androidUnitTest.dependencies {
            implementation(kotlin("test"))
            // Pruebas semánticas de los componentes Xauxa bajo Robolectric.
            implementation("androidx.compose.ui:ui-test-junit4:${libs.versions.androidx.compose.get()}")
            implementation("androidx.compose.ui:ui-test-manifest:${libs.versions.androidx.compose.get()}")
            implementation("org.robolectric:robolectric:${libs.versions.robolectric.get()}")
        }
    }
}
