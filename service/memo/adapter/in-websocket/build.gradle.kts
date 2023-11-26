
plugins {
    springBootConventions
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation(project(":service:memo:core"))
    implementation(project(":libs:core-kernel"))
}