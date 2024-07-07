plugins {
    springBootConventions
    `kotlin-jpa`
}

group = "kr.co.jiniaslog.user"

dependencies {
    implementation(project(Modules.Service.AuthUser.Application.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.RdbKernel.path))
    api(libs.spring.boot.starter.data.jpa)
    implementation(libs.h2)
    implementation(libs.spring.boot.starter.spy)
}
