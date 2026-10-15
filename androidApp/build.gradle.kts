plugins { id("agendaqr.android-application") }

android {
    namespace = "com.agendaqr.android"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":feature:destinations:data"))
    implementation(project(":feature:destinations:presentation"))
    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
    // Biometría: BiometricPrompt exige un FragmentActivity como dueño del
    // ciclo de vida del prompt (la dependencia la aporta :data).
    implementation(libs.androidx.fragment)
    androidTestImplementation(libs.core.ktx)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.uiautomator)
    androidTestImplementation(kotlin("test"))
}
