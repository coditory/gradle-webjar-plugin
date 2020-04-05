package com.coditory.gradle.webjar.shared

import org.gradle.api.Project
import java.time.Clock

internal object TimeMarkers {
    fun createTimeMarkerFile(project: Project, relativePath: String, clock: Clock = Clock.systemUTC()) {
        val now = clock.instant()
        val timeMarker = project.buildDir.resolve(relativePath)
        timeMarker.parentFile.mkdirs()
        timeMarker.writeText(now.toString())
    }
}
