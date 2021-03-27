buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = Versions.Kotlin))
        classpath(kotlin("serialization", version = Versions.Kotlin))
        classpath("com.github.jengelman.gradle.plugins:shadow:${Versions.Shadow}")
    }
}

subprojects {
    repositories {
        jcenter()
    }

    tasks.withType<Jar> {
        archiveBaseName.set("${project.group}.${project.name}")
    }
}
