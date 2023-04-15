import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.8.0"
    val springBootVersion = "3.0.5"
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    id("org.flywaydb.flyway") version "5.2.4"
}

group = "kr.co.jiniaslog"
version = "2.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

var leafModules = mutableListOf<Project>()

// add system's LeafModule
findProject(":system")?.let {
    it.subprojects.forEach { subProject ->
        val isLeafModule = subProject.subprojects.isEmpty()
        if (isLeafModule) {
            dependencies {
                implementation(project(subProject.path))
            }
            leafModules.add(subProject)
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")

    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.3.Final")
    kaptTest("org.mapstruct:mapstruct-processor:1.5.3.Final")
    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
    testImplementation("org.testcontainers:mysql:1.17.6")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + "-Xjsr305=strict"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("testAll") {
    dependsOn(leafModules.map { it.tasks.named("test") })
    dependsOn(tasks.test)
}

tasks.getByName("jar") {
    enabled = false
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    enabled = true
}
