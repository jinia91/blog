plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.memo"

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.springdoc.openapi.ui)
    implementation(project(Modules.Service.Memo.Core.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.RestKernel.path))
}
