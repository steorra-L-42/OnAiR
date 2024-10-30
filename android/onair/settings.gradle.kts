pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "OnAiR"
include(":app")
include(":core:network")
include(":core:common")
include(":core:designsystem")
include(":domain")
include(":data")
include(":features:auth")
include(":features:broadcast")
include(":core:notification")
include(":features:media")
include(":features:statistics")
include(":core:navigation")
