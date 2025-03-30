plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.blog"

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(project(Modules.Service.Blog.Core.path))
    implementation(project(Modules.Libs.CoreKernel.path))
}
