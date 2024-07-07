plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.memo"

dependencies {
    implementation(libs.spring.boot.starter.data.neo4j)
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Service.Memo.Application.path))
    implementation(project(Modules.Service.Memo.Domain.path))
}