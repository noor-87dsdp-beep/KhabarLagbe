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
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                // Validate Mapbox token before attempting to use repository
                val mapboxToken = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN")
                    .orElse(providers.environmentVariable("MAPBOX_DOWNLOADS_TOKEN"))
                    .getOrElse("")
                
                if (mapboxToken.isEmpty()) {
                    logger.warn("⚠️  MAPBOX_DOWNLOADS_TOKEN not found!")
                    logger.warn("⚠️  Mapbox dependencies will fail to download.")
                    logger.warn("⚠️  See MAPBOX_SETUP.md for configuration instructions.")
                } else if (!mapboxToken.startsWith("sk.")) {
                    logger.warn("⚠️  MAPBOX_DOWNLOADS_TOKEN should start with 'sk.'")
                    logger.warn("⚠️  Current value starts with: ${mapboxToken.take(3)}")
                }
                
                password = mapboxToken
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

rootProject.name = "KhabarLagbe"
include(":app")
include(":rider-app")
include(":restaurant-app")
 