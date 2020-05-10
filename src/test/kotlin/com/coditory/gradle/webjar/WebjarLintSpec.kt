package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_LINT_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.base.SpecProjectBuilder.Companion.project
import com.coditory.gradle.webjar.base.SpecProjectBuilder.Companion.projectWithPlugins
import com.coditory.gradle.webjar.base.getNpmTask
import com.coditory.gradle.webjar.base.getNpmTaskProvider
import com.coditory.gradle.webjar.base.getTask
import com.moowork.gradle.node.npm.NpmSetupTask
import org.assertj.core.api.Assertions.assertThat
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME
import org.junit.jupiter.api.Test

class WebjarLintSpec {
    private val project = projectWithPlugins()
        .withSamplePackageJson()
        .build()

    @Test
    fun `should configure webjarLint task`() {
        val lintTask = project.getNpmTask(WEBJAR_LINT_TASK)
        assertThat(lintTask.group).isEqualTo(WEBJAR_TASK_GROUP)
        assertThat(lintTask.args).isEqualTo(listOf("run", "lint"))
        assertThat(lintTask.outputs.files).contains(project.buildDir.resolve("lint/timestamp"))
        assertThat(lintTask.inputs.files).contains(
            project.projectDir.resolve(".eslintrc"),
            project.projectDir.resolve(".eslintignore"),
            project.projectDir.resolve(".tslint"),
            project.projectDir.resolve(".tslintignore")
        )
        assertThat(lintTask.dependsOn).contains(
            WEBJAR_INSTALL_TASK,
            NpmSetupTask.NAME
        )
    }

    @Test
    fun `should skip lint caching from configuration`() {
        project.extensions.configure(WebjarExtension::class.java) {
            it.cacheLint = false
        }
        val testTask = project.getNpmTask(WEBJAR_LINT_TASK)
        assertThat(testTask.outputs.files).isEmpty()
        assertThat(testTask.inputs.files).isEmpty()
    }

    @Test
    fun `should dynamically add additional files to task inputs`() {
        val project = project()
            .withFile("src/index.js")
            .withPlugins(WebjarPlugin::class)
            .build()
        val lintTask = project.getNpmTask(WEBJAR_LINT_TASK)
        assertThat(lintTask.inputs.files).contains(
            project.projectDir.resolve("src/index.js")
        )
    }

    @Test
    fun `should configure webjarLint task to run before check task`() {
        val lintTask = project.getNpmTaskProvider(WEBJAR_LINT_TASK)
        val checkTask = project.getTask(CHECK_TASK_NAME)
        assertThat(checkTask.dependsOn).contains(lintTask)
    }

    @Test
    fun `should not configure webjarLint task to run before clean task on --skipWebjar flag`() {
        val project = project()
            .withSkipWebjarFlag()
            .withPlugins(WebjarPlugin::class)
            .build()
        val lintTask = project.getNpmTaskProvider(WEBJAR_LINT_TASK)
        val checkTask = project.getTask(CHECK_TASK_NAME)
        assertThat(checkTask.dependsOn).doesNotContain(lintTask)
    }
}
