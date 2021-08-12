plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":libs:http-support"))
    implementation(kotlin("reflect", version = Versions.Kotlin))
    implementation("com.sun.mail:jakarta.mail:${Versions.JakartaMail}")
    api("io.konform:konform:${Versions.Konform}")

    testImplementation(project(":libs:test-support"))
    testRuntimeOnly(project(":libs:test-runtime-support"))
}
