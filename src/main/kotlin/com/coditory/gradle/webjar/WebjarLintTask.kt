package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_LINT_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.shared.TimeMarkers.createTimeMarkerFile
import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME

internal object WebjarLintTask {
    @Suppress("UnstableApiUsage")
    fun install(project: Project, webjar: WebjarExtension) {
        val lintTask = project.tasks.register(WEBJAR_LINT_TASK, NpmTask::class.java) { task ->
            task.dependsOn(WEBJAR_INSTALL_TASK)
            task.group = WEBJAR_TASK_GROUP
            task.setArgs(listOf("run", webjar.taskNames.lint))
            if (webjar.cache.enabled && webjar.cache.cacheLint) {
                setupCache(task, project, webjar)
            }
        }
        if (!WebjarSkipCondition.isWebjarSkipped(project)) {
            project.tasks.named(CHECK_TASK_NAME).configure {
                it.dependsOn(lintTask)
            }
        }
    }

    private fun setupCache(task: NpmTask, project: Project, webjar: WebjarExtension) {
        webjar.cache.src
            .map { project.projectDir.resolve(it) }
            .forEach {
                if (it.isDirectory) {
                    task.inputs.dir(it)
                } else if (it.isFile) {
                    task.inputs.file(it)
                }
            }
        task.inputs.files(".eslintrc", ".eslintignore", "package.json")
        task.outputs.file(project.buildDir.resolve(webjar.cache.lintTimestamp))
        task.doLast { createTimeMarkerFile(project, webjar.cache.lintTimestamp) }
    }
}
