plugins {
    springBootConventions
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
}

dependencies {
    implementation(project(":libs:core-kernel"))
    implementation(project(":libs:messaging-kernel"))
    implementation(project(":service:message-nexus"))
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.integration:spring-integration-webflux")
    ksp(project(":libs:messaging-kernel"))
}

tasks.withType<org.jmailen.gradle.kotlinter.tasks.ConfigurableKtLintTask>().configureEach {
    exclude { it.file.path.contains("build/generated") }
}