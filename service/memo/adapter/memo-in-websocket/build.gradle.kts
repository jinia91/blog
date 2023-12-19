
plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.memo"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation(project(":service:memo:memo-core"))
    implementation(project(":libs:core-kernel"))
}