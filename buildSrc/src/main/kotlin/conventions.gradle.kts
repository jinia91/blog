import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("org.jmailen.kotlinter")
    jacoco
}

allOpen {
    annotation("kr.co.jiniaslog.shared.core.context.CustomComponent")
}

repositories {
    mavenCentral()
}

group = "kr.co.jiniaslog"
version = "2.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


tasks.findByName("bootJar")?.let {
    it.enabled = false
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    enabled = true
}

kotlinter {
    reporters = arrayOf("checkstyle", "plain")
}

val jar: Jar by tasks
jar.enabled = true
jar.archiveFileName.set("${project.parent?.name}-${project.name}.jar")

dependencies {
    // core
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")

    // test dependencies
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core:5.7.2")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("ch.qos.logback:logback-classic:1.4.5")
}

// jacoco setting

jacoco {
    toolVersion = "0.8.7"
}

tasks.test {
    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(file("$buildDir/jacoco/jacoco.exec"))
    }
    finalizedBy("jacocoTestReport")
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.required.set(true)
    }
        finalizedBy("jacocoTestCoverageVerification")

    classDirectories.setFrom(
        fileTree(project.buildDir) {
            exclude(
                "**/Q*.*",
                "**/*Test.*"
            )
            include(
                "**/classes/**/main/**"
            )
        }
    )
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = "BRANCH"
                minimum = "0.7".toBigDecimal()
            }

            limit {
                counter = "LINE"
                minimum = "0.7".toBigDecimal()
            }
        }
    }
    classDirectories.setFrom(
        fileTree(project.buildDir) {
            exclude(
                "**/Q*.*",
                "**/*Test.*"
            )
            include(
                "**/classes/**/main/**"
            )
        }
    )
}
