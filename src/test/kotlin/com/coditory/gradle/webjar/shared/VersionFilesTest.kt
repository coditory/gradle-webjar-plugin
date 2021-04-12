package com.coditory.gradle.webjar.shared

import com.coditory.gradle.webjar.base.TestProjectBuilder
import com.coditory.gradle.webjar.base.readFile
import com.coditory.gradle.webjar.base.writeFile
import com.coditory.gradle.webjar.shared.VersionFiles.nodeVersionFile
import com.coditory.gradle.webjar.shared.VersionFiles.npmVersionFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VersionFilesTest {
    private val project = TestProjectBuilder.projectWithPlugins()
        .withSamplePackageJson()
        .build()

    private val nodeVersion = "v12.16.1"
    private val npmVersion = "6.14.4"

    @Test
    fun `should return undefined if version file does not exist`() {
        val version = nodeVersionFile(project).read()
        assertThat(version).isEqualTo("undefined")
    }

    @Test
    fun `should read previously created node version file`() {
        project.writeFile("node_modules/.nodeVersion", nodeVersion)
        val version = nodeVersionFile(project).read()
        assertThat(version).isEqualTo(nodeVersion)
    }

    @Test
    fun `should write node version file`() {
        nodeVersionFile(project).write(nodeVersion)
        assertThat(project.readFile("node_modules/.nodeVersion"))
            .isEqualTo(nodeVersion)
    }

    @Test
    fun `should write and read node version file`() {
        nodeVersionFile(project).write(nodeVersion)
        val version = nodeVersionFile(project).read()
        assertThat(version).isEqualTo(nodeVersion)
    }

    @Test
    fun `should read and write npm version file`() {
        project.writeFile("node_modules/.npmVersion", npmVersion)
        val version = npmVersionFile(project).read()
        assertThat(version).isEqualTo(npmVersion)
    }
}
