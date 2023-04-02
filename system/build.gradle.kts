import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * System's Global Convention
 * - Kotlin version 1.8.10
 * - Kotlinter version 3.14.0
 * - mockk version 1.13.4
 * - kotest version 5.5.4
 */

plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
    id("org.jmailen.kotlinter") version "3.14.0" apply false
}

repositories {
    mavenCentral()
}

group = "kr.co.jiniaslog"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.getByName("jar") {
    enabled = false
}

val localLib = ":system:lib"

subprojects {
    if(project.subprojects.isNotEmpty()) return@subprojects // build only leaf project

    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jmailen.kotlinter")

    repositories {
        mavenCentral()
    }

    tasks.getByName("jar") {
        enabled = false
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    afterEvaluate {
        // adding global dependency
        if (project.path != localLib) {
            dependencies {
                api(project(localLib))
                implementation("org.jetbrains.kotlin:kotlin-reflect")

                testImplementation("io.mockk:mockk:1.13.4")
                testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
                testImplementation("io.kotest:kotest-assertions-core:5.5.4")
                implementation("io.kotest:kotest-extensions-spring:4.4.3")

                testImplementation("org.assertj:assertj-core:3.24.2")
                testImplementation("ch.qos.logback:logback-classic:1.4.5")
            }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = freeCompilerArgs + "-Xjsr305=strict"
        }
    }

    tasks.getByName("jar") {
        enabled = false
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}