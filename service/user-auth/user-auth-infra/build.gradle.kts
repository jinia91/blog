plugins {
    springBootConventions
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.jsonwebtoken:jjwt:0.12.3")
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Service.AuthUser.Core.path))
    testImplementation(testFixtures(project(Modules.Libs.CoreKernel.path)))
}
