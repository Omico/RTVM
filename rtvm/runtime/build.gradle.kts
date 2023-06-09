plugins {
    id("build-logic.android.library")
}

android {
    namespace = "me.omico.mvvmrt.runtime"
}

dependencies {
    api(project(":annotation"))
    api(project(":api"))
}

dependencies {
    compileOnly(kotlinx.coroutines.core)
}
