plugins { id("agendaqr.android-application") }

android {
    namespace = "com.agendaqr.android"
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
}
