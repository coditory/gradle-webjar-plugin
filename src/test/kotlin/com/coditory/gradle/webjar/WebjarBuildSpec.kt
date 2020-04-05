package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_BUILD_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.base.SpecProjectBuilder.Companion.project
import com.coditory.gradle.webjar.base.SpecProjectBuilder.Companion.projectWithPlugins
import com.coditory.gradle.webjar.base.getNpmTask
import com.coditory.gradle.webjar.base.getTask
import com.moowork.gradle.node.npm.NpmSetupTask
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.plugins.JavaPlugin.PROCESS_RESOURCES_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_TASK_NAME
import org.junit.jupiter.api.Test

class WebjarBuildSpec {
    private val project = projectWithPlugins()
        .withSamplePackageJson()
        .build()

    @Test
    fun `should configure webjarBuild task`() {
        val buildTask = project.getNpmTask(WEBJAR_BUILD_TASK)
        assertThat(buildTask.group).isEqualTo(WEBJAR_TASK_GROUP)
        assertThat(buildTask.args).isEqualTo(listOf("run", "build"))
        assertThat(buildTask.outputs.files).contains(project.buildDir.resolve("dist"))
        assertThat(buildTask.inputs.files).contains(
            project.projectDir.resolve(".babelrc"),
            project.projectDir.resolve("package.json"),
            project.projectDir.resolve("package-lock.json")
        )
        assertThat(buildTask.dependsOn).contains(
            WEBJAR_INSTALL_TASK,
            NpmSetupTask.NAME
        )
    }

    @Test
    fun `should dynamically add additional files to task inputs`() {
        val project = project()
            .withFile("src/index.js")
            .withPlugins(WebjarPlugin::class)
            .build()
        val buildTask = project.getNpmTask(WEBJAR_BUILD_TASK)
        assertThat(buildTask.inputs.files).contains(
            project.projectDir.resolve("src/index.js")
        )
    }

    @Test
    fun `should configure webjarBuild task to run before processResources task`() {
        val buildTask = project.getNpmTask(WEBJAR_BUILD_TASK)
        val processResourcesTask = project.getTask(PROCESS_RESOURCES_TASK_NAME)
        assertThat(processResourcesTask.dependsOn).contains(buildTask)
    }

    @Test
    fun `should not configure webjarBuild task to run before clean task on --skipWebjar flag`() {
        val project = project()
            .withSkipWebjarFlag()
            .withPlugins(WebjarPlugin::class)
            .build()
        val buildTask = project.getNpmTask(WEBJAR_BUILD_TASK)
        val javaBuildTask = project.getTask(BUILD_TASK_NAME)
        assertThat(javaBuildTask.dependsOn).doesNotContain(buildTask)
    }
}
