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
import org.gradle.api.tasks.TaskAction

internal abstract class WebjarInstallTask : NpmTask() {
    init {
        group = WEBJAR_TASK_GROUP
        args.set(listOf("install"))
        setupCache()
    }

    private fun setupCache() {
        inputs.files("package.json", "package-lock.json")
        outputs.dir("node_modules")
    }

    @TaskAction
    fun run() {
        exec()
        writeVersionFiles(project)
    }

    private fun writeVersionFiles(project: Project) {
        val node = project.extensions.findByType(NodeExtension::class.java)
        nodeVersionFile(project).write(node?.version?.orNull)
        npmVersionFile(project).write(node?.npmVersion?.orNull)
    }

    companion object {
        fun install(project: Project) {
            project.tasks
                .register(WEBJAR_INSTALL_TASK, WebjarInstallTask::class.java)
                .configure {
                    it.dependsOn(WEBJAR_INIT_TASK)
                    it.dependsOn(WEBJAR_REMOVE_MODULES_TASK)
                }
        }
    }
}
