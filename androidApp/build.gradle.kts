plugins { id("agendaqr.android-application") }

android {
    namespace = "com.agendaqr.android"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":feature:destinations:data"))
    implementation(project(":feature:destinations:presentation"))
    // Debug-only host for the component lab (core:ui). Release builds do not
    // depend on the lab entry point through this configuration.
    debugImplementation(project(":core:ui"))
    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
}
