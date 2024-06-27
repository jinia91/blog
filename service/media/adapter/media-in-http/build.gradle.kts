
plugins {
    springBootConventions
}

dependencies {
    implementation(project(Modules.Service.Media.Domain.path))
    implementation(project(Modules.Service.Media.Application.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
}
