import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    id("java-test-fixtures")
    id("org.jetbrains.kotlinx.kover")
}

allOpen {
    annotation("kr.co.jiniaslog.shared.core.annotation.CustomComponent")
}

noArg {
    annotation("kr.co.jiniaslog.shared.core.annotation.NoArgConstructor")
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    test {
        useJUnitPlatform()
        val activeProfile =
            if (project.hasProperty("profile")) {
                project.property("profile").toString()
            } else {
                "default"
            }

        systemProperty("spring.profiles.active", activeProfile)
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
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.logging)
    implementation(libs.kotlin.jackson)
    implementation(libs.kotlin.jackson.binding)

    testImplementation(libs.junit.param)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertion)
    testImplementation(libs.mockk)
    testImplementation(libs.logback.classic)

    testFixturesApi(libs.kotest.runner.junit5)
    testFixturesApi(libs.kotest.assertion)
}
