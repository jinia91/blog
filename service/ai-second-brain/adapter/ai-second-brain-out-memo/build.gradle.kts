plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.ai"

dependencies {
    implementation(project(Modules.Service.AiSecondBrain.Core.path))
    implementation(project(Modules.Service.Memo.Core.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.RdbKernel.path))
    implementation(libs.spring.boot.starter.core)
}
