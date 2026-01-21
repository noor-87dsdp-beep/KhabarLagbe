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
                // Read token from multiple sources in priority order:
                // 1. ~/.gradle/gradle.properties (RECOMMENDED - secure & persistent)
                // 2. local.properties (project-specific)
                // 3. Environment variable (CI/CD)
                val mapboxToken = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").orNull
                    ?: localProperties.getProperty("MAPBOX_DOWNLOADS_TOKEN")
                    ?: providers.environmentVariable("MAPBOX_DOWNLOADS_TOKEN").orNull
                    ?: ""
                
                password = mapboxToken
                
                // Validation - show warning but don't fail build (Mapbox is optional)
                if (mapboxToken.isEmpty()) {
                    logger.warn("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    logger.warn("⚠️ MAPBOX_DOWNLOADS_TOKEN NOT FOUND (Mapbox features disabled)")
                    logger.warn("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    logger.warn("📍 To enable Mapbox, add to ~/.gradle/gradle.properties:")
                    logger.warn("   MAPBOX_DOWNLOADS_TOKEN=sk.your_secret_token_here")
                    logger.warn("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                } else if (!mapboxToken.startsWith("sk.")) {
                    logger.warn("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    logger.warn("⚠️ INVALID MAPBOX_DOWNLOADS_TOKEN (Mapbox features disabled)")
                    logger.warn("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    logger.warn("⚠️ Secret tokens must start with 'sk.' prefix")
                    logger.warn("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                } else {
                    logger.info("✅ Mapbox authentication configured successfully")
                }
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
 