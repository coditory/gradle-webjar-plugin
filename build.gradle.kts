import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
    id("jacoco")
    id("com.github.kt3k.coveralls") version "2.12.0"
    id("com.gradle.plugin-publish") version "0.14.0"
    id("java-gradle-plugin")
    id("maven-publish")
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

ktlint {
    version.set("0.41.0")
}

dependencies {
    implementation(gradleApi())
    implementation("com.github.node-gradle:gradle-node-plugin:3.0.1")

    testImplementation("org.assertj:assertj-core:3.19.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
}

group = "com.coditory.gradle"

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
        setExceptionFormat("full")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
        allWarningsAsErrors = true
    }
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
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
        }
    }
}

pluginBundle {
    website = "https://github.com/coditory/gradle-webjar-plugin"
    vcsUrl = "https://github.com/coditory/gradle-webjar-plugin"
    description = "Creates jar with front end resources. Maps gradle java project tasks to npm tasks."
    tags = listOf("npm", "webjar", "frontend")

    (plugins) {
        "webjarPlugin" {
            displayName = "Webjar plugin"
        }
    }
}
