package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_REMOVE_MODULES_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.base.SpecProjectBuilder.Companion.project
import com.coditory.gradle.webjar.base.SpecProjectBuilder.Companion.projectWithPlugins
import com.coditory.gradle.webjar.base.getNpmTask
import com.moowork.gradle.node.npm.NpmSetupTask
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WebjarInstallSpec {
    private val project = projectWithPlugins()
        .withSamplePackageJson()
        .build()

    @Test
    fun `should configure webjarInstall task`() {
        val installTask = project.getNpmTask(WEBJAR_INSTALL_TASK)
        assertThat(installTask.group).isEqualTo(WEBJAR_TASK_GROUP)
        assertThat(installTask.args).isEqualTo(listOf("install"))
        assertThat(installTask.outputs.files).contains(project.projectDir.resolve("node_modules"))
        assertThat(installTask.dependsOn).contains(
            WEBJAR_REMOVE_MODULES_TASK,
            NpmSetupTask.NAME
        )
    }

    @Test
    fun `should dynamically add additional files to task inputs`() {
        val inputFiles = listOf("package.json", "package-lock.json")
        val project = project()
            .withFiles(inputFiles)
            .withPlugins(WebjarPlugin::class)
            .build()
        val installTask = project.getNpmTask(WEBJAR_INSTALL_TASK)
        assertThat(installTask.inputs.files).containsAll(
            inputFiles.map { project.projectDir.resolve(it) }
        )
    }
}
