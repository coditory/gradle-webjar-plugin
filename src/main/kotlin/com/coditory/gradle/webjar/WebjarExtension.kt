package com.coditory.gradle.webjar

import org.gradle.api.Action

open class WebjarExtension {
    var distDir: String = "dist"
    var outputDir: String = "webjar"
    var webjarDir: String = "static"
    var taskNames: WebjarTaskNames = WebjarTaskNames()
    var cache: WebjarCache = WebjarCache()

    fun taskNames(config: Action<in WebjarTaskNames>) {
        config.execute(taskNames)
    }

    fun cache(config: Action<in WebjarCache>) {
        config.execute(cache)
    }
}

open class WebjarTaskNames {
    var clean: String = "clean"
    var build: String = "build"
    var test: String = "test"
    var lint: String = "lint"
    var watch: String = "watch"
}

open class WebjarCache {
    var enabled: Boolean = true
    var cacheTest: Boolean = true
    var cacheLint: Boolean = true

    // relative to project root directory
    var src: List<String> = listOf("src")
    var test: List<String> = listOf("tests")
    var dist: String = "dist"

    // relative to project build directory
    var testTimestampFile: String = "test/timestamp"
    var lintTimestampFile: String = "lint/timestamp"
}
