package com.coditory.gradle.webjar.base

import com.coditory.gradle.webjar.WebjarPlugin
import com.coditory.gradle.webjar.base.PackageJson.Companion.packageJson
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import java.io.File
import java.nio.file.Files
import kotlin.io.path.createTempDirectory
import kotlin.reflect.KClass

class TestProjectBuilder private constructor(projectDir: File, name: String) {
    private val project = ProjectBuilder.builder()
        .withProjectDir(projectDir)
        .withName(name)
        .build()

    fun withGroup(group: String): TestProjectBuilder {
        project.group = group
        return this
    }

    fun withVersion(version: String): TestProjectBuilder {
        project.version = version
        return this
    }

    fun withSkipWebjarFlag(): TestProjectBuilder {
        project.extensions.extraProperties.set("skipWebjar", "true")
        return this
    }

    fun withSamplePackageJson(): TestProjectBuilder {
        packageJson(project)
            .withLoggingScripts()
            .writeFile()
        return this
    }

    fun withPlugins(vararg plugins: KClass<out Plugin<*>>): TestProjectBuilder {
        plugins
            .toList()
            .forEach { project.plugins.apply(it.java) }
        return this
    }

    fun withBuildGradle(content: String): TestProjectBuilder {
        val buildFile = project.rootDir.resolve("build.gradle")
        buildFile.writeText(content.trimIndent().trim() + "\n")
        return this
    }

    fun withFile(path: String, content: String = ""): TestProjectBuilder {
        val filePath = project.rootDir.resolve(path).toPath()
        Files.createDirectories(filePath.parent)
        val testFile = Files.createFile(filePath).toFile()
        testFile.writeText(content.trimIndent().trim() + "\n")
        return this
    }

    fun withFiles(vararg path: String): TestProjectBuilder {
        path.forEach { withFile(it) }
        return this
    }

    fun withFiles(paths: List<String>): TestProjectBuilder {
        paths.forEach { withFile(it) }
        return this
    }

    fun build(): Project {
        return project
    }

    companion object {
        private var projectDirs = mutableListOf<File>()

        fun project(name: String = "sample-project"): TestProjectBuilder {
            return TestProjectBuilder(createProjectDir(name), name)
        }

        fun projectWithPlugins(name: String = "sample-project"): TestProjectBuilder {
            return project(name)
                .withPlugins(JavaPlugin::class, WebjarPlugin::class)
        }

        @Suppress("EXPERIMENTAL_API_USAGE_ERROR")
        private fun createProjectDir(directory: String): File {
            removeProjectDirs()
            val projectParentDir = createTempDirectory().toFile()
            val projectDir = projectParentDir.resolve(directory)
            projectDir.mkdir()
            projectDirs.add(projectParentDir)
            return projectDir
        }

        private fun removeProjectDirs() {
            projectDirs.forEach {
                it.deleteRecursively()
            }
        }
    }
}
