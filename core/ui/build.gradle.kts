plugins { id("agendaqr.compose-library") }

// AgendaMobile (ADR-0013): el sistema de diseño vive en este módulo. Los
// recursos de Compose exponen el logo público del producto.
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
        // (AmAccessibilityContractTest, AmResponsiveLayoutContractTest):
        // necesitan el manifest de test (ComponentActivity) y los recursos.
        unitTests.isIncludeAndroidResources = true
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)
            // Recursos Compose (logo público del producto).
            implementation(compose.components.resources)
            // Tokens de movimiento (Easing) y las transiciones de
            // superficie consumen androidx.compose.animation(.core).
            implementation(compose.animation)
            // Iconografía Lucide (ISC) consumida SOLO a través de AmIcon.
            // Licencia registrada en docs/05-design-system/06-licencias-terceros.md.
            implementation(libs.icons.lucide)
            // WindowSizeClass para la adaptación compacto/medium/expanded
            // (AmNavBar vs AmNavRail, contenido centrado con
            // ContentMaxWidth).
            implementation(libs.material3.window.size)
        }
        androidUnitTest.dependencies {
            implementation(kotlin("test"))
            // Pruebas semánticas de los componentes AgendaMobile bajo
            // Robolectric.
            implementation("androidx.compose.ui:ui-test-junit4:${libs.versions.androidx.compose.get()}")
            implementation("androidx.compose.ui:ui-test-manifest:${libs.versions.androidx.compose.get()}")
            implementation("org.robolectric:robolectric:${libs.versions.robolectric.get()}")
        }
    }
}
