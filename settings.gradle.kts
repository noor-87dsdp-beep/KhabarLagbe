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
                
                // Validation with clearer error messages
                if (mapboxToken.isEmpty()) {
                    logger.error("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    logger.error("❌ MAPBOX_DOWNLOADS_TOKEN NOT FOUND!")
                    logger.error("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    logger.error("")
                    logger.error("📍 QUICK FIX - Add to ~/.gradle/gradle.properties:")
                    logger.error("   MAPBOX_DOWNLOADS_TOKEN=sk.your_secret_token_here")
                    logger.error("")
                    logger.error("📖 Get your token: https://account.mapbox.com/access-tokens/")
                    logger.error("   • Click 'Create a token'")
                    logger.error("   • Enable 'DOWNLOADS:READ' scope")
                    logger.error("   • Copy the token (starts with sk.)")
                    logger.error("")
                    logger.error("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                } else if (!mapboxToken.startsWith("sk.")) {
                    logger.error("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    logger.error("❌ INVALID MAPBOX_DOWNLOADS_TOKEN!")
                    logger.error("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    logger.error("")
                    logger.error("⚠️  Secret tokens must start with 'sk.' prefix")
                    logger.error("⚠️  You might be using a public token (pk.) instead")
                    logger.error("")
                    logger.error("📖 Get correct token: https://account.mapbox.com/access-tokens/")
                    logger.error("")
                    logger.error("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
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
 