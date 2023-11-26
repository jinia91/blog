
plugins {
    springBootConventions
}

dependencies {
    api(project(":libs:core-kernel"))
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")
}
