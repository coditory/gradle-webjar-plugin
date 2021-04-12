package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_BUILD_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.base.SystemProperties.withSystemProperty
import com.coditory.gradle.webjar.base.TestProjectBuilder.Companion.project
import com.coditory.gradle.webjar.base.TestProjectBuilder.Companion.projectWithPlugins
import com.coditory.gradle.webjar.base.getNpmTask
import com.coditory.gradle.webjar.base.getNpmTaskProvider
import com.coditory.gradle.webjar.base.getTask
import com.github.gradle.node.npm.task.NpmSetupTask
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.plugins.JavaPlugin.PROCESS_RESOURCES_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_TASK_NAME
import org.junit.jupiter.api.Test

class WebjarBuildTest {
    private val project = projectWithPlugins()
        .withSamplePackageJson()
        .build()

    @Test
    fun `should configure webjarBuild task`() {
        val buildTask = project.getNpmTask(WEBJAR_BUILD_TASK)
        assertThat(buildTask.group).isEqualTo(WEBJAR_TASK_GROUP)
        assertThat(buildTask.args.get()).isEqualTo(listOf("run", "build"))
        assertThat(buildTask.outputs.files).contains(project.projectDir.resolve("dist"))
        assertThat(buildTask.inputs.files).contains(
            project.projectDir.resolve(".babelrc"),
            project.projectDir.resolve(".tsconfig.json"),
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
        val buildTask = project.getNpmTaskProvider(WEBJAR_BUILD_TASK)
        val processResourcesTask = project.getTask(PROCESS_RESOURCES_TASK_NAME)
        assertThat(processResourcesTask.dependsOn).contains(buildTask)
    }

    @Test
    fun `should not configure webjarBuild task to run before clean task on --skipWebjar flag`() {
        val project = project()
            .withSkipWebjarFlag()
            .withPlugins(WebjarPlugin::class)
            .build()
        val buildTask = project.getNpmTaskProvider(WEBJAR_BUILD_TASK)
        val javaBuildTask = project.getTask(BUILD_TASK_NAME)
        assertThat(javaBuildTask.dependsOn).doesNotContain(buildTask)
    }

    @Test
    fun `should not configure webjarBuild task to run before clean task on SKIP_WEBJAR env`() {
        val project = withSystemProperty("SKIP_WEBJAR", "true") {
            project()
                .withPlugins(WebjarPlugin::class)
                .build()
        }
        val buildTask = project.getNpmTaskProvider(WEBJAR_BUILD_TASK)
        val javaBuildTask = project.getTask(BUILD_TASK_NAME)
        assertThat(javaBuildTask.dependsOn).doesNotContain(buildTask)
    }

    @Test
    fun `should use build npm task name from extension`() {
        val project = project()
            .withPlugins(WebjarPlugin::class)
            .build()
        project.extensions.configure(WebjarExtension::class.java) {
            it.taskNames.build = "build2"
        }
        val buildTask = project.getNpmTask(WEBJAR_BUILD_TASK)
        assertThat(buildTask.args.get()).isEqualTo(listOf("run", "build2"))
    }
}
