package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INIT_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_REMOVE_MODULES_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.base.SpecProjectBuilder
import com.coditory.gradle.webjar.base.getNpmTask
import com.moowork.gradle.node.npm.NpmSetupTask
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WebjarInitSpec {
    private val project = SpecProjectBuilder.projectWithPlugins()
        .withSamplePackageJson()
        .build()

    @Test
    fun `should configure webjarInit task`() {
        val initTask = project.getNpmTask(WEBJAR_INIT_TASK)
        assertThat(initTask.group).isEqualTo(WEBJAR_TASK_GROUP)
        assertThat(initTask.args).isEqualTo(listOf("install", "--package-lock-only"))
        assertThat(initTask.outputs.files).contains(project.projectDir.resolve("package-lock.json"))
        assertThat(initTask.inputs.files).contains(project.projectDir.resolve("package.json"))
        assertThat(initTask.dependsOn).contains(
            WEBJAR_REMOVE_MODULES_TASK,
            NpmSetupTask.NAME
        )
    }
}
