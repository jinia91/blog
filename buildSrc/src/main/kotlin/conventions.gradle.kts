import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("java-test-fixtures")
    id("org.jetbrains.kotlinx.kover")
}

allOpen {
    annotation("kr.co.jiniaslog.shared.core.annotation.CustomComponent")
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    test {
        useJUnitPlatform()
    }

    findByName("bootJar")?.let {
        it.enabled = false
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        enabled = true
        archiveFileName.set("${project.parent?.parent?.name}-${project.parent?.name}-${project.name}.jar")
    }
}

dependencies {
    // core
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.0")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    // test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("ch.qos.logback:logback-classic:1.4.5")
}
