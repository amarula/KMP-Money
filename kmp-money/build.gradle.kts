import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    jvm()
    android {
        namespace = "com.amarula.kmpMoney"
        compileSdk = libs.versions.android.sdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true

        withJava()
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(libs.versions.jvm.get())
        }
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.runtime)
            implementation(libs.compose.components.resources)
            implementation(libs.big.num)
            implementation(libs.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

compose.resources {
    packageOfResClass = "com.amarula.kmpMoney.resources"
    publicResClass = true
    generateResClass = always
}
