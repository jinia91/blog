
plugins {
    springBootConventions
}

dependencies {
    api(project(":libs:core-kernel"))
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.integration:spring-integration-webflux")
}
