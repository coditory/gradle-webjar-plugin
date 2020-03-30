package com.coditory.gradle.frontend

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

open class FrontendPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin(JavaPlugin::class.java)) {
            project. plugins.apply(JavaPlugin::class.java)
        }
        setupNodePlugin(project)
        setupNpmTasks(project)
    }

    private fun setupNodePlugin(project: Project) {
        project
            .convention.getPlugin(JavaPluginConvention::class.java)
            .sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            .resources.srcDir(project.buildDir.resolve("webjar"))
        project.plugins.apply(NodePlugin::class.java)
        project.extensions.configure(NodeExtension::class.java) {
            it.workDir = project.projectDir.resolve(".node/node")
            it.npmWorkDir = project.projectDir.resolve(".node/npm")
            it.yarnWorkDir = project.projectDir.resolve(".node/yarn")
            it.download = true
            it.version = "13.12.0"
            it.npmVersion = "6.14.4"
        }
    }

    private fun setupNpmTasks(project: Project) {
        NpmBuildTask.install(project)
        NpmCleanTask.install(project)
        NpmInstallTask.install(project)
        NpmLintTask.install(project)
        NpmRemoveModulesTask.install(project)
        NpmTestTask.install(project)
        NpmWatchTask.install(project)
    }

    companion object {
        const val PLUGIN_ID = "com.coditory.frontend"
        const val NPM_REMOVE_MODULES_TASK = "webjarRemoveModules"
        const val NPM_INSTALL_TASK = "webjarInstall"
        const val NPM_CLEAN_TASK = "webjarClean"
        const val NPM_LINT_TASK = "webjarLint"
        const val NPM_TEST_TASK = "webjarTest"
        const val NPM_WATCH_TASK = "webjarWatch"
        const val NPM_BUILD_TASK = "webjarBuild"
    }
}
