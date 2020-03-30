package com.coditory.gradle.frontend

import com.coditory.gradle.frontend.FrontendPlugin.Companion.NPM_INSTALL_TASK
import com.coditory.gradle.frontend.FrontendPlugin.Companion.NPM_LINT_TASK
import com.coditory.gradle.frontend.ProjectFiles.filterExistingDirs
import com.coditory.gradle.frontend.ProjectFiles.filterExistingFiles
import com.coditory.gradle.frontend.TimeMarkers.createTimeMarkerFile
import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin.VERIFICATION_GROUP
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME

internal object NpmLintTask {
    @Suppress("UnstableApiUsage")
    fun install(project: Project) {
        val lintTask = project.tasks.create(NPM_LINT_TASK, NpmTask::class.java) { task ->
            task.group = VERIFICATION_GROUP
            filterExistingDirs(project, "src").forEach {
                task.inputs.dir(it)
            }
            filterExistingFiles(project, ".eslintrc", ".eslintignore", ".tslint", ".tslintignore").forEach {
                task.inputs.files(it)
            }
            task.outputs.dir(project.buildDir.resolve("lint"))
            task.dependsOn(NPM_INSTALL_TASK)
            task.setArgs(listOf("run", "lint"))
            task.doLast { createTimeMarkerFile(project, "lint/timestamp") }

        }
        project.tasks.named(CHECK_TASK_NAME).configure {
            it.dependsOn(lintTask)
        }
    }
}
