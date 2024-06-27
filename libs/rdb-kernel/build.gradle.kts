plugins {
    springBootConventions
    kotlin("plugin.jpa")
}

dependencies {
    api(project(Modules.Libs.CoreKernel.path))
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")
}
