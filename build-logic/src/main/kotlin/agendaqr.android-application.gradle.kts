plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    compileSdk = 36
    defaultConfig {
        applicationId = "com.agendaqr.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
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
                storeFile = file(prodKeystore)
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
