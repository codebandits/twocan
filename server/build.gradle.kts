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
    mainClass.set("io.twocan.ServerKt")
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
            environment = mapOf(
                "IDENTITY_URI" to "http://localhost:8080/",
                "SERVICE_PORT" to "8080",
                "HEALTH_PORT" to "8181"
            )
        }
    }
}
