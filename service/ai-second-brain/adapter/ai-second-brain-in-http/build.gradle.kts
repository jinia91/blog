plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.ai"

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.springdoc.openapi.ui)
    implementation(project(Modules.Service.AiSecondBrain.Core.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.RestKernel.path))
}
