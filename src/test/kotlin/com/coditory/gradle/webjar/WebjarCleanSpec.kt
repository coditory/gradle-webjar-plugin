package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_CLEAN_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_INSTALL_TASK
import com.coditory.gradle.webjar.WebjarPlugin.Companion.WEBJAR_TASK_GROUP
import com.coditory.gradle.webjar.base.SpecProjectBuilder.Companion.project
import com.coditory.gradle.webjar.base.SpecProjectBuilder.Companion.projectWithPlugins
import com.coditory.gradle.webjar.base.getNpmTask
import com.coditory.gradle.webjar.base.getNpmTaskProvider
import com.coditory.gradle.webjar.base.getTask
import com.moowork.gradle.node.npm.NpmSetupTask
import org.assertj.core.api.Assertions.assertThat
import org.gradle.language.base.plugins.LifecycleBasePlugin.CLEAN_TASK_NAME
import org.junit.jupiter.api.Test

class WebjarCleanSpec {
    private val project = projectWithPlugins()
        .withSamplePackageJson()
        .build()

    @Test
    fun `should configure webjarClean task`() {
        val cleanTask = project.getNpmTask(WEBJAR_CLEAN_TASK)
        assertThat(cleanTask.group).isEqualTo(WEBJAR_TASK_GROUP)
        assertThat(cleanTask.args).isEqualTo(listOf("run", "clean"))
        assertThat(cleanTask.dependsOn).contains(
            WEBJAR_INSTALL_TASK,
            NpmSetupTask.NAME
        )
    }

    @Test
    fun `should configure webjarClean task to run before clean task`() {
        val cleanTask = project.getNpmTaskProvider(WEBJAR_CLEAN_TASK)
        val javaCleanTask = project.getTask(CLEAN_TASK_NAME)
        assertThat(javaCleanTask.dependsOn).contains(cleanTask)
    }

    @Test
    fun `should not configure webjarClean task to run before clean task on --skipWebjar flag`() {
        val project = project()
            .withSkipWebjarFlag()
            .withPlugins(WebjarPlugin::class)
            .build()
        val cleanTask = project.getNpmTaskProvider(WEBJAR_CLEAN_TASK)
        val javaCleanTask = project.getTask(CLEAN_TASK_NAME)
        assertThat(javaCleanTask.dependsOn).doesNotContain(cleanTask)
    }
}
