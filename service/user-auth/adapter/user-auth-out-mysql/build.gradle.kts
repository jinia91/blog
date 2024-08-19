plugins {
    springBootConventions
    `kotlin-jpa`
}

group = "kr.co.jiniaslog.user"

dependencies {
    implementation(project(Modules.Service.AuthUser.Application.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.RdbKernel.path))
    implementation(libs.spring.boot.starter.spy)
    implementation(libs.mysql.connector.j)
    api(libs.spring.boot.starter.data.jpa)
}
