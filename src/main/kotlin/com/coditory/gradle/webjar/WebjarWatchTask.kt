package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_WATCH_TASK
import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.Project

internal object WebjarWatchTask {
    @Suppress("UnstableApiUsage")
    fun install(project: Project, webjar: WebjarExtension) {
        project.tasks.register(WEBJAR_WATCH_TASK, NpmTask::class.java) { task ->
            task.dependsOn(WEBJAR_INSTALL_TASK)
            task.group = WEBJAR_TASK_GROUP
            task.setArgs(listOf("run", webjar.watchTaskName))
        }
    }
}
