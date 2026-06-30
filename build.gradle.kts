// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.ktlint) apply false
}

tasks.register<Copy>("installGitHook") {
    val sourceHook = rootProject.layout.projectDirectory.file("scripts/pre-push")
    val targetDir = rootProject.layout.projectDirectory.dir("../.git/hooks")

    from(sourceHook)
    into(targetDir)

    doLast {
        val hookFile = targetDir.file("pre-push").asFile
        if (hookFile.exists()) {
            hookFile.setExecutable(true, false)
            println("✅ Git hook installed successfully at: ${hookFile.path}")
        } else {
            println("❌ Failed to find installed hook at: ${hookFile.path}")
        }
    }
}

gradle.projectsEvaluated {
    tasks.matching { it.name == "prepareKotlinBuildScriptModel" }.configureEach {
        dependsOn("installGitHook")
    }
}