plugins {
    kotlin("jvm")
}

dependencies {
    api("com.natpryce:hamkrest:${Versions.HamKrest}")
    api("com.jayway.jsonpath:json-path-assert:${Versions.JsonPath}")
    api("io.mockk:mockk:${Versions.Mockk}")
    api("org.spekframework.spek2:spek-dsl-jvm:${Versions.Spek}")
}
