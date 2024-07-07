
plugins {
    springBootConventions
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(project(Modules.Service.Media.Domain.path))
    implementation(project(Modules.Service.Media.Application.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(libs.springdoc.openapi.ui)
}
