import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

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
    archiveFileName.set("${project.parent?.parent?.name}-${project.parent?.name}-${project.name}.jar")
}

dependencies {
    implementation(libs.spring.boot.starter.core)
    implementation(libs.spring.boot.configuration)
    testImplementation(libs.spring.boot.starter.test)
}
