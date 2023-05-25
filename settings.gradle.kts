@file:Suppress("UnstableApiUsage")

rootProject.name = "rtvm-root"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
        gradlePluginPortal()
    }
}

includeBuild("rtvm") {
    dependencySubstitution {
        substitute(module("me.omico.rtvm:rtvm-annotation")).using(project(":annotation"))
        substitute(module("me.omico.rtvm:rtvm-api")).using(project(":api"))
        substitute(module("me.omico.rtvm:rtvm-runtime")).using(project(":runtime"))
        substitute(module("me.omico.rtvm:rtvm-compiler")).using(project(":compiler"))
    }
}

includeBuild("example")
