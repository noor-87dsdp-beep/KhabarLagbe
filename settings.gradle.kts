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

// Load local.properties file
val localProperties = java.util.Properties()
val localPropertiesFile = File(rootDir, "local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
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
                val mapboxToken = localProperties.getProperty("MAPBOX_DOWNLOADS_TOKEN")
                    ?: providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").orNull
                    ?: providers.environmentVariable("MAPBOX_DOWNLOADS_TOKEN").orNull
                    ?: ""
                
                if (mapboxToken.isEmpty()) {
                    logger.warn("⚠️  MAPBOX_DOWNLOADS_TOKEN not found!")
                    logger.warn("⚠️  Mapbox dependencies will fail to download.")
                    logger.warn("⚠️  See MAPBOX_SETUP.md for configuration instructions.")
                } else if (!mapboxToken.startsWith("sk.")) {
                    logger.warn("⚠️  MAPBOX_DOWNLOADS_TOKEN appears to be invalid!")
                    logger.warn("⚠️  Secret tokens should start with 'sk.' prefix")
                    logger.warn("⚠️  Please verify your token in local.properties")
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
 