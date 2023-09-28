import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * System's Global Convention
 * - Kotlin version 1.8.10
 * - Kotlinter version 3.14.0
 * - mockk version 1.13.4
 * - kotest version 5.5.4
 */

plugins {
    kotlin("jvm")
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

subprojects {

    if (project.subprojects.isNotEmpty()) return@subprojects // build only leaf project

    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jmailen.kotlinter")

    repositories {
        mavenCentral()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    afterEvaluate {

        val sharedCoreKernel = ":system:shared-core-kernel"

        // adding global dependency
        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-reflect")
            implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

            testImplementation("io.mockk:mockk:1.13.4")
            testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
            testImplementation("io.kotest:kotest-assertions-core:5.5.4")
            implementation("io.kotest:kotest-extensions-spring:4.4.3")

            testImplementation("org.assertj:assertj-core:3.24.2")
            testImplementation("ch.qos.logback:logback-classic:1.4.5")
        }

        if (project.path != sharedCoreKernel) {
            dependencies {
                implementation(project(sharedCoreKernel))
            }
        }

        tasks.withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "17"
                freeCompilerArgs = freeCompilerArgs + "-Xjsr305=strict"
            }
        }

        tasks.findByName("bootJar")?.let {
            it.enabled = false
        }

        tasks.named<Jar>("jar") {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            enabled = true
        }

        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
}