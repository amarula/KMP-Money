import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kover)
    alias(libs.plugins.detekt)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.maven.publish)
}

group = "com.github.amarula"
version = "0.1.0"

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
            implementation(libs.kotlinx.serialization.json)
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

detekt {
    buildUponDefaultConfig = true
    parallel = true

    config.setFrom(rootProject.files("config/detekt.yml"))

    source.setFrom(
        files("src/commonMain/")
    )
}

kover {
    reports {
        filters {
            excludes {
                packages("com.amarula.kmpMoney.resources")
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = false)
    signAllPublications()

    coordinates(group.toString(), "kmp-money", version.toString())

    pom {
        name.set("KMP Money")
        description.set(
            "A Kotlin Multiplatform library for working with monetary amounts safely and precisely."
        )
        inceptionYear.set("2026")
        url.set("https://github.com/amarula/KMP-Money")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("amarula")
                name.set("Amarula Solutions")
                url.set("https://github.com/amarula")
            }
        }

        scm {
            url.set("https://github.com/amarula/KMP-Money")
            connection.set("scm:git:git://github.com/amarula/KMP-Money.git")
            developerConnection.set("scm:git:ssh://git@github.com/amarula/KMP-Money.git")
        }
    }
}
