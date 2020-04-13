package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INIT_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_REMOVE_MODULES_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.base.SpecProjectBuilder.Companion.projectWithPlugins
import com.coditory.gradle.webjar.base.getNpmTask
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
        assertThat(installTask.args).isEqualTo(listOf("install", "--no-package-lock", "--no-save"))
        assertThat(installTask.outputs.files).contains(project.projectDir.resolve("node_modules"))
        assertThat(installTask.inputs.files).contains(
            project.projectDir.resolve("package.json"),
            project.projectDir.resolve("package-lock.json")
        )
        assertThat(installTask.dependsOn).contains(
            WEBJAR_REMOVE_MODULES_TASK,
            WEBJAR_INIT_TASK
        )
    }
}
