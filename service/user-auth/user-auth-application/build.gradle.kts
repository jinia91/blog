plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.user"

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.websocket)
    api(project(Modules.Service.AuthUser.Domain.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.WebsocketKernel.path))
    implementation("org.springframework.security:spring-security-messaging")
}
