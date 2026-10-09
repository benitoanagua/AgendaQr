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
    androidTestImplementation(libs.core.ktx)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    // Ronda 2 QA (ADR-0010): UiAutomator para flujos de sistema.
    androidTestImplementation(libs.androidx.uiautomator)
    // Compose semantics en instrumentados (búsqueda de nodos reales).
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${libs.versions.androidx.compose.get()}")
    androidTestImplementation(kotlin("test"))
}
