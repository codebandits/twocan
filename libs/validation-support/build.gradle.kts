plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":libs:http-support"))
    api("io.konform:konform:${Versions.Konform}")
}
