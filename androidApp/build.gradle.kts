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
    androidTestImplementation("androidx.test:core-ktx:1.6.1")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation(kotlin("test"))
}
