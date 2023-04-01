import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * System's Global Convention
 * - Kotlin version 1.8.0
 */

plugins {
    val kotlinVersion = "1.8.0"
    kotlin("jvm") version kotlinVersion
}

group = "kr.co.jiniaslog"

dependencies {
    val isSystemProject = project.path.startsWith(":system")
    if (isSystemProject) {
        dependencies {
            implementation(project(path))
        }
    }

}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = freeCompilerArgs + "-Xjsr305=strict"
        }
    }
}

repositories {
    mavenCentral()
}