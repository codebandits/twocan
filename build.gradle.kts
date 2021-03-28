import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = Versions.Kotlin))
        classpath(kotlin("serialization", version = Versions.Kotlin))
        classpath("com.github.jengelman.gradle.plugins:shadow:${Versions.Shadow}")
    }
}

subprojects {
    repositories {
        jcenter()
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
}
