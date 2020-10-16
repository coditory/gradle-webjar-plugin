package acceptance

import com.coditory.gradle.webjar.base.JarExtractor
import com.coditory.gradle.webjar.base.SpecProjectBuilder
import com.coditory.gradle.webjar.base.SpecProjectRunner.runGradle
import com.coditory.gradle.webjar.base.readBuildFile
import com.coditory.gradle.webjar.base.readFile
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class BuildProjectSpec {
    private val exceptedJsScript = "console.log(\"Some js\")\n"
    private val project = SpecProjectBuilder.project("sample-project")
        .withBuildGradle(
            """
            plugins {
                id 'com.coditory.webjar'
            }
            version = '0.1.0-SNAPSHOT'

            // Default values just to check if mapping works
            webjar {
                // Directory where npm puts the result
                distDir = "dist"
                // Directory with npm results in the jar
                webjarDir = "static"

                // NPM Task names
                taskNames {
                    clean = "clean"
                    build = "build"
                    test = "test"
                    lint = "lint"
                    watch = "watch"
                }

                // Caching options
                cache {
                    enabled = true
                    cacheTest = true
                    cacheLint = true
                    // Some timestamp files used for gradle caching
                    testTimestampFile = "test/timestamp"
                    lintTimestampFile = "lint/timestamp"
                    // Location of src and dest input files
                    src = ["src"]
                    test = ["tests"]
                }
            }
            """
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
                "build": "mkdir -p dist && echo 'console.log(\"Some js\")' > dist/index.js",
                "watch": "echo 'missing watch script'"
              },
              "dependencies": {
                "@coditory/jsdeep": "^1.0.5"
              }
            }
            """
        )
        .build()

    @ParameterizedTest(name = "should clean build webjar for gradle version: {0}")
    @ValueSource(strings = ["current", "5.0"])
    fun `should clean build webjar`(gradleVersion: String?) {
        val result = runGradle(project, listOf("clean", "build"), gradleVersion)
        assertThat(result.task(":clean")?.outcome).isEqualTo(UP_TO_DATE)
        assertThat(result.task(":build")?.outcome).isEqualTo(SUCCESS)
        expectJsFileInJar()
        expectLintAndTestTimestampFiles()
        expectNodeAndNpmVersionFiles()
        expectNodeModuleDownloaded()
    }

    @Test
    fun `should cache webjar build tasks`() {
        runGradle(project, listOf("build"))
        val result = runGradle(project, listOf("build"))
        assertThat(result.task(":webjarLint")?.outcome).isEqualTo(UP_TO_DATE)
        assertThat(result.task(":webjarTest")?.outcome).isEqualTo(UP_TO_DATE)
        assertThat(result.task(":webjarBuild")?.outcome).isEqualTo(UP_TO_DATE)
    }

    private fun expectJsFileInJar() {
        assertThat(project.readFile("dist/index.js")).isEqualTo(exceptedJsScript)
        assertThat(project.readBuildFile("webjar/static/index.js")).isEqualTo(exceptedJsScript)
        assertThat(project.readBuildFile("resources/main/static/index.js")).isEqualTo(exceptedJsScript)
        JarExtractor.extractJar(project.buildDir.resolve("libs/sample-project-0.1.0-SNAPSHOT.jar"))
        assertThat(project.readBuildFile("libs/sample-project-0.1.0-SNAPSHOT/static/index.js")).isEqualTo(exceptedJsScript)
    }

    private fun expectNodeModuleDownloaded() {
        assertThat(project.readFile("node_modules/@coditory/jsdeep/package.json"))
            .contains(""""name": "@coditory/jsdeep"""")
    }

    private fun expectLintAndTestTimestampFiles() {
        assertThat(project.readBuildFile("test/timestamp")).isNotBlank()
        assertThat(project.readBuildFile("lint/timestamp")).isNotBlank()
    }

    private fun expectNodeAndNpmVersionFiles() {
        assertThat(project.readFile("node_modules/.nodeVersion")).isEqualTo("13.12.0")
        assertThat(project.readFile("node_modules/.npmVersion")).isEqualTo("6.14.4")
    }
}
