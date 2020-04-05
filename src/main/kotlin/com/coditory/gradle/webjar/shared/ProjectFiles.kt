package com.coditory.gradle.webjar.shared

import org.gradle.api.Project

object ProjectFiles {
    fun filterExistingDirs(project: Project, vararg paths: String): List<String> {
        return paths
            .map { project.projectDir.resolve(it) }
            .filter { it.isDirectory }
            .map { it.absolutePath }
    }

    fun filterExistingFiles(project: Project, vararg paths: String): List<String> {
        return paths
            .map { project.projectDir.resolve(it) }
            .filter { it.isFile }
            .map { it.absolutePath }
    }
}
