plugins {
    springBootConventions
}

dependencies {
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Service.Blog.Core.path))
    implementation(libs.spring.data.elasticsearch)
}
