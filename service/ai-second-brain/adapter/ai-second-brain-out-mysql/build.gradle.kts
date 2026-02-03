plugins {
    springBootConventions
    `kotlin-jpa`
    `kotlin-kapt`
}

group = "kr.co.jiniaslog.ai"

dependencies {
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.RdbKernel.path))
    implementation(project(Modules.Service.AiSecondBrain.Core.path))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
}
