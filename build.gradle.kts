import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    id("jacoco")
    id("com.github.kt3k.coveralls") version "2.12.0"
    id("com.gradle.plugin-publish") version "1.0.0"
    id("java-gradle-plugin")
    id("maven-publish")
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

ktlint {
    version.set("0.45.2")
}

dependencies {
    implementation("com.github.node-gradle:gradle-node-plugin:3.4.0")

    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

group = "com.coditory.gradle"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
        setExceptionFormat("full")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        allWarningsAsErrors = true
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

coveralls {
    sourceDirs = listOf("src/main/kotlin")
}

gradlePlugin {
    plugins {
        create("webjarPlugin") {
            id = "com.coditory.webjar"
            implementationClass = "com.coditory.gradle.webjar.WebjarPlugin"
            displayName = "Webjar plugin"
            description = "Creates jar with front end resources. Maps gradle java project tasks to npm tasks."
        }
    }
}

pluginBundle {
    website = "https://github.com/coditory/gradle-webjar-plugin"
    vcsUrl = "https://github.com/coditory/gradle-webjar-plugin"
    tags = listOf("npm", "webjar", "frontend")
}
