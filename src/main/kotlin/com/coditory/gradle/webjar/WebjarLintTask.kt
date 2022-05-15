package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_LINT_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.WebjarSkipCondition.isWebjarSkipped
import com.coditory.gradle.webjar.shared.TimeMarkers.createTimeMarkerFile
import com.github.gradle.node.npm.task.NpmTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME
import javax.inject.Inject

internal abstract class WebjarLintTask @Inject constructor(
    private val webjar: WebjarExtension
) : NpmTask() {
    private val cacheEnabled = webjar.cache.enabled && webjar.cache.cacheLint

    init {
        group = WEBJAR_TASK_GROUP
        args.set(listOf("run", webjar.taskNames.lint))
        if (cacheEnabled) {
            setupCache()
        }
    }

    private fun setupCache() {
        webjar.cache.src
            .map { project.projectDir.resolve(it) }
            .forEach {
                if (it.isDirectory) {
                    inputs.dir(it)
                } else if (it.isFile) {
                    inputs.file(it)
                }
            }
        inputs.files(".eslintrc", ".eslintignore", "package.json")
        outputs.file(project.buildDir.resolve(webjar.cache.lintTimestampFile))
    }

    @TaskAction
    fun run() {
        exec()
        if (cacheEnabled) {
            createTimeMarkerFile(project, webjar.cache.lintTimestampFile)
        }
    }

    companion object {
        fun install(project: Project, webjar: WebjarExtension) {
            val lintTaskProvider = project.tasks.register(WEBJAR_LINT_TASK, WebjarLintTask::class.java, webjar)
            lintTaskProvider.configure { it.dependsOn(WEBJAR_INSTALL_TASK) }
            if (!isWebjarSkipped(project)) {
                project.tasks.named(CHECK_TASK_NAME).configure {
                    it.dependsOn(lintTaskProvider)
                }
            }
        }
    }
}
