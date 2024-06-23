plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.memo"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(Modules.Service.Memo.Domain.path))
    implementation(project(Modules.Service.Memo.Application.path))
    implementation(project(Modules.Libs.CoreKernel.path))
}
