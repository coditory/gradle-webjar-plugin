package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_BUILD_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.github.gradle.node.npm.task.NpmTask
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin.PROCESS_RESOURCES_TASK_NAME

internal object WebjarBuildTask {
    fun install(project: Project, webjar: WebjarExtension) {
        val buildTask = project.tasks.register(WEBJAR_BUILD_TASK, NpmTask::class.java) { task ->
            task.group = WEBJAR_TASK_GROUP
            task.dependsOn(WEBJAR_INSTALL_TASK)
            task.args.set(listOf("run", webjar.taskNames.build))
            task.doLast { copyToJarOutput(project, webjar) }
            if (webjar.cache.enabled) {
                setupCaching(project, webjar, task)
            }
        }
        if (!WebjarSkipCondition.isWebjarSkipped(project)) {
            project.tasks.named(PROCESS_RESOURCES_TASK_NAME).configure {
                it.dependsOn(buildTask)
            }
        }
    }

    private fun setupCaching(project: Project, webjar: WebjarExtension, task: NpmTask) {
        webjar.cache.src
            .map { project.projectDir.resolve(it) }
            .forEach {
                if (it.isDirectory) {
                    task.inputs.dir(it)
                } else if (it.isFile) {
                    task.inputs.file(it)
                }
            }
        task.inputs.files("package.json", "package-lock.json", ".babelrc", ".tsconfig.json")
        task.outputs.dir(webjar.distDir)
    }

    private fun copyToJarOutput(project: Project, webjar: WebjarExtension) {
        val from = project.projectDir.resolve(webjar.distDir)
        if (from.isDirectory) {
            val to = project.buildDir
                .resolve(webjar.outputDir)
                .resolve(webjar.webjarDir)
            to.mkdirs()
            from.copyRecursively(to, true)
        }
    }
}
