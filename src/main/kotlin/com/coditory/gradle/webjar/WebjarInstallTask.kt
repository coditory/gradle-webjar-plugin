package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INIT_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_REMOVE_MODULES_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.shared.VersionFiles.nodeVersionFile
import com.coditory.gradle.webjar.shared.VersionFiles.npmVersionFile
import com.github.gradle.node.NodeExtension
import com.github.gradle.node.npm.task.NpmTask
import org.gradle.api.Project

internal object WebjarInstallTask {
    fun install(project: Project) {
        project.tasks.register(WEBJAR_INSTALL_TASK, NpmTask::class.java) { task ->
            task.dependsOn(WEBJAR_INIT_TASK)
            task.dependsOn(WEBJAR_REMOVE_MODULES_TASK)
            task.group = WEBJAR_TASK_GROUP
            setupCaching(task)
            task.args.set(listOf("install"))
            task.doLast { writeVersionFiles(project) }
        }
    }

    private fun setupCaching(task: NpmTask) {
        task.inputs.files("package.json", "package-lock.json")
        task.outputs.dir("node_modules")
    }

    private fun writeVersionFiles(project: Project) {
        val node = project.extensions.findByType(NodeExtension::class.java)
        nodeVersionFile(project).write(node?.version?.orNull)
        npmVersionFile(project).write(node?.npmVersion?.orNull)
    }
}
