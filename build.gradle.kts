import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

buildscript {
    repositories {
        gradlePluginPortal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = Versions.Kotlin))
        classpath(kotlin("serialization", version = Versions.Kotlin))
        classpath("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:${Versions.Shadow}")
    }
}

subprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<Jar> {
        archiveBaseName.set("${project.group}.${project.name}")
    }

    tasks.withType<Test> {
        useJUnitPlatform {
            includeEngines("spek2")
        }

        testLogging {
            events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    configurations.all {
        resolutionStrategy.force("org.slf4j:slf4j-api:${Versions.Sl4fj}")
    }
}
