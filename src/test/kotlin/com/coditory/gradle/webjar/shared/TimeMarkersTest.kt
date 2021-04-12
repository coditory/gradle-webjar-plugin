package com.coditory.gradle.webjar.shared

import com.coditory.gradle.webjar.base.TestProjectBuilder
import com.coditory.gradle.webjar.base.UpdatableFixedClock
import com.coditory.gradle.webjar.base.readBuildFile
import com.coditory.gradle.webjar.shared.TimeMarkers.createTimeMarkerFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TimeMarkersTest {
    private val clock = UpdatableFixedClock()
    private val project = TestProjectBuilder.projectWithPlugins()
        .withSamplePackageJson()
        .build()

    @Test
    fun `should write time marker file`() {
        createTimeMarkerFile(project, "test/time", clock)
        val timestamp = project.readBuildFile("test/time")
        assertThat(timestamp).isEqualTo("2015-12-03T10:15:30.123456Z")
    }
}
