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
}
