package com.coditory.gradle.webjar

import org.gradle.api.Project
import java.io.File

internal object VersionFiles {
    const val UNDEFINED_VERSION = "undefined"

    fun npmVersionFile(project: Project): VersionFile {
        return versionFile(project, "npm")
    }

    fun nodeVersionFile(project: Project): VersionFile {
        return versionFile(project, "node")
    }

    private fun file(project: Project, path: String): File {
        return project.projectDir.resolve(path)
    }

    private fun versionFile(project: Project, name: String): VersionFile {
        return VersionFile(file(project, "node_modules/.${name}Version"))
    }

    class VersionFile(private val target: File) {
        fun read(): String {
            return if (target.isFile) {
                target.readText()
            } else {
                UNDEFINED_VERSION
            }
        }

        fun write(content: String?) {
            target.createNewFile()
            val nonEmptyContent = if (content.isNullOrBlank()) UNDEFINED_VERSION else content
            target.writeText(nonEmptyContent)
        }
    }
}
