plugins {
    kotlin("jvm")
}

dependencies {
    api(platform("org.http4k:http4k-bom:${Versions.Http4k}"))
    api("org.http4k:http4k-client-apache")
}
