plugins {
    kotlin("jvm")
}

dependencies {
    api("org.spekframework.spek2:spek-runner-junit5:${Versions.Spek}")
    api(project(":libs:logging-runtime-support"))
}
