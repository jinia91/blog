
plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.user"

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(project(Modules.Service.AuthUser.Application.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.RestKernel.path))
    implementation(libs.springdoc.openapi.ui)
    implementation(libs.spring.boot.starter.security)
}
