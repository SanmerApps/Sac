plugins {
    alias(libs.plugins.pro.library)
}

android {
    namespace = "dev.sanmer.sac.io"

    sourceSets {
        getByName("main") {
            jniLibs.srcDir("src/main/libs")
        }
    }
}

dependencies {}