plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":components:identity:core"))

    implementation(project(":libs:http-support"))
    implementation(project(":libs:http-client-support"))
    implementation(project(":libs:logging-support"))

    testImplementation(project(":libs:test-support"))
    testImplementation(project(":libs:http-test-support"))
    testRuntimeOnly(project(":libs:test-runtime-support"))
}

