package com.coditory.gradle.frontend

import com.coditory.gradle.frontend.FrontendPlugin.Companion.NPM_INSTALL_TASK
import com.coditory.gradle.frontend.FrontendPlugin.Companion.NPM_REMOVE_MODULES_TASK
import com.coditory.gradle.frontend.ProjectFiles.filterExistingFiles
import com.coditory.gradle.frontend.VersionFiles.nodeVersionFile
import com.coditory.gradle.frontend.VersionFiles.npmVersionFile
import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.SetupTask
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin.BUILD_GROUP

internal object NpmInstallTask {
    fun install(project: Project) {
        project.tasks.create(NPM_INSTALL_TASK, NpmTask::class.java) { task ->
            task.group = BUILD_GROUP
            filterExistingFiles(project, "package.json", "package-lock.json").forEach {
                task.inputs.files(it)
            }
            task.outputs.dir("node_modules")
            task.setArgs(listOf("install"))
            task.doFirst { ensurePackageJson(project) }
            task.doLast { writeVersionFiles(project) }
            task.dependsOn(SetupTask.NAME)
            task.dependsOn(NPM_REMOVE_MODULES_TASK)
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
