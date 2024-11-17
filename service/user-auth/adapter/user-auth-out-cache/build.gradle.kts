plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.user"

dependencies {
    implementation(project(Modules.Service.AuthUser.Application.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(libs.spring.data.redis)
    implementation(libs.caffeine)
}
