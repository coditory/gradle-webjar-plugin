package com.coditory.gradle.frontend

import com.coditory.gradle.frontend.base.SpecProjectBuilder.Companion.projectWithPlugins
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PluginSetupSpec {
    private val project = projectWithPlugins()
        .build()

    @Test
    fun `should register build plugin with its dependencies`() {
        assertThat(project.plugins.getPlugin(FrontendPlugin.PLUGIN_ID))
            .isInstanceOf(FrontendPlugin::class.java)
    }
}
