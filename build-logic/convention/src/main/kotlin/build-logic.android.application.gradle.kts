@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    kotlin("android")
    id("me.omico.gradm.generated")
}

kotlin {
    jvmToolchain(11)
}

android {
    compileSdk = 33
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-P", "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$buildDir/compose",
            "-P", "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$buildDir/compose",
        )
    }
}
