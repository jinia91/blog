plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.user"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    api(project(Modules.Service.AuthUser.Core.path))
    implementation(project(Modules.Libs.CoreKernel.path))
}
