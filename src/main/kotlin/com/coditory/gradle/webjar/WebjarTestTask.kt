package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TEST_TASK
import com.coditory.gradle.webjar.shared.TimeMarkers.createTimeMarkerFile
import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin.TEST_TASK_NAME

object WebjarTestTask {
    fun install(project: Project, webjar: WebjarExtension) {
        val testTask = project.tasks.register(WEBJAR_TEST_TASK, NpmTask::class.java) { task ->
            task.dependsOn(WEBJAR_INSTALL_TASK)
            task.group = WEBJAR_TASK_GROUP
            if (webjar.cache.enabled && webjar.cache.cacheTest) {
                setupCache(task, project, webjar)
            }
            task.setArgs(listOf("run", webjar.taskNames.test))
        }
        if (!WebjarSkipCondition.isWebjarSkipped(project)) {
            project.tasks.named(TEST_TASK_NAME).configure {
                it.dependsOn(testTask)
            }
        }
    }

    private fun setupCache(task: NpmTask, project: Project, webjar: WebjarExtension) {
        cacheInputs(webjar.cache.src, project, task)
        cacheInputs(webjar.cache.test, project, task)
        task.inputs.files(".babelrc", "jest.config.js", "cypress.json", "package.json", "package-lock.json")
        task.outputs.file(project.buildDir.resolve(webjar.cache.testTimestamp))
        task.doLast { createTimeMarkerFile(project, webjar.cache.testTimestamp) }
    }

    private fun cacheInputs(paths: List<String>, project: Project, task: NpmTask) {
        paths
            .map { project.projectDir.resolve(it) }
            .forEach {
                if (it.isDirectory) {
                    task.inputs.dir(it)
                } else if (it.isFile) {
                    task.inputs.file(it)
                }
            }
    }
}
