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
    var distDir: String = "dist"
    var outputDir: String = "webjar"
    var webjarDir: String = "static"
}
