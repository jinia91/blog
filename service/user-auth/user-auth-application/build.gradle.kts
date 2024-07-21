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

    testImplementation(testFixtures(project(Modules.Libs.CoreKernel.path)))
    testImplementation(testFixtures(project(Modules.Service.AuthUser.Application.path)))

    testFixturesApi(project(Modules.Libs.CoreKernel.path))
}
