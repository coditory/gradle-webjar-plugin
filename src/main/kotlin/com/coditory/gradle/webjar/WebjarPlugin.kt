package com.coditory.gradle.webjar

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

open class WebjarPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin(JavaPlugin::class.java)) {
            project.plugins.apply(JavaPlugin::class.java)
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
        WebjarBuildTask.install(project)
        WebjarCleanTask.install(project)
        WebjarInstallTask.install(project)
        WebjarLintTask.install(project)
        WebjarRemoveModulesTask.install(project)
        WebjarTestTask.install(project)
        WebjarWatchTask.install(project)
    }

    companion object {
        const val PLUGIN_ID = "com.coditory.webjar"
        const val WEBJAR_TASK_GROUP = "webjar"
        const val WEBJAR_REMOVE_MODULES_TASK = "webjarRemoveModules"
        const val WEBJAR_INSTALL_TASK = "webjarInstall"
        const val WEBJAR_CLEAN_TASK = "webjarClean"
        const val WEBJAR_LINT_TASK = "webjarLint"
        const val WEBJAR_TEST_TASK = "webjarTest"
        const val WEBJAR_WATCH_TASK = "webjarWatch"
        const val WEBJAR_BUILD_TASK = "webjarBuild"
    }
}
