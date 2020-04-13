package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_LINT_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.shared.ProjectFiles.filterExistingDirs
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
            filterExistingDirs(project, webjar.srcDir).forEach {
                task.inputs.dir(it)
            }
            task.inputs.files(".eslintrc", ".eslintignore", ".tslint", ".tslintignore")
            task.outputs.file(project.buildDir.resolve(webjar.lintTimestampFile))
            task.setArgs(listOf("run", webjar.lintTaskName))
            task.doLast { createTimeMarkerFile(project, webjar.lintTimestampFile) }
        }
        if (!WebjarSkipCondition.isWebjarSkipped(project)) {
            project.tasks.named(CHECK_TASK_NAME).configure {
                it.dependsOn(lintTask)
            }
        }
    }
}