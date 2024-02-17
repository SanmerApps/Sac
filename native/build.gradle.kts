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

val compileRust = task<Exec>("compileRust") {
    commandLine(listOf("cargo", "build", "--all-targets", "--release"))
    workingDir(file("jni"))
}

task<Copy>("mergeReleaseJniLib") {
    dependsOn(compileRust)
    destinationDir = file("src/main/libs")

    listOf(
        "aarch64-linux-android" to "arm64-v8a",
        "x86_64-linux-android" to "x86_64"
    ).forEach { (raw, target) ->
        from(file("jni/target/${raw}/release")) {
            include("libsac_jni.so")
            rename("libsac_jni.so", "${target}/libsac-jni.so")
        }
    }
}