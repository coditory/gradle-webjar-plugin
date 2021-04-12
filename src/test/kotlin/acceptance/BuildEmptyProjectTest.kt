package acceptance

import com.coditory.gradle.webjar.base.TestProjectBuilder
import com.coditory.gradle.webjar.base.TestProjectRunner.runGradle
import com.coditory.gradle.webjar.base.readFile
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test

class BuildEmptyProjectTest {
    private val project = TestProjectBuilder.project("sample-project")
        .withBuildGradle(
            """
            plugins {
                id 'com.coditory.webjar'
            }
            """.trimIndent()
        )
        .build()

    @Test
    fun `should end with success and generate default package-json`() {
        val result = runGradle(project, listOf("build"))
        assertThat(result.task(":build")?.outcome)
            .isEqualTo(TaskOutcome.SUCCESS)
        assertThat(project.readFile("package.json").trim()).isEqualTo(
            """
            {
                "name": "sample-project",
                "scripts": {
                    "lint": "echo 'missing lint script'",
                    "test": "echo 'missing test script'",
                    "clean": "echo 'missing clean script'",
                    "build": "echo 'missing build script'",
                    "watch": "echo 'missing watch script'"
                }
            }
            """.trimIndent().trim()
        )
    }
}
