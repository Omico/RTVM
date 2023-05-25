import me.omico.age.spotless.configureSpotless
import me.omico.age.spotless.intelliJIDEARunConfiguration
import me.omico.age.spotless.kotlin
import me.omico.age.spotless.kotlinGradle
import me.omico.build.rootGradle

plugins {
    id("build-logic.root-project.base")
    id("com.diffplug.spotless")
    id("me.omico.age.spotless")
}

repositories {
    mavenCentral()
}

val spotlessKotlinCopyright: File = rootGradle.rootProject.file("spotless/copyright.kt")

allprojects {
    configureSpotless {
        freshmark {
            target("**/*.md")
            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }
        intelliJIDEARunConfiguration()
        kotlin(
            licenseHeaderFile = spotlessKotlinCopyright,
            licenseHeaderConfig = {
                updateYearWithLatest(true)
                yearSeparator("-")
            },
        )
        kotlinGradle()
    }
}

subprojects {
    rootProject.tasks {
        spotlessApply { dependsOn(this@subprojects.tasks.spotlessApply) }
        spotlessCheck { dependsOn(this@subprojects.tasks.spotlessCheck) }
    }
}
