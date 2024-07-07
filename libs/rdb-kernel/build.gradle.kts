plugins {
    springBootConventions
    kotlin("plugin.jpa")
}

dependencies {
    api(project(Modules.Libs.CoreKernel.path))
    api(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.spy)
}
