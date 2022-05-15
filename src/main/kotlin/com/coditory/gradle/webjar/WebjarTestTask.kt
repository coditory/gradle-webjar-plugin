package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TEST_TASK
import com.coditory.gradle.webjar.shared.TimeMarkers.createTimeMarkerFile
import com.github.gradle.node.npm.task.NpmTask
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin.TEST_TASK_NAME
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

internal abstract class WebjarTestTask @Inject constructor(
    private val webjar: WebjarExtension
) : NpmTask() {
    init {
        group = WEBJAR_TASK_GROUP
        args.set(listOf("run", webjar.taskNames.test))
        if (webjar.cache.enabled && webjar.cache.cacheTest) {
            setupCache()
        }
    }

    private fun setupCache() {
        cacheInputs(webjar.cache.src, project)
        cacheInputs(webjar.cache.test, project)
        inputs.files(".babelrc", "jest.config.js", "cypress.json", "package.json", "package-lock.json")
        outputs.file(project.buildDir.resolve(webjar.cache.testTimestampFile))
    }

    @TaskAction
    fun run() {
        exec()
        createTimeMarkerFile(project, webjar.cache.testTimestampFile)
    }

    private fun cacheInputs(paths: List<String>, project: Project) {
        paths
            .map { project.projectDir.resolve(it) }
            .forEach {
                if (it.isDirectory) {
                    inputs.dir(it)
                } else if (it.isFile) {
                    inputs.file(it)
                }
            }
    }

    companion object {
        fun install(project: Project, webjar: WebjarExtension) {
            val taskProvider = project.tasks.register(WEBJAR_TEST_TASK, WebjarTestTask::class.java, webjar)
            taskProvider.configure { it.dependsOn(WEBJAR_INSTALL_TASK) }
            if (!WebjarSkipCondition.isWebjarSkipped(project)) {
                project.tasks.named(TEST_TASK_NAME).configure {
                    it.dependsOn(taskProvider)
                }
            }
        }
    }
}
