plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:core-kernel"))
    implementation("org.springframework.integration:spring-integration-webflux")
}