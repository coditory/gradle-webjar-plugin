package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.ProjectFiles.filterExistingDirs
import com.coditory.gradle.webjar.ProjectFiles.filterExistingFiles
import com.coditory.gradle.webjar.TimeMarkers.createTimeMarkerFile
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TEST_TASK
import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin.TEST_TASK_NAME

object WebjarTestTask {
    fun install(project: Project) {
        val testTask = project.tasks.create(WEBJAR_TEST_TASK, NpmTask::class.java) { task ->
            task.dependsOn(WEBJAR_INSTALL_TASK)
            task.group = WEBJAR_TASK_GROUP
            filterExistingDirs(project, "src", "tests").forEach {
                task.inputs.dir(it)
            }
            filterExistingFiles(project, ".babelrc", "package.json", "package-lock.json").forEach {
                task.inputs.files(it)
            }
            task.outputs.dir(project.buildDir.resolve("test"))
            task.setArgs(listOf("run", "test"))
            task.doLast { createTimeMarkerFile(project, "test/timestamp") }
        }
        if (!WebjarSkipCondition.isWebjarSkipped(project)) {
            project.tasks.named(TEST_TASK_NAME).configure {
                it.dependsOn(testTask)
            }
        }
    }
}
