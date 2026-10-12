pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // PREFER_SETTINGS keeps settings.gradle.kts as the single effective
    // source of repositories: project-declared repositories are ignored.
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AgendaQr"
include(":androidApp")
include(":core:ui")
include(":feature:destinations:domain")
include(":feature:destinations:data")
include(":feature:destinations:presentation")
include(":shared")
include(":supabase:acceptance")
