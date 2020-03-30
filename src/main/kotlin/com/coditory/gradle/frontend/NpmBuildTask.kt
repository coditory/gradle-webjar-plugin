package com.coditory.gradle.frontend

import com.coditory.gradle.frontend.FrontendPlugin.Companion.NPM_BUILD_TASK
import com.coditory.gradle.frontend.FrontendPlugin.Companion.NPM_INSTALL_TASK
import com.coditory.gradle.frontend.ProjectFiles.filterExistingDirs
import com.coditory.gradle.frontend.ProjectFiles.filterExistingFiles
import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin.BUILD_GROUP
import org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_TASK_NAME

internal object NpmBuildTask {
    fun install(project: Project) {
        val buildTask = project.tasks.create(NPM_BUILD_TASK, NpmTask::class.java) { task ->
            task.group = BUILD_GROUP
            filterExistingDirs(project, "src").forEach {
                task.inputs.dir(it)
            }
            filterExistingFiles(project, ".babelrc", "package.json", "package-lock.json").forEach {
                task.inputs.files(it)
            }
            task.outputs.dir(project.buildDir.resolve("webjar/static"))
            task.dependsOn(NPM_INSTALL_TASK)
            task.setArgs(listOf("run", "build"))
            task.doLast { copyToJarOutput(project) }
        }
        project.tasks.named(BUILD_TASK_NAME).configure {
            it.dependsOn(buildTask)
        }
    }

    private fun copyToJarOutput(project: Project) {
        val from = project.projectDir.resolve("dist")
        val to = project.buildDir.resolve("webjar/static")
        to.mkdirs()
        from.copyRecursively(to, true)
    }
}
