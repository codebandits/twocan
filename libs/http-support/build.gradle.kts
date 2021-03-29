plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api(platform("org.http4k:http4k-bom:${Versions.Http4k}"))
    api("org.http4k:http4k-core")
    api("org.http4k:http4k-cloudnative")
    api(project(":libs:serialization-support"))
}
