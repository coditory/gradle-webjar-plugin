package com.coditory.gradle.frontend

import com.coditory.gradle.frontend.FrontendPlugin.Companion.NPM_INSTALL_TASK
import com.coditory.gradle.frontend.FrontendPlugin.Companion.NPM_TEST_TASK
import com.coditory.gradle.frontend.ProjectFiles.filterExistingDirs
import com.coditory.gradle.frontend.ProjectFiles.filterExistingFiles
import com.coditory.gradle.frontend.TimeMarkers.createTimeMarkerFile
import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin.VERIFICATION_GROUP
import org.gradle.api.plugins.JavaPlugin.TEST_TASK_NAME

object NpmTestTask {
    fun install(project: Project) {
        val testTask = project.tasks.create(NPM_TEST_TASK, NpmTask::class.java) { task ->
            task.group = VERIFICATION_GROUP
            filterExistingDirs(project, "src", "tests").forEach {
                task.inputs.dir(it)
            }
            filterExistingFiles(project, ".babelrc", "package.json", "package-lock.json").forEach {
                task.inputs.files(it)
            }
            task.outputs.dir(project.buildDir.resolve("test"))
            task.dependsOn(NPM_INSTALL_TASK)
            task.setArgs(listOf("run", "test"))
            task.doLast { createTimeMarkerFile(project, "test/timestamp") }
        }
        project.tasks.named(TEST_TASK_NAME).configure {
            it.dependsOn(testTask)
        }
    }
}
