import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.pro.application)
    alias(libs.plugins.pro.compose)
    alias(libs.plugins.pro.hilt)
    alias(libs.plugins.protobuf)
}

val baseVersionName = "0.1.4"
val isDevVersion get() = exec("git tag --contains HEAD").isEmpty()
val verNameSuffix get() = if (isDevVersion) ".dev" else ""

android {
    namespace = "dev.sanmer.sac"

    defaultConfig {
        applicationId = namespace
        versionName = "${baseVersionName}${verNameSuffix}.${commitId}"
        versionCode = commitCount

        resourceConfigurations += arrayOf("en", "zh-rCN")

        ndk {
            abiFilters += arrayOf("arm64-v8a", "x86_64")
        }
    }

    val releaseSigning = if (project.hasReleaseKeyStore) {
        signingConfigs.create("release") {
            storeFile = project.releaseKeyStore
            storePassword = project.releaseKeyStorePassword
            keyAlias = project.releaseKeyAlias
            keyPassword = project.releaseKeyPassword
            enableV2Signing = true
            enableV3Signing = true
        }
    } else {
        signingConfigs.getByName("debug")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        all {
            signingConfig = releaseSigning
            buildConfigField("Boolean", "IS_DEV_VERSION", isDevVersion.toString())
        }
    }

    buildFeatures {
        aidl = true
        buildConfig = true
    }

    packaging.resources.excludes += setOf(
        "META-INF/**",
        "okhttp3/**",
        "kotlin/**",
        "org/**",
        "**.properties",
        "**.bin",
        "**/*.proto"
    )

    dependenciesInfo.includeInApk = false

    applicationVariants.configureEach {
        outputs.configureEach {
            (this as? ApkVariantOutputImpl)?.outputFileName =
                "Sac-${versionName}-${versionCode}-${name}.apk"
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDir("src/main/libs")
        }
    }

    splits {
        abi {
            reset()
            include("arm64-v8a", "x86_64")
            isEnable = true
            isUniversalApk = true
        }
    }
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

// Workaround for https://github.com/google/ksp/issues/1590
androidComponents {
    onVariants(selector().all()) { variant ->
        afterEvaluate {
            @Suppress("DEPRECATION")
            val capName = variant.name.capitalize()
            tasks.getByName<KotlinCompile>("ksp${capName}Kotlin") {
                setSource(tasks.getByName("generate${capName}Proto").outputs)
            }
        }
    }
}

dependencies {
    implementation(projects.native)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.lifecycle.viewModel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.window.manager)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.protobuf.kotlin.lite)

    implementation(libs.skiko.android)
    implementation(libs.lets.plot)
    implementation(libs.lets.plot.kotlin)
    implementation(libs.lets.plot.compose)

    implementation(libs.timber)
}