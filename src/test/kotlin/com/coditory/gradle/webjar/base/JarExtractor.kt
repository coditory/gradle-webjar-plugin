package com.coditory.gradle.webjar.base

import java.io.File
import java.util.jar.JarFile

object JarExtractor {
    fun extractJar(jarFile: File, to: String? = null) {
        extractJar(jarFile.absolutePath, to)
    }

    fun extractJar(path: String, to: String? = null) {
        JarFile(path)
            .use { extractJar(it, to) }
    }

    private fun extractJar(jar: JarFile, to: String? = null) {
        val jarFile = File(jar.name)
        val distDir = (to ?: jarFile.parent) + File.separator
        val libName = jarFile.name.removeSuffix(".jar")
        File(distDir + libName).let {
            if (!it.exists()) it.mkdir()
        }
        val extractDir = distDir + libName + File.separator
        jar.entries().toList().forEach {
            val file = File(extractDir + it.name)
            if (it.isDirectory()) {
                file.mkdir()
            } else {
                file.parentFile.mkdirs()
                jar.getInputStream(it)
                    .copyTo(file.outputStream())
            }
        }
    }
}
