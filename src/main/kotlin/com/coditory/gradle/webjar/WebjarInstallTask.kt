package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INIT_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_REMOVE_MODULES_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.shared.VersionFiles.nodeVersionFile
import com.coditory.gradle.webjar.shared.VersionFiles.npmVersionFile
import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.Project

internal object WebjarInstallTask {
    fun install(project: Project) {
        project.tasks.register(WEBJAR_INSTALL_TASK, NpmTask::class.java) { task ->
            task.dependsOn(WEBJAR_INIT_TASK)
            task.dependsOn(WEBJAR_REMOVE_MODULES_TASK)
            task.group = WEBJAR_TASK_GROUP
            task.inputs.files("package.json", "package-lock.json")
            task.outputs.dir("node_modules")
            task.setArgs(listOf("install", "--no-package-lock", "--no-save"))
            task.doLast { writeVersionFiles(project) }
        }
    }

    private fun writeVersionFiles(project: Project) {
        val node = project.extensions.findByType(NodeExtension::class.java)
        nodeVersionFile(project).write(node?.version)
        npmVersionFile(project).write(node?.npmVersion)
    }
}
