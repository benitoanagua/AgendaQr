plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Aviso de licencias de terceros (Lucide + envoltorio composeicons): se
// copia a assets generados en tiempo de build y AGP lo empaqueta en el
// APK. La fuente canónica vive en `third-party-notices/`; no hay scripts
// externos.
val thirdPartyNotices = layout.projectDirectory.dir("third-party-notices")
val generatedThirdPartyNotices = layout.buildDirectory.dir("generated/thirdPartyNotices")
val generateThirdPartyNotices = tasks.register<Sync>("generateThirdPartyNotices") {
    description = "Copia los avisos de licencias de terceros a los assets del distribuible."
    from(thirdPartyNotices)
    into(generatedThirdPartyNotices)
}

android {
    compileSdk = 36
    // Los avisos generados entran como assets del distribuible.
    sourceSets.getByName("main").assets.srcDir(generatedThirdPartyNotices)
    defaultConfig {
        applicationId = "com.agendaqr.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
        // Instrumentados de la app (share intents/singleTask/URI).
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release") {
            val prodKeystore = providers.environmentVariable("ANDROID_KEYSTORE_FILE").orNull
            val prodStorePassword = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD").orNull
            val prodKeyAlias = providers.environmentVariable("ANDROID_KEY_ALIAS").orNull
            val prodKeyPassword = providers.environmentVariable("ANDROID_KEY_PASSWORD").orNull
            val allowDebugSigningForRc = providers.gradleProperty("allowDebugSigningForRc")
                .map(String::toBoolean)
                .orElse(false)
                .get()
            val productionSigningReady =
                prodKeystore != null &&
                    prodStorePassword != null &&
                    prodKeyAlias != null &&
                    prodKeyPassword != null &&
                    file(requireNotNull(prodKeystore)).exists()

            if (productionSigningReady) {
                storeFile = file(requireNotNull(prodKeystore))
                storePassword = prodStorePassword
                keyAlias = prodKeyAlias
                keyPassword = prodKeyPassword
            } else if (allowDebugSigningForRc) {
                logger.warn("Debug signing explicitly enabled with -PallowDebugSigningForRc=true")
                val debugConfig = signingConfigs.getByName("debug")
                storeFile = debugConfig.storeFile
                storePassword = debugConfig.storePassword
                keyAlias = debugConfig.keyAlias
                keyPassword = debugConfig.keyPassword
            } else {
                throw GradleException(
                    "Release signing requires ANDROID_KEYSTORE_FILE, ANDROID_KEYSTORE_PASSWORD, " +
                        "ANDROID_KEY_ALIAS and ANDROID_KEY_PASSWORD pointing to an existing production keystore. " +
                        "For an explicit local RC only, use -PallowDebugSigningForRc=true."
                )
            }
        }
    }
    // D6: advertencias triaged con justificación en lint-baseline.xml
    // (versiones de libs: actualización dedicada; icono y reglas de
    // backup: decisión de producto pendiente; onBackInvokedCallback:
    // intencional API 33+).
    lint {
        baseline = project.file("lint-baseline.xml")
        warningsAsErrors = false
        // D6: `./gradlew :androidApp:updateLintBaseline` regenera el archivo.
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures.compose = true
}

// El empaquetado de assets corre tras `preBuild`: asegura que los avisos
// estén generados antes de fusionarlos en el APK/AAB.
tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn(generateThirdPartyNotices)
}
