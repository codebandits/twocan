plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api(project(":components:identity:core"))

    implementation(project(":libs:http-support"))
    implementation(project(":libs:logging-support"))
    implementation(project(":libs:validation-support"))

    testImplementation(project(":libs:test-support"))
    testImplementation(project(":libs:http-test-support"))
    testRuntimeOnly(project(":libs:test-runtime-support"))
}
