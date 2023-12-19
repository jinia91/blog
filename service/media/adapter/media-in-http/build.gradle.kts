
plugins {
    springBootConventions
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":service:media:media-core"))
    implementation(project(":libs:core-kernel"))
    implementation(project(":libs:http-kernel"))
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
}