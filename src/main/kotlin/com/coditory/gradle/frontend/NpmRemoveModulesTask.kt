package com.coditory.gradle.frontend

import com.coditory.gradle.frontend.FrontendPlugin.Companion.NPM_REMOVE_MODULES_TASK
import com.coditory.gradle.frontend.VersionFiles.UNDEFINED_VERSION
import com.coditory.gradle.frontend.VersionFiles.nodeVersionFile
import com.coditory.gradle.frontend.VersionFiles.npmVersionFile
import com.moowork.gradle.node.NodeExtension
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.BasePlugin.BUILD_GROUP

internal object NpmRemoveModulesTask {
    fun install(project: Project) {
        project.tasks.create(NPM_REMOVE_MODULES_TASK) {
            it.group = BUILD_GROUP
            removeStaleNpmModules(it.project, it.logger)
        }
    }

    private fun removeStaleNpmModules(project: Project, logger: Logger) {
        val node = project.extensions.findByType(NodeExtension::class.java)
        val nodeVersion = normalizeVersion(node?.version)
        val npmVersion = normalizeVersion(node?.npmVersion)
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
}
