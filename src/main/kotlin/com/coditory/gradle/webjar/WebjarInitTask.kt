package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INIT_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_REMOVE_MODULES_TASK
import com.coditory.gradle.webjar.shared.SemVersion
import com.github.gradle.node.NodeExtension
import com.github.gradle.node.npm.task.NpmSetupTask
import com.github.gradle.node.npm.task.NpmTask
import org.gradle.api.Project

object WebjarInitTask {
    fun install(project: Project) {
        project.tasks.register(WEBJAR_INIT_TASK, NpmTask::class.java) { task ->
            task.dependsOn(NpmSetupTask.NAME)
            task.dependsOn(WEBJAR_REMOVE_MODULES_TASK)
            task.group = WebjarPlugin.WEBJAR_TASK_GROUP
            task.args.set(resolveNpmArguments(project))
            task.doFirst { ensurePackageJson(project) }
            setupCaching(task)
        }
    }

    private fun setupCaching(task: NpmTask) {
        task.inputs.files("package.json")
        task.outputs.file("package-lock.json")
    }

    private fun resolveNpmArguments(project: Project): List<String> {
        val node = project.extensions.findByType(NodeExtension::class.java)
        val npmSemVersion = SemVersion.parseOrNull(node?.npmVersion?.orNull)
        return if (npmSemVersion == null || npmSemVersion >= SemVersion.parse("6.0.0")) {
            listOf("install", "--package-lock-only")
        } else {
            listOf("install")
        }
    }

    private fun ensurePackageJson(project: Project) {
        val packageJsonFile = project.projectDir.resolve("package.json")
        if (!packageJsonFile.isFile) {
            packageJsonFile.createNewFile()
            packageJsonFile.writeText(
                """
                {
                    "name": "${project.name}",
                    "scripts": {
                        "lint": "echo 'missing lint script'",
                        "test": "echo 'missing test script'",
                        "clean": "echo 'missing clean script'",
                        "build": "echo 'missing build script'",
                        "watch": "echo 'missing watch script'"
                    }
                }
                """.trimIndent()
            )
        }
    }
}
