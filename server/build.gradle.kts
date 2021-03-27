plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(platform("org.http4k:http4k-bom:${Versions.Http4k}"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-jetty")
    implementation("org.http4k:http4k-cloudnative")
    implementation(project(":components:identity"))

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
                            "SERVICE_PORT" to "8080",
                            "HEALTH_PORT" to "8181"
                    )
            )
        }
    }
}
