package acceptance

import com.coditory.gradle.webjar.base.PackageJson.Companion.packageJson
import com.coditory.gradle.webjar.base.TestProjectBuilder
import com.coditory.gradle.webjar.base.TestProjectRunner.runGradle
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class FailBuildTest {
    private val project = TestProjectBuilder.project("sample-project")
        .withBuildGradle(
            """
            plugins {
                id 'com.coditory.webjar'
            }
            version = '0.1.0-SNAPSHOT'
            """.trimIndent()
        )
        .build()

    @Test
    fun `should not fail with logging scripts`() {
        packageJson(project)
            .withLoggingScripts()
            .writeFile()
        val result = runGradle(project, listOf("build"))
        assertThat(result.task(":build")?.outcome).isEqualTo(SUCCESS)
    }

    // There is no need to test other webjar tasks
    // @ValueSource(strings = ["test", "lint", "clean", "build"])
    @ParameterizedTest(name = "should fail build on failing npm task {0}")
    @ValueSource(strings = ["test"])
    fun `should fail build on failing webjarTest`(npmTask: String) {
        packageJson(project)
            .withLoggingScripts()
            .withScript(npmTask, "echo 'Failure: $npmTask' >&2; exit 1")
            .writeFile()
        assertThatThrownBy { runGradle(project, listOf("clean", "build")) }
            .hasMessageContaining("Failure: $npmTask")
    }
}
