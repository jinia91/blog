plugins {
    springBootConventions
    kotlin("plugin.jpa")
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("mysql:mysql-connector-java:8.0.32")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")
}