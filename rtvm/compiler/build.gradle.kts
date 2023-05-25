plugins {
    id("build-logic.kotlin.jvm")
}

dependencies {
    implementation(project(":annotation"))
    implementation(project(":api"))
}

dependencies {
    implementation(elucidator)
    implementation(kotlinpoet)
    implementation(kotlinpoet.ksp)
    implementation(ksp.symbol.processing.api)
}
