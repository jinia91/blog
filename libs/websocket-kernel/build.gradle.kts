
plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.shared"

dependencies {
    implementation(libs.spring.boot.starter.websocket)
    implementation(libs.spring.boot.starter.validation)
    implementation(project(Modules.Libs.CoreKernel.path))
}
