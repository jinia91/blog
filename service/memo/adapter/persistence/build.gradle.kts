plugins {
    springBootConventions
}

dependencies {
    implementation(project(":service:memo:core"))
    implementation(project(":libs:core-kernel"))
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
}