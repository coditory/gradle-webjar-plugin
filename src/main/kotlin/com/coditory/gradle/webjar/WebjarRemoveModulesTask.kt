package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_REMOVE_MODULES_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.shared.VersionFiles.UNDEFINED_VERSION
import com.coditory.gradle.webjar.shared.VersionFiles.nodeVersionFile
import com.coditory.gradle.webjar.shared.VersionFiles.npmVersionFile
import com.github.gradle.node.NodeExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

internal abstract class WebjarRemoveModulesTask : DefaultTask() {
    init {
        group = WEBJAR_TASK_GROUP
    }

    @TaskAction
    fun removeStaleNpmModules() {
        val node = project.extensions.findByType(NodeExtension::class.java)
        val nodeVersion = normalizeVersion(node?.version?.orNull)
        val npmVersion = normalizeVersion(node?.npmVersion?.orNull)
        val lastNodeVersion = nodeVersionFile(project).read()
        val lastNpmVersion = npmVersionFile(project).read()
        val versionChanged = (lastNodeVersion != nodeVersion || lastNpmVersion != npmVersion)
        if (versionChanged) {
            logger.warn("Node/NPM version changed - removing node_modules in order to rebuild dependencies")
            logger.warn("  Node detected: $lastNodeVersion, expected: $nodeVersion")
            logger.warn("  NPM  detected: $lastNpmVersion, expected: $npmVersion")
            removeNpmModules(project)
        }
    }

    private fun normalizeVersion(value: String?): String {
        return if (value.isNullOrBlank()) {
            UNDEFINED_VERSION
        } else {
            value
        }
    }

    private fun removeNpmModules(project: Project) {
        val nodeModulesDir = project.projectDir.resolve("node_modules")
        nodeModulesDir.deleteRecursively()
        nodeModulesDir.mkdirs()
    }

    companion object {
        fun install(project: Project) {
            project.tasks.register(WEBJAR_REMOVE_MODULES_TASK, WebjarRemoveModulesTask::class.java)
        }
    }
}
