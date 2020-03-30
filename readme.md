# Frontend Gradle Plugin

[![Join the chat at https://gitter.im/coditory/gradle-frontend-plugin](https://badges.gitter.im/coditory/gradle-frontend-plugin.svg)](https://gitter.im/coditory/gradle-frontend-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/coditory/gradle-frontend-plugin.svg?branch=master)](https://travis-ci.org/coditory/gradle-frontend-plugin)
[![Coverage Status](https://coveralls.io/repos/github/coditory/gradle-frontend-plugin/badge.svg)](https://coveralls.io/github/coditory/gradle-frontend-plugin)
[![Gradle Plugin Portal](https://img.shields.io/badge/Plugin_Portal-v0.1.0-green.svg)](https://plugins.gradle.org/plugin/com.coditory.frontend)

This plugin connects JVM and Node based projects.
It maps typical java to npm tasks (build, test, clean, etc).
The build result is packed to a jar file so it can be imported as a dependency by a java based project.

## Enabling the plugin

Add to your `build.gradle`:

```gradle
plugins {
  id 'com.coditory.frontend' version '0.1.0'
}
```
