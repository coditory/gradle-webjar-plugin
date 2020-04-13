package com.coditory.gradle.webjar.base

import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

fun Project.executeNpmTask(name: String) {
    val task = this.tasks.named(name, NpmTask::class.java).get()
    if (task.actions.size != 1) {
        throw IllegalArgumentException("Expected single action in task: $name")
    }
    task.exec()
}

fun Project.getNpmTaskProvider(name: String): TaskProvider<NpmTask> {
    return this.tasks
        .named(name, NpmTask::class.java)
}

fun Project.getNpmTask(name: String): NpmTask {
    return getNpmTaskProvider(name).get()
}

fun Project.getTask(name: String): Task {
    return this.tasks
        .named(name)
        .get()
}

fun Project.writeFile(path: String, content: String): Project {
    val resolved = this.projectDir.resolve(path)
    resolved.parentFile.mkdirs()
    resolved.writeText(content)
    return this
}

fun Project.readFile(path: String): String {
    return this.projectDir.resolve(path)
        .readText()
}

fun Project.readBuildFile(path: String): String {
    return this.buildDir.resolve(path)
        .readText()
}
