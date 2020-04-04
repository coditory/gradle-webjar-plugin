package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.ProjectFiles.filterExistingFiles
import com.coditory.gradle.webjar.VersionFiles.nodeVersionFile
import com.coditory.gradle.webjar.VersionFiles.npmVersionFile
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_REMOVE_MODULES_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.SetupTask
import org.gradle.api.Project

internal object WebjarInstallTask {
    fun install(project: Project) {
        project.tasks.create(WEBJAR_INSTALL_TASK, NpmTask::class.java) { task ->
            task.dependsOn(SetupTask.NAME)
            task.dependsOn(WEBJAR_REMOVE_MODULES_TASK)
            task.group = WEBJAR_TASK_GROUP
            filterExistingFiles(project, "package.json", "package-lock.json").forEach {
                task.inputs.files(it)
            }
            task.outputs.dir("node_modules")
            task.setArgs(listOf("install"))
            task.doFirst { ensurePackageJson(project) }
            task.doLast { writeVersionFiles(project) }
        }
    }

    private fun writeVersionFiles(project: Project) {
        val node = project.extensions.findByType(NodeExtension::class.java)
        nodeVersionFile(project).write(node?.version)
        npmVersionFile(project).write(node?.npmVersion)
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
