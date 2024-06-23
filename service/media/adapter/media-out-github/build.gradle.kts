plugins {
    springBootConventions
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(Modules.Service.Media.Domain.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation("org.kohsuke:github-api:1.318")
}
