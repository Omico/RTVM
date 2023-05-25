package me.omico.build

import org.gradle.api.Project
import org.gradle.api.invocation.Gradle

val Project.rootGradle: Gradle
    get() = run {
        var rootGradle = gradle
        while (rootGradle.parent != null) {
            rootGradle = rootGradle.parent!!
        }
        rootGradle
    }
