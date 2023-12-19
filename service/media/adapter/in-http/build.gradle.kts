
plugins {
    springBootConventions
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":service:media:core"))
    implementation(project(":libs:core-kernel"))
    implementation(project(":libs:http-kernel"))
}