package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_REMOVE_MODULES_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.base.TestProjectBuilder
import com.coditory.gradle.webjar.base.getTask
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class WebjarRemoveModulesTest {
    private val project = TestProjectBuilder.projectWithPlugins()
        .withSamplePackageJson()
        .build()

    @Test
    fun `should configure webjarRemoveModules task`() {
        val removeModulesTask = project.getTask(WEBJAR_REMOVE_MODULES_TASK)
        Assertions.assertThat(removeModulesTask.group).isEqualTo(WEBJAR_TASK_GROUP)
    }
}
