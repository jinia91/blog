
plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.memo"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(Modules.Service.AuthUser.Core.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.HttpKernel.path))
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
}