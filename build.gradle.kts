/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/8.0.2/userguide/building_java_projects.html
 */

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.8.21"

    id("org.openjfx.javafxplugin") version "0.0.13"

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // This dependency is used by the application.
    implementation("com.google.guava:guava:31.1-jre")

    implementation("no.tornado:tornadofx:1.7.20")

    implementation("io.socket:socket.io-client:2.0.1") {
        exclude("org.json', module: 'json")
    }
}

application {
    // Define the main class for the application.
    mainClass.set("app.MyApp")
}

javafx {
    version = "20"
    modules("javafx.controls", "javafx.fxml")
}

configure<SourceSetContainer>
{
    named("main")
    {
        java.srcDir("src")
    }
}