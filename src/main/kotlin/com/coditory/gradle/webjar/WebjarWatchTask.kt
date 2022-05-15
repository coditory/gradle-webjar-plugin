package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_WATCH_TASK
import com.github.gradle.node.npm.task.NpmTask
import org.gradle.api.Project
import javax.inject.Inject

internal abstract class WebjarWatchTask @Inject constructor(
    webjar: WebjarExtension
) : NpmTask() {
    init {
        group = WEBJAR_TASK_GROUP
        args.set(listOf("run", webjar.taskNames.watch))
    }

    companion object {
        fun install(project: Project, webjar: WebjarExtension) {
            project.tasks
                .register(WEBJAR_WATCH_TASK, WebjarWatchTask::class.java, webjar)
                .configure {
                    it.dependsOn(WEBJAR_INSTALL_TASK)
                }
        }
    }
}
