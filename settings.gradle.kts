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
        // Mapbox SDK repository
        // Currently commented out since Mapbox dependencies are disabled
        // To enable: Set MAPBOX_DOWNLOADS_TOKEN and uncomment Mapbox dependencies
        // maven {
        //     url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
        //     credentials {
        //         username = "mapbox"
        //         password = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN")
        //             .orElse(providers.environmentVariable("MAPBOX_DOWNLOADS_TOKEN"))
        //             .orElse("")
        //             .get()
        //     }
        //     authentication {
        //         create<BasicAuthentication>("basic")
        //     }
        // }
    }
}

rootProject.name = "KhabarLagbe"
include(":app")
include(":rider-app")
include(":restaurant-app")
 