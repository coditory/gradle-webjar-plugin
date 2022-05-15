package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_CLEAN_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.WebjarSkipCondition.isWebjarSkipped
import com.github.gradle.node.npm.task.NpmTask
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin.CLEAN_TASK_NAME
import javax.inject.Inject

internal abstract class WebjarCleanTask @Inject constructor(
    webjar: WebjarExtension
) : NpmTask() {

    init {
        group = WEBJAR_TASK_GROUP
        args.set(listOf("run", webjar.taskNames.clean))
    }

    companion object {
        fun install(project: Project, webjar: WebjarExtension) {
            val taskProvider = project.tasks.register(WEBJAR_CLEAN_TASK, WebjarCleanTask::class.java, webjar)
            taskProvider.configure { it.dependsOn(WEBJAR_INSTALL_TASK) }
            if (!isWebjarSkipped(project)) {
                project.tasks.named(CLEAN_TASK_NAME).configure {
                    it.dependsOn(taskProvider)
                }
            }
        }
    }
}
