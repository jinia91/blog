plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-aop:3.0.5")
    implementation("org.springframework.integration:spring-integration-core:6.0.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
