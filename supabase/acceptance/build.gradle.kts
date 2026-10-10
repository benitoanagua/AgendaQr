plugins {
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.postgresql:postgresql:42.7.4")
}

tasks.test {
    // Raíz del repo para localizar supabase/migrations y supabase/tests.
    systemProperty("repo.root", rootDir.path)
}
