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
        val webjar = setupWebjarExtension(project)
        setupNodePlugin(project, webjar)
        setupWebjarTasks(project, webjar)
    }

    private fun setupNodePlugin(project: Project, webjar: WebjarExtension) {
        project
            .convention.getPlugin(JavaPluginConvention::class.java)
            .sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            .resources.srcDir(project.buildDir.resolve(webjar.outputDir))
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

    private fun setupWebjarExtension(project: Project): WebjarExtension {
        return project.extensions.create(WEBJAR_EXTENSION, WebjarExtension::class.java)
    }

    private fun setupWebjarTasks(project: Project, webjar: WebjarExtension) {
        WebjarInitTask.install(project)
        WebjarBuildTask.install(project, webjar)
        WebjarCleanTask.install(project, webjar)
        WebjarInstallTask.install(project)
        WebjarLintTask.install(project, webjar)
        WebjarRemoveModulesTask.install(project)
        WebjarTestTask.install(project, webjar)
        WebjarWatchTask.install(project, webjar)
    }

    companion object {
        const val PLUGIN_ID = "com.coditory.webjar"
        const val WEBJAR_TASK_GROUP = "webjar"
        const val WEBJAR_EXTENSION = "webjar"
        const val WEBJAR_REMOVE_MODULES_TASK = "webjarRemoveModules"
        const val WEBJAR_INIT_TASK = "webjarInit"
        const val WEBJAR_INSTALL_TASK = "webjarInstall"
        const val WEBJAR_CLEAN_TASK = "webjarClean"
        const val WEBJAR_LINT_TASK = "webjarLint"
        const val WEBJAR_TEST_TASK = "webjarTest"
        const val WEBJAR_WATCH_TASK = "webjarWatch"
        const val WEBJAR_BUILD_TASK = "webjarBuild"
    }
}
