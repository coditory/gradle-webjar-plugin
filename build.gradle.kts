import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import pl.allegro.tech.build.axion.release.domain.hooks.HookContext
import pl.allegro.tech.build.axion.release.domain.hooks.HooksConfig
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition

plugins {
    kotlin("jvm") version "1.3.72"
    id("jacoco")
    id("pl.allegro.tech.build.axion-release") version "1.11.0"
    id("com.github.kt3k.coveralls") version "2.10.1"
    id("com.gradle.plugin-publish") version "0.11.0"
    id("java-gradle-plugin")
    id("maven-publish")
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

repositories {
    jcenter()
    gradlePluginPortal()
}

ktlint {
    version.set("0.36.0")
    enableExperimentalRules.set(true)
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.github.node-gradle:gradle-node-plugin:2.2.3")

    testImplementation("org.assertj:assertj-core:3.16.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

scmVersion {
    versionCreator("versionWithBranch")
    hooks = HooksConfig().also {
        it.pre(
            "fileUpdate",
            mapOf(
                "files" to listOf("readme.md") as Any,
                "pattern" to KotlinClosure2<String, HookContext, String>({ v, _ -> v }),
                "replacement" to KotlinClosure2<String, HookContext, String>({ v, _ -> v })
            )
        )
        it.pre("commit", KotlinClosure2<String, ScmPosition, String>({ v, _ -> "Release: $v [ci skip]" }))
    }
}

group = "com.coditory.gradle"
version = scmVersion.version

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "11"
    }
    withType<Test> {
        testLogging {
            events("passed", "failed", "skipped")
            setExceptionFormat("full")
        }
    }
    withType<Test> {
        useJUnitPlatform()
    }
    jacocoTestReport {
        reports {
            xml.isEnabled = true
            html.isEnabled = true
        }
    }
    coveralls {
        sourceDirs = listOf("src/main/kotlin")
    }
}

gradlePlugin {
    plugins {
        create("webjarPlugin") {
            id = "com.coditory.webjar"
            implementationClass = "com.coditory.gradle.webjar.WebjarPlugin"
        }
    }
}

// Marking new version (incrementPatch [default], incrementMinor, incrementMajor)
// ./gradlew markNextVersion -Prelease.incrementer=incrementMinor
// Releasing the plugin:
// ./gradlew release && ./gradlew publishPlugins
pluginBundle {
    website = "https://github.com/coditory/gradle-webjar-plugin"
    vcsUrl = "https://github.com/coditory/gradle-webjar-plugin"
    description = "Creates jar with front end resources. Maps gradle java project tasks to npm tasks."
    tags = listOf("npm", "webjar")

    (plugins) {
        "webjarPlugin" {
            displayName = "Webjar plugin"
        }
    }
}
