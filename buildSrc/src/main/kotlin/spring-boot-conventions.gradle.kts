plugins {
    id("conventions")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}


tasks.findByName("bootJar")?.let {
    it.enabled = false
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    enabled = true
}

val jar: Jar by tasks
jar.enabled = true
jar.archiveFileName.set("${project.parent?.name}-${project.name}.jar")

dependencies {
    implementation(libs.spring.boot.webflux)
    implementation(libs.spring.boot.validation)
    implementation(libs.spring.boot.configuration.processor)

    testImplementation(libs.kotest.extensions.spring)
    testImplementation(libs.spring.boot.test) {
        exclude(module = "mockito-core")
    }
}