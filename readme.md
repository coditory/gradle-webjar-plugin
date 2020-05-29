# Webjar Gradle Plugin

[![Join the chat at https://gitter.im/coditory/gradle-webjar-plugin](https://badges.gitter.im/coditory/gradle-webjar-plugin.svg)](https://gitter.im/coditory/gradle-webjar-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.com/coditory/gradle-webjar-plugin.svg?branch=master)](https://travis-ci.com/coditory/gradle-webjar-plugin)
[![Coverage Status](https://coveralls.io/repos/github/coditory/gradle-webjar-plugin/badge.svg)](https://coveralls.io/github/coditory/gradle-webjar-plugin)
[![Gradle Plugin Portal](https://img.shields.io/badge/Plugin_Portal-v0.1.3-green.svg)](https://plugins.gradle.org/plugin/com.coditory.webjar)

This plugin connects JVM and Node based projects.
It maps typical java to npm tasks (build, test, clean, etc).
The build result is packed into a jar file so it can be imported as a dependency by a java project.

This plugin builds frontend submodules. It can be also used to build a [webjar libraries](#building-webjar-library). See [sample usage](https://github.com/coditory/gradle-webjar-plugin-sample).

## Enabling the plugin

Add to your `build.gradle`:

```gradle
plugins {
  id 'com.coditory.webjar' version '0.1.3'
}
```

When the project is build (`./gradlew buid`),
produced jar contains all front end resources.

## Tasks

| Gradle Task     | Npm Task         | Description |
| ---             | ---              | ---         |
| `webjarClean`   | `clean`          | Cleans output directory |
| `webjarLint`    | `lint`           | Checkstyle sources      |
| `webjarTest`    | `test`           | Run tests               |
| `webjarBuild`   | `build`          | Build Project           |
| `webjarWatch`   | `watch`          | Run in watch mode. Should be run with `--no-daemon` in order to stop the process on `ctrl+c` |

There is also `webjarInit` that:
- downloads Node and NPM
- creates `package.json` and `package-lock.json` if missing

*Why should I use gradle tasks instead of npm tasks*
Gradle tasks runs using embedded node.
- `npm run watch` uses system Node and NPM
- `./gradlew webjarWatch --no-daemon` uses project Node and NPM

## Skipping webjar build
Frontend projects take a lot of time to build.
You can skip frontend build with:
- `./gradlew build -PskipWebjar` - project property
- `SKIP_WEBJAR=true ./gradlew build` - system environment variable

## Configuration

### Configuring Webjar plugin

```gradle
webjar {
    // NPM Task names
    cleanTaskName = "clean"
    buildTaskName = "build"
    testTaskName = "test"
    lintTaskName = "lint"
    watchTaskName = "watch"
    // Some timestamp files used for gradle caching
    testTimestampFile = "test/timestamp"
    lintTimestampFile = "lint/timestamp"
    // Front end project structure
    srcDir = "src"
    srcDirs = emptyList()
    testDir = "tests"
    testDirs = emptyList()
    distDir = "dist"
    // Directory in the output jar
    webjarDir = "static"
    // caching options
    cacheTest = true
    cacheLint = true
}
```
All values from above example are defaults.

### Configuring Node and NPM
Webjar plugin uses great [`gradle-node-plugin`](https://github.com/node-gradle/gradle-node-plugin).
You can configure Node and NPM with:

```gradle
node {
  // Version of node to use.
  version = '13.12.0'
  // Version of npm to use.
  npmVersion = '6.14.4'
  // Base URL for fetching node distributions (change if you have a mirror).
  // Or set to null if you want to add the repository on your own.
  distBaseUrl = 'https://nodejs.org/dist'
  // If true, it will download node using above parameters.
  // If false, it will try to use globally installed node.
  download = true
  // Set the work directory for unpacking node
  workDir = file("${project.buildDir}/.node/node")
  // Set the work directory for NPM
  npmWorkDir = file("${project.buildDir}/.node/npm")
  // Set the work directory where node_modules should be located
  nodeModulesDir = file("${project.projectDir}")
}
```
All values from above example are defaults setup by webjar plugin.

## Using for a front end submodule

There is a project with two submodules:

```
my-project
 |- backend
 |- frontend
```

Backend project depends on frontend project:

```gradle
dependencies {
    implementation(project(":frontend"))
}
```

Frontend project uses `webjar` plugin to map npm tasks to gradle.
When frontend is built all frontend resources are available on backend classpath under `/static` folder.

## Building [webjar library](https://www.webjars.org/)

Creating a webjar library for [webjars.org](https://www.webjars.org/),
requires specifying standardized `webjarDir`

```gradle
webjar {
  webjarDir = "META-INF/resources/webjars/${project.name}/${project.version}"
}
```
