package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_CLEAN_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.WebjarSkipCondition.isWebjarSkipped
import com.github.gradle.node.npm.task.NpmTask
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin.CLEAN_TASK_NAME

internal object WebjarCleanTask {
    @Suppress("UnstableApiUsage")
    fun install(project: Project, webjar: WebjarExtension) {
        val cleanTask = project.tasks.register(WEBJAR_CLEAN_TASK, NpmTask::class.java) { task ->
            task.dependsOn(WEBJAR_INSTALL_TASK)
            task.group = WEBJAR_TASK_GROUP
            task.args.set(listOf("run", webjar.taskNames.clean))
        }
        if (!isWebjarSkipped(project)) {
            project.tasks.named(CLEAN_TASK_NAME).configure {
                it.dependsOn(cleanTask)
            }
        }
    }
}
