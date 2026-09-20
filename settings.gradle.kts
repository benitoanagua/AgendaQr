pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
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
