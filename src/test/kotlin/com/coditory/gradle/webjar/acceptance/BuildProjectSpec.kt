package com.coditory.gradle.webjar.acceptance

import com.coditory.gradle.webjar.base.SpecProjectBuilder
import com.coditory.gradle.webjar.base.SpecProjectRunner.runGradle
import com.coditory.gradle.webjar.base.readBuildFile
import com.coditory.gradle.webjar.base.readFile
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.provider.ValueSource

class BuildProjectSpec {
    private val exceptedJsScript = "console.log(\"Some js\")\n"
    private val project = SpecProjectBuilder.project("sample-project")
        .withBuildGradle(
            """
            plugins {
                id 'com.coditory.webjar'
            }
            """.trimIndent()
        )
        .withFile(
            "package.json",
            """
            {
                "name": "sample-project",
                "scripts": {
                    "lint": "echo 'no lint problems'",
                    "test": "echo 'tests passed'",
                    "clean": "rm -rf dist",
                    "build": "mkdir dist && echo 'console.log(\"Some js\")' > dist/index.js",
                    "watch": "echo 'missing watch script'"
                },
                "dependencies": {
                    "@coditory/jsdeep": "^1.0.5"
                }
            }
            """.trimIndent()
        )
        .build()

    @Test
    @ValueSource(strings = ["current", "5.0"])
    fun `should clean build webjar`() {
        val result = runGradle(project, listOf("clean", "build"))
        assertThat(result.task(":clean")?.outcome).isEqualTo(TaskOutcome.UP_TO_DATE)
        assertThat(result.task(":build")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        assertThat(project.readFile("dist/index.js")).isEqualTo(exceptedJsScript)
        assertThat(project.readBuildFile("webjar/static/index.js")).isEqualTo(exceptedJsScript)
        assertThat(project.readBuildFile("resources/main/static/index.js")).isEqualTo(exceptedJsScript)
        assertThat(project.readFile("node_modules/.nodeVersion")).isEqualTo("13.12.0")
        assertThat(project.readFile("node_modules/.npmVersion")).isEqualTo("6.14.4")
        assertThat(project.readBuildFile("test/timestamp")).isNotBlank()
        assertThat(project.readBuildFile("lint/timestamp")).isNotBlank()
    }
}
