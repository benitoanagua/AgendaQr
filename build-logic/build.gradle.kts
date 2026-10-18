plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.11.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.20")
    implementation("org.jetbrains.kotlin:compose-compiler-gradle-plugin:2.2.20")
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.8.2")
}

kotlin { jvmToolchain(17) }
