package com.coditory.gradle.webjar

import com.coditory.gradle.webjar.base.SpecProjectBuilder.Companion.projectWithPlugins
import com.moowork.gradle.node.NodePlugin
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PluginSetupSpec {
    private val project = projectWithPlugins()
        .build()

    @Test
    fun `should register plugin with its dependencies`() {
        assertThat(project.plugins.getPlugin(WebjarPlugin.PLUGIN_ID))
            .isInstanceOf(WebjarPlugin::class.java)
        assertThat(project.plugins.getPlugin("com.github.node-gradle.node"))
            .isInstanceOf(NodePlugin::class.java)
    }
}
