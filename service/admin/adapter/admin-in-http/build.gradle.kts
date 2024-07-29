
plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.admin"

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(project(Modules.Service.Admin.Core.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(libs.springdoc.openapi.ui)
}
