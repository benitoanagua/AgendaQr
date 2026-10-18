plugins { id("agendaqr.compose-library") }

// Sistema de diseño y vectores de iconos del producto viven en este módulo.

android {
    namespace = "com.agendaqr.core.ui"
    testOptions {
        // Pruebas de UI Compose sobre Robolectric en androidUnitTest
        // (AmAccessibilityContractTest, AmResponsiveLayoutContractTest):
        // necesitan el manifest de test (ComponentActivity) y los recursos.
        unitTests.isIncludeAndroidResources = true
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)
            // Tokens de movimiento (Easing) y las transiciones de
            // superficie consumen androidx.compose.animation(.core).
            implementation(compose.animation)
            // WindowSizeClass para la adaptación compacto/medium/expanded
            // (AmNavBar vs AmNavRail, contenido centrado con
            // ContentMaxWidth).
            implementation(libs.material3.window.size)
        }
        androidUnitTest.dependencies {
            implementation(kotlin("test"))
            // Pruebas semánticas de los componentes del sistema bajo
            // Robolectric.
            implementation("androidx.compose.ui:ui-test-junit4:${libs.versions.androidx.compose.get()}")
            implementation("androidx.compose.ui:ui-test-manifest:${libs.versions.androidx.compose.get()}")
            implementation("org.robolectric:robolectric:${libs.versions.robolectric.get()}")
        }
    }
}
