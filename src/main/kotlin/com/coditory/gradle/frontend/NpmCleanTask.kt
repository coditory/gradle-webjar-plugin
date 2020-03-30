package com.coditory.gradle.frontend

import com.coditory.gradle.frontend.FrontendPlugin.Companion.NPM_CLEAN_TASK
import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin.BUILD_GROUP
import org.gradle.language.base.plugins.LifecycleBasePlugin.CLEAN_TASK_NAME

internal object NpmCleanTask {
    @Suppress("UnstableApiUsage")
    fun install(project: Project) {
        val cleanTask = project.tasks.create(NPM_CLEAN_TASK, NpmTask::class.java) {
            it.group = BUILD_GROUP
            it.setArgs(listOf("run", "clean"))
        }
        project.tasks.named(CLEAN_TASK_NAME).configure {
            it.dependsOn(cleanTask)
        }
    }
}
