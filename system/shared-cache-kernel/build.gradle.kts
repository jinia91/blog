plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
}

repositories {
    mavenCentral()
}

dependencies{
    implementation("org.redisson:redisson-spring-boot-starter:3.17.7")
}
