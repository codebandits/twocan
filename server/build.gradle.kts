plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":components:birds"))
    implementation(project(":components:identity"))

    implementation(project(":libs:http-support"))
    implementation(project(":libs:http-server-support"))

    runtimeOnly(project(":libs:logging-runtime-support"))
}

application {
    mainClassName = "io.twocan.ServerKt" // https://git.io/JT9Sg
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveClassifier.set("")
    }

    withType<AbstractArchiveTask> {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    getByName<JavaExec>("run") {
        doFirst {
            setEnvironment(
                    mapOf(
                            "IDENTITY_URI" to "http://localhost:8080/",
                            "SERVICE_PORT" to "8080",
                            "HEALTH_PORT" to "8181"
                    )
            )
        }
    }
}
