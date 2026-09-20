plugins { id("agendaqr.compose-library") }

android { namespace = "com.agendaqr.core.ui" }

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)
        }
    }
}
