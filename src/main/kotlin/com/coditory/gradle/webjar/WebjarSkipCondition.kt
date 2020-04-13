package com.coditory.gradle.webjar

import org.gradle.api.Project

object WebjarSkipCondition {
    fun isWebjarSkipped(project: Project): Boolean {
        return hasSystemEnvironmentFlag() || hasPropertyFlag(project)
    }

    private fun hasSystemEnvironmentFlag(): Boolean {
        val value = System.getenv("SKIP_WEBJAR")
        return !value.isNullOrBlank() &&
            !value.toString().equals("false", true)
    }

    private fun hasPropertyFlag(project: Project): Boolean {
        val name = "skipWebjar"
        if (project.properties.containsKey(name)) {
            val value = project.properties[name]
            return value == null || !value.toString().equals("false", true)
        }
        return false
    }
}
