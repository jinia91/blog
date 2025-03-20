plugins {
    springBootConventions
}

dependencies {
    api(project(Modules.Libs.CoreKernel.path))
    api(libs.springdoc.openapi.ui)
    implementation(libs.spring.boot.starter.web)
}
