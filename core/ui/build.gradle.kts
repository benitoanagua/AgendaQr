plugins { id("agendaqr.compose-library") }

android { namespace = "com.agendaqr.core.ui" }

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
        }
    }
}
