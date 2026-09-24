pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // PREFER_SETTINGS keeps settings.gradle.kts as the single effective source
    // of repositories: project-declared repositories are ignored. The Kotlin
    // wasmJs plugin always registers the Node.js/Yarn distribution Ivy repos
    // by itself, which FAIL_ON_PROJECT_REPOS would reject; the content filters
    // below restrict those repos to org.nodejs / com.yarnpkg artifacts only.
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        ivy {
            url = uri("https://nodejs.org/dist")
            patternLayout { artifact("v[revision]/[artifact](-v[revision])-[classifier].[ext]") }
            metadataSources { artifact() }
            content { includeGroup("org.nodejs") }
        }
        ivy {
            url = uri("https://github.com/yarnpkg/yarn/releases/download")
            patternLayout { artifact("v[revision]/[artifact](-v[revision]).[ext]") }
            metadataSources { artifact() }
            content { includeGroup("com.yarnpkg") }
        }
        ivy {
            url = uri("https://github.com/WebAssembly/binaryen/releases/download")
            patternLayout { artifact("version_[revision]/binaryen-version_[revision]-[classifier].[ext]") }
            metadataSources { artifact() }
            content { includeGroup("com.github.webassembly") }
        }
    }
}

rootProject.name = "AgendaQr"
include(":androidApp")
include(":core:ui")
include(":feature:destinations:domain")
include(":feature:destinations:data")
include(":feature:destinations:presentation")
include(":shared")
