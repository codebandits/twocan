plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":libs:http-support"))
    implementation(project(":libs:logging-support"))
}
