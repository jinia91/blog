
plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.memo"

dependencies {
    implementation(libs.spring.boot.starter.websocket)
    implementation(libs.spring.boot.starter.validation)
    implementation(project(Modules.Service.Memo.Domain.path))
    implementation(project(Modules.Service.Memo.Application.path))
    implementation(project(Modules.Libs.CoreKernel.path))
}
