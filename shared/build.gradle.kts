plugins { id("agendaqr.compose-library") }

android { namespace = "com.agendaqr.shared" }

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:ui"))
            implementation(project(":feature:destinations:presentation"))
        }
        iosMain.dependencies { }
    }
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "AgendaQrShared"
            isStatic = true
        }
    }
}
