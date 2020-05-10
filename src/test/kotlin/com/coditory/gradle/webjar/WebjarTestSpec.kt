package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TEST_TASK
import com.coditory.gradle.webjar.base.SpecProjectBuilder.Companion.project
import com.coditory.gradle.webjar.base.SpecProjectBuilder.Companion.projectWithPlugins
import com.coditory.gradle.webjar.base.getNpmTask
import com.coditory.gradle.webjar.base.getNpmTaskProvider
import com.coditory.gradle.webjar.base.getTask
import com.moowork.gradle.node.npm.NpmSetupTask
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.plugins.JavaPlugin
import org.junit.jupiter.api.Test

class WebjarTestSpec {
    private val project = projectWithPlugins()
        .withSamplePackageJson()
        .build()

    @Test
    fun `should configure webjarTest task`() {
        val testTask = project.getNpmTask(WEBJAR_TEST_TASK)
        assertThat(testTask.group).isEqualTo(WEBJAR_TASK_GROUP)
        assertThat(testTask.args).isEqualTo(listOf("run", "test"))
        assertThat(testTask.outputs.files).contains(project.buildDir.resolve("test/timestamp"))
        assertThat(testTask.inputs.files).contains(
            project.projectDir.resolve(".babelrc"),
            project.projectDir.resolve("package.json"),
            project.projectDir.resolve("package-lock.json")
        )
        assertThat(testTask.dependsOn).contains(
            WEBJAR_INSTALL_TASK,
            NpmSetupTask.NAME
        )
    }

    @Test
    fun `should skip test caching from configuration`() {
        project.extensions.configure(WebjarExtension::class.java) {
            it.cacheTest = false
        }
        val testTask = project.getNpmTask(WEBJAR_TEST_TASK)
        assertThat(testTask.outputs.files).isEmpty()
        assertThat(testTask.inputs.files).isEmpty()
    }

    @Test
    fun `should dynamically add additional files to task inputs`() {
        val inputFiles = listOf("src/index.js", "tests/index.js")
        val project = project()
            .withFiles(inputFiles)
            .withPlugins(WebjarPlugin::class)
            .build()
        val testTask = project.getNpmTask(WEBJAR_TEST_TASK)
        assertThat(testTask.inputs.files).containsAll(
            inputFiles.map { project.projectDir.resolve(it) }
        )
    }

    @Test
    fun `should configure webjarTest task to run before test task`() {
        val testTask = project.getNpmTaskProvider(WEBJAR_TEST_TASK)
        val javaTestTask = project.getTask(JavaPlugin.TEST_TASK_NAME)
        assertThat(javaTestTask.dependsOn).contains(testTask)
    }

    @Test
    fun `should not configure webjarTest task to run before clean task on --skipWebjar flag`() {
        val project = project()
            .withSkipWebjarFlag()
            .withPlugins(WebjarPlugin::class)
            .build()
        val testTask = project.getNpmTaskProvider(WEBJAR_TEST_TASK)
        val javaTestTask = project.getTask(JavaPlugin.TEST_TASK_NAME)
        assertThat(javaTestTask.dependsOn).doesNotContain(testTask)
    }
}
