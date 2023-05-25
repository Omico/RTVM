@file:Suppress("UnstableApiUsage")

plugins {
    id("build-logic.android.application")
    id("com.google.devtools.ksp")
}

android {
    namespace = "me.omico.mvvmrt.example"
    defaultConfig {
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    ksp(rtvm.compiler)
    implementation(rtvm.runtime)
}

dependencies {
    implementation(platform(androidx.compose.bom))
    implementation(androidx.activity.compose)
    implementation(androidx.compose.material3)
    implementation(androidx.compose.ui)
    implementation(androidx.compose.ui.graphics)
    implementation(androidx.compose.ui.tooling.preview)
    implementation(androidx.core.ktx)
    implementation(androidx.lifecycle.viewmodel.compose)
    debugImplementation(androidx.compose.ui.test.manifest)
    debugImplementation(androidx.compose.ui.tooling)
}
