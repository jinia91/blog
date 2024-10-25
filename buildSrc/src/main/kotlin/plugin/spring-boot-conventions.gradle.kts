import gradle.kotlin.dsl.accessors._d73e6e232b216eb4ac6b7e087dea1520.allOpen
import gradle.kotlin.dsl.accessors._d73e6e232b216eb4ac6b7e087dea1520.noArg
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

plugins {
    id("conventions")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

allOpen {
    annotation("kr.co.jiniaslog.shared.core.annotation.CustomComponent")
}

noArg {
    annotation("kr.co.jiniaslog.shared.core.annotation.NoArgConstructor")
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
    implementation(libs.spring.data.redis)
    testImplementation(libs.spring.boot.starter.test)
}
