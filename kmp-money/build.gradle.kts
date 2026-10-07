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
    `maven-publish`
    signing
}

group = "com.amarulasolutions"
version = (findProperty("releaseVersion") as String?) ?: "0.1.0"

kotlin {
    withSourcesJar()

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

// Maven Central requires a javadoc artifact on every publication; KMP has no single
// "javadoc" so ship an empty jar, same as most Kotlin-only multiplatform libraries do.
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier = "javadoc"
}

publishing {
    repositories {
        maven {
            name = "centralStaging"
            url = uri(layout.buildDirectory.dir("central-staging"))
        }
    }

    publications.withType<MavenPublication>().configureEach {
        artifact(javadocJar)

        pom {
            name = "KMP Money"
            description =
                "A Kotlin Multiplatform library for working with monetary amounts safely and precisely."
            url = "https://github.com/amarula/KMP-Money"

            licenses {
                license {
                    name = "Apache-2.0"
                    url = "https://www.apache.org/licenses/LICENSE-2.0"
                }
            }

            developers {
                developer {
                    id = "amarula"
                    name = "Amarula"
                }
            }

            scm {
                url = "https://github.com/amarula/KMP-Money"
                connection = "scm:git:https://github.com/amarula/KMP-Money.git"
                developerConnection = "scm:git:https://github.com/amarula/KMP-Money.git"
            }
        }
    }
}

signing {
    val signingKey = findProperty("signingInMemoryKey") as String?
    val signingPassword = findProperty("signingInMemoryKeyPassword") as String?
    isRequired = signingKey != null

    if (signingKey != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}

// Bundles the staged, signed artifacts into the zip layout the Central Portal
// Publisher API expects (https://central.sonatype.org/publish/publish-portal-api/).
val zipCentralBundle by tasks.registering(Zip::class) {
    dependsOn("publishAllPublicationsToCentralStagingRepository")
    from(layout.buildDirectory.dir("central-staging"))
    archiveFileName = "central-bundle.zip"
    destinationDirectory = layout.buildDirectory.dir("central-bundle")
}
