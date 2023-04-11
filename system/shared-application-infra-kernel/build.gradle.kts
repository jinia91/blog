plugins {
    val kotlinVersion = "1.8.10"
    val springBootVersion = "3.0.5"
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
}

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter:3.0.5")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.0.5")
    implementation("org.springframework.integration:spring-integration-core:6.0.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
