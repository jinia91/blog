import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * System's Global Convention
 * - Kotlin version 1.8.10
 * - Kotlinter version 3.14.0
 */

plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
    id("org.jmailen.kotlinter") version "3.14.0" apply false
}

group = "kr.co.jiniaslog"

val jar: Jar by tasks
jar.enabled = true
jar.archiveFileName.set("${project.name}.jar")

val localLib = ":system:lib"

dependencies {
    subprojects.forEach { subproject ->
        val isSystemChildProject = subproject.path.startsWith(":system:")
        if (isSystemChildProject && subproject.name != localLib) {
            api(project(subproject.path))
        }
    }
    api(project(localLib))
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jmailen.kotlinter")

    repositories {
        mavenCentral()
    }

    afterEvaluate {
        // recursive dependency
        val isSubModule = project.path.startsWith(":system:")
        if (isSubModule) {
            project.subprojects.forEach { subproject ->
                dependencies {
                    api(project(subproject.path))
                }
            }
        }

        // adding global dependency
        if(project.path != localLib) {
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

        tasks.withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "17"
                freeCompilerArgs = freeCompilerArgs + "-Xjsr305=strict"
            }
        }

        val jar: Jar by tasks
        jar.enabled = true
        jar.archiveFileName.set("${project.name}.jar")

        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
}