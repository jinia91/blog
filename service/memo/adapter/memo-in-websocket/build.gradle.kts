
plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.memo"

dependencies {
    api("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation(project(Modules.Service.Memo.Domain.path))
    implementation(project(Modules.Service.Memo.Application.path))
    implementation(project(Modules.Libs.CoreKernel.path))
}
