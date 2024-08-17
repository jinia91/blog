plugins {
    springBootConventions
}

dependencies {
    api(project(Modules.Libs.CoreKernel.path))
    implementation(libs.spring.boot.starter.web)
}
