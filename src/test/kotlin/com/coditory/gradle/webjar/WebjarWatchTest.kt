package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_WATCH_TASK
import com.coditory.gradle.webjar.base.TestProjectBuilder.Companion.projectWithPlugins
import com.coditory.gradle.webjar.base.getNpmTask
import com.github.gradle.node.npm.task.NpmSetupTask
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WebjarWatchTest {
    private val project = projectWithPlugins()
        .withSamplePackageJson()
        .build()

    @Test
    fun `should configure webjarClean task`() {
        val watchTask = project.getNpmTask(WEBJAR_WATCH_TASK)
        assertThat(watchTask.group).isEqualTo(WEBJAR_TASK_GROUP)
        assertThat(watchTask.args.get()).isEqualTo(listOf("run", "watch"))
        assertThat(watchTask.dependsOn).contains(
            WEBJAR_INSTALL_TASK,
            NpmSetupTask.NAME
        )
    }
}
