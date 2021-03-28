plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":libs:http-support"))
    implementation(project(":libs:test-support"))
    implementation(platform("org.http4k:http4k-bom:${Versions.Http4k}"))
    implementation("org.http4k:http4k-server-jetty")
    api("org.http4k:http4k-testing-hamkrest")
}
