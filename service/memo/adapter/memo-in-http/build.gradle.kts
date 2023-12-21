
plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.memo"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":service:memo:memo-core"))
    implementation(project(":libs:core-kernel"))
    implementation(project(":libs:http-kernel"))
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
}