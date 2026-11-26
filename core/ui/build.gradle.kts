plugins { id("agendaqr.compose-library") }

// Sistema de diseño y vectores de iconos del producto viven en este módulo.

android {
    namespace = "com.agendaqr.core.ui"
    testOptions {
        // Pruebas de UI Compose sobre Robolectric en androidUnitTest
        // (AqAccessibilityContractTest, AqResponsiveLayoutContractTest):
        // necesitan el manifest de test (ComponentActivity) y los recursos.
        unitTests.isIncludeAndroidResources = true
    }
}

kotlin {
    // wasmJs solo en este módulo (no en el convention plugin compartido):
    // alimenta el Component Lab web (:core:ui-lab). El resto de módulos
    // KMP no publica variante web.
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        // `browser()` en una librería no genera distribución: solo declara
        // el entorno JS que el consumidor (:core:ui-lab) necesita para
        // resolver el toolchain npm de Kotlin/Wasm.
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(compose.material3)
            // Tokens de movimiento (Easing) y las transiciones de
            // superficie consumen androidx.compose.animation(.core).
            implementation(compose.animation)
            // WindowSizeClass para la adaptación compacto/medium/expanded
            // (AqNavBar vs AqNavRail, contenido centrado con
            // ContentMaxWidth).
            implementation(libs.material3.window.size)
            // Inter se distribuye como recurso multiplataforma (D-05): el
            // generador produce Res.font.* en tiempo de compilación.
            implementation(compose.components.resources)
            // Previews de token en claro, oscuro y 200 % de fuente.
            implementation(compose.components.uiToolingPreview)
        }
        androidUnitTest.dependencies {
            implementation(kotlin("test"))
            // Pruebas semánticas de los componentes del sistema bajo
            // Robolectric.
            implementation("androidx.compose.ui:ui-test-junit4:${libs.versions.androidx.compose.get()}")
            implementation("androidx.compose.ui:ui-test-manifest:${libs.versions.androidx.compose.get()}")
            implementation("org.robolectric:robolectric:${libs.versions.robolectric.get()}")
            // Capturas de componentes del sistema de diseño bajo demanda
            // (evidencia de fidelidad Moon; mismo mecanismo que la galería
            // de pantallas, ADR-0007). No-op sin -Pagendaqr.capture=true.
            implementation(libs.roborazzi)
            implementation(libs.roborazzi.compose)
        }
        // Fixture de fidelidad Moon v1 generado desde el JSON del Figma
        // (`generateMoonSpecFixtures`, ADR-0050); nunca se versiona.
        androidUnitTest {
            kotlin.srcDir(layout.buildDirectory.dir("generated/moonSpec/kotlin"))
        }
    }
}

// La prueba de fidelidad consume el fixture generado en la raíz del proyecto.
tasks.matching { it.name == "compileDebugUnitTestKotlinAndroid" }.configureEach {
    dependsOn(":generateMoonSpecFixtures")
}

tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
    val capture = providers.gradleProperty("agendaqr.capture").orNull == "true"
    systemProperty("agendaqr.capture", capture.toString())
    if (capture) {
        systemProperty("roborazzi.test.record", "true")
        outputs.upToDateWhen { false }
    }
}

tasks.register("captureComponents") {
    group = "verification"
    description = "Regenera las capturas de componentes Aq (requiere -Pagendaqr.capture=true)."
    dependsOn("testDebugUnitTest")
    doLast {
        if (providers.gradleProperty("agendaqr.capture").orNull != "true") {
            throw GradleException(
                "Ejecuta: ./gradlew :core:ui:captureComponents -Pagendaqr.capture=true",
            )
        }
    }
}
