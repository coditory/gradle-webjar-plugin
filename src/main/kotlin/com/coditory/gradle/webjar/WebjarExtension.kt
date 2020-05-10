package com.coditory.gradle.webjar

open class WebjarExtension {
    var cleanTaskName: String = "clean"
    var buildTaskName: String = "build"
    var testTaskName: String = "test"
    var testTimestampFile: String = "test/timestamp"
    var lintTaskName: String = "lint"
    var lintTimestampFile: String = "lint/timestamp"
    var watchTaskName: String = "watch"
    var srcDir: String = "src"
    var testDir: String = "tests"
    var srcDirs: List<String> = emptyList()
    var testDirs: List<String> = emptyList()
    var distDir: String = "dist"
    var outputDir: String = "webjar"
    var webjarDir: String = "static"
    var cacheTest: Boolean = true
    var cacheLint: Boolean = true

    internal fun resolveSrcDirs(): List<String> {
        return if (srcDirs.isEmpty()) listOf(srcDir) else srcDirs
    }

    internal fun resolveTestDirs(): List<String> {
        return if (testDirs.isEmpty()) listOf(testDir) else testDirs
    }
}
