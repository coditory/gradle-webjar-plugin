package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.shared.ProjectFiles.filterExistingDirs
import com.coditory.gradle.webjar.shared.ProjectFiles.filterExistingFiles
import com.coditory.gradle.webjar.shared.TimeMarkers.createTimeMarkerFile
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_LINT_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME

internal object WebjarLintTask {
    @Suppress("UnstableApiUsage")
    fun install(project: Project) {
        val lintTask = project.tasks.create(WEBJAR_LINT_TASK, NpmTask::class.java) { task ->
            task.dependsOn(WEBJAR_INSTALL_TASK)
            task.group = WEBJAR_TASK_GROUP
            filterExistingDirs(project, "src").forEach {
                task.inputs.dir(it)
            }
            task.inputs.files(".eslintrc", ".eslintignore", ".tslint", ".tslintignore")
            task.outputs.dir(project.buildDir.resolve("lint"))
            task.setArgs(listOf("run", "lint"))
            task.doLast { createTimeMarkerFile(project, "lint/timestamp") }
        }
        if (!WebjarSkipCondition.isWebjarSkipped(project)) {
            project.tasks.named(CHECK_TASK_NAME).configure {
                it.dependsOn(lintTask)
            }
        }
    }
}
