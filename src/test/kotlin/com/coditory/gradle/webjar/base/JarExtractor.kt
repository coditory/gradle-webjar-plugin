package com.coditory.gradle.webjar.base

import java.io.File
import java.util.jar.JarFile

object JarExtractor {
    fun extractJar(path: String) {
        val jar = JarFile(path)
        val jarFile = File(jar.name)
        val distDir = jarFile.parent + File.separator
        val libName = jarFile.name.split("\\.").first()
        new File("${distDir+libName}").with {
            if (!it.exists()) it.mkdir()
        }
        distDir += libName+File.separator
        for (JarEntry file in jar.entries()){
            def f = new File("${distDir+file.name}")
            if (file.isDirectory()){
                f.mkdir()
                continue
            }
            def is = jar.getInputStream(file)
            f.withOutputStream { def stream ->
                while (is.available()>0)
                stream.write(is.read())
            }
            is.close()
        }
        jar.close()
    }
}
