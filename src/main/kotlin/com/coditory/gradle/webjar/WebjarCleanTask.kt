package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_CLEAN_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.WebjarSkipCondition.isWebjarSkipped
import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin.CLEAN_TASK_NAME

internal object WebjarCleanTask {
    @Suppress("UnstableApiUsage")
    fun install(project: Project) {
        val cleanTask = project.tasks.create(WEBJAR_CLEAN_TASK, NpmTask::class.java) { task ->
            task.dependsOn(WEBJAR_INSTALL_TASK)
            task.group = WEBJAR_TASK_GROUP
            task.setArgs(listOf("run", "clean"))
        }
        if (!isWebjarSkipped(project)) {
            project.tasks.named(CLEAN_TASK_NAME).configure {
                it.dependsOn(cleanTask)
            }
        }
    }
}
