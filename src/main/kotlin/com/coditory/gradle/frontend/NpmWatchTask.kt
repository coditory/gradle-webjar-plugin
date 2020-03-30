package com.coditory.gradle.frontend

import com.coditory.gradle.frontend.FrontendPlugin.Companion.NPM_INSTALL_TASK
import com.coditory.gradle.frontend.FrontendPlugin.Companion.NPM_WATCH_TASK
import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_GROUP

internal object NpmWatchTask {
    @Suppress("UnstableApiUsage")
    fun install(project: Project) {
        project.tasks.create(NPM_WATCH_TASK, NpmTask::class.java) {
            it.group = BUILD_GROUP
            it.dependsOn(NPM_INSTALL_TASK)
            it.setArgs(listOf("run", "watch"))
        }
    }
}
