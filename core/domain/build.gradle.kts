plugins {
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.studysmart.core.domain"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        
        val localProperties = java.util.Properties()
        val localFile = project.rootProject.file("local.properties")
        if (localFile.exists()) {
            localProperties.load(localFile.inputStream())
        }
        val apiKey = localProperties.getProperty("GEMINI_API_KEY") ?: "PLACEHOLDER_KEY"
        
        buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.io.ktor.client.core)
    implementation(libs.io.ktor.serialization.kotlinx.json)
    testImplementation(libs.junit)
}
