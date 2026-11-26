plugins {
    id("agendaqr.wasm-app")
}

// Component Lab web del sistema de diseño (ADR-0049): única app wasmJs
// del repo. Depende solo de :core:ui — nunca de domain/data ni de las
// pantallas de producto.
kotlin {
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "agendaqr-ui-lab.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        wasmJsMain.dependencies {
            implementation(project(":core:ui"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(libs.kotlinx.browser)
        }
    }
}
