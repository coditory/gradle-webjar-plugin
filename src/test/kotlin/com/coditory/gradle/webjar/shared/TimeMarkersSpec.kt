package com.coditory.gradle.webjar.shared

import com.coditory.gradle.webjar.shared.TimeMarkers.createTimeMarkerFile
import com.coditory.gradle.webjar.base.SpecProjectBuilder
import com.coditory.gradle.webjar.base.UpdatableFixedClock
import com.coditory.gradle.webjar.base.readBuildFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TimeMarkersSpec {
    private val clock = UpdatableFixedClock()
    private val project = SpecProjectBuilder.projectWithPlugins()
        .withSamplePackageJson()
        .build()

    @Test
    fun `should write time marker file`() {
        createTimeMarkerFile(project, "test/time", clock)
        val timestamp = project.readBuildFile("test/time")
        assertThat(timestamp).isEqualTo("2015-12-03T10:15:30.123456Z")
    }
}
