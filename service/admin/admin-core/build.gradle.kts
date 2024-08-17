plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.admin"

dependencies {
    implementation(libs.spring.boot.starter.web)
    api(project(Modules.Service.AuthUser.Application.path))
    api(project(Modules.Service.AuthUser.Domain.path))
    implementation(project(Modules.Libs.CoreKernel.path))
}
