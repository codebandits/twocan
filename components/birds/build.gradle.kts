plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api(project(":components:birds:core"))
    implementation(project(":components:identity:client"))

    implementation(project(":libs:http-support"))
    implementation(project(":libs:logging-support"))

    testImplementation(project(":libs:test-support"))
    testImplementation(project(":libs:http-test-support"))
    testRuntimeOnly(project(":libs:test-runtime-support"))
}
