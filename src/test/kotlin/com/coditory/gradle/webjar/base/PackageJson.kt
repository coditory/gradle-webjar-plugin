package com.coditory.gradle.webjar.base

import org.gradle.api.Project

class PackageJson(
    private val project: Project
) {
    private val scripts: MutableMap<String, String> = mutableMapOf()

    fun withLoggingScripts(): PackageJson {
        listOf("clean", "test", "lint", "build")
            .filter { !scripts.containsKey(it) }
            .forEach { scripts[it] = "echo 'npm - $it'" }
        return this
    }

    fun withScript(name: String, script: String): PackageJson {
        scripts[name] = script
        return this
    }

    fun writeFile() {
        project.projectDir.resolve("package.json")
            .writeText(build())
    }

    private fun build(): String {
        return toJson(
            mapOf(
                "name" to project.name,
                "scripts" to scripts
            )
        )
    }

    private fun toJson(value: Any?, padding: Int = 0): String {
        if (value == null) return "null"
        if (value is String) return "\"$value\""
        if (value is Map<*, *>) {
            val subPadding = padding + 2
            val subSpace = " ".repeat(subPadding)
            val space = " ".repeat(padding)
            return value
                .map { "${subSpace}\"${it.key}\": ${toJson(it.value, subPadding)}" }
                .joinToString(",\n", "{\n", "\n${space}}")
        }
        return value.toString()
    }

    companion object {
        fun packageJson(project: Project): PackageJson {
            return PackageJson(project)
        }
    }
}
