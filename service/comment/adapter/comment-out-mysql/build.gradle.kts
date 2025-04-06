plugins {
    springBootConventions
    `kotlin-jpa`
}

dependencies {
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.RdbKernel.path))
    implementation(project(Modules.Service.Comment.Core.path))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
}
