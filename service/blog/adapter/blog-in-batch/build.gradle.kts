plugins {
    springBootConventions
}

dependencies {
    implementation(project(Modules.Service.Blog.Core.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.RestKernel.path))
    implementation(libs.spring.boot.starter.core)
}
