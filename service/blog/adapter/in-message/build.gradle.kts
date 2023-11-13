plugins {
    springBootConventions
}

dependencies {
    implementation(project(":service:blog:core"))
    implementation(project(":libs:core-kernel"))
    implementation(project(":service:message-nexus"))
    implementation("org.springframework.integration:spring-integration-webflux")
}