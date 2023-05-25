@file:Suppress("UnstableApiUsage")

rootProject.name = "rtvm-example"

pluginManagement {
    includeBuild("../build-logic/gradm")
    repositories {
        google()
        mavenCentral()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
        gradlePluginPortal()
    }
}

plugins {
    id("me.omico.gradm.generated")
}

includeBuild("../build-logic")

include(":app")
