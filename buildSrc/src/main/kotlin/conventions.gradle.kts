import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("org.jmailen.kotlinter")
}

allOpen {
    annotation("kr.co.jiniaslog.shared.core.context.CustomComponent")
}

repositories {
    mavenCentral()
}

group = "kr.co.jiniaslog"

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

val jar: Jar by tasks
jar.enabled = true
jar.archiveFileName.set("${project.parent?.name}-${project.name}.jar")

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core:5.7.2")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.1")
}
