import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * System's Global Convention
 * - Kotlin version 1.8.0
 */

plugins {
    val kotlinVersion = "1.8.0"
    kotlin("jvm") version kotlinVersion
    id("org.jmailen.kotlinter") version "3.14.0" apply false
}

group = "kr.co.jiniaslog"
val jar: Jar by tasks
jar.enabled = true
jar.archiveFileName.set("${project.name}.jar")


dependencies {
    subprojects.forEach { subproject ->
        val isSystemChildProject = subproject.path.startsWith(":system:")
        if (isSystemChildProject) {
            implementation(project(subproject.path))
        }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jmailen.kotlinter")

    repositories {
        mavenCentral()
    }

    val localLib = ":system:lib"

    if(this.name != localLib)
    dependencies {
        api(localLib)
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
}