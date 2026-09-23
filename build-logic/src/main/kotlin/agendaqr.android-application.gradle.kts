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
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
    signingConfigs {
        create("release") {
            val prodKeystore = providers.environmentVariable("ANDROID_KEYSTORE_FILE").orNull
            val prodStorePassword = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD").orNull
            val prodKeyAlias = providers.environmentVariable("ANDROID_KEY_ALIAS").orNull
            val prodKeyPassword = providers.environmentVariable("ANDROID_KEY_PASSWORD").orNull
            if (prodKeystore != null && prodStorePassword != null && prodKeyAlias != null && prodKeyPassword != null && file(prodKeystore).exists()) {
                storeFile = file(prodKeystore)
                storePassword = prodStorePassword
                keyAlias = prodKeyAlias
                keyPassword = prodKeyPassword
            } else {
                if (prodKeystore != null) {
                    logger.warn("Production keystore not found at $prodKeystore, using debug signing configuration for RC")
                }
                val debugConfig = signingConfigs.getByName("debug")
                storeFile = debugConfig.storeFile
                storePassword = debugConfig.storePassword
                keyAlias = debugConfig.keyAlias
                keyPassword = debugConfig.keyPassword
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
