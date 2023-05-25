plugins {
    id("build-logic.root-project.base")
    id("build-logic.git.hooks")
    id("build-logic.spotless")
}

// Do not cache changing modules
allprojects {
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}

tasks {
    spotlessApply {
        dependsOn(gradle.includedBuild("example").task(":spotlessApply"))
        dependsOn(gradle.includedBuild("rtvm").task(":spotlessApply"))
    }
    spotlessCheck {
        dependsOn(gradle.includedBuild("example").task(":spotlessCheck"))
        dependsOn(gradle.includedBuild("rtvm").task(":spotlessCheck"))
    }
}
