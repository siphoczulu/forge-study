import org.gradle.api.tasks.JavaExec

plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
}

javafx {
    version = "25.0.2"
    modules = listOf("javafx.controls")
}

application {
    mainClass.set("com.forge.fx.ForgeFxApp")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
tasks.register<JavaExec>("runCli") {
    group = "application"
    description = "Run Forge CLI (v0.1)"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.forge.App")
    standardInput = System.`in`
}