plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.rss"

dependencies {
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Service.MessageNexus.path))
    implementation(project(Modules.Service.Blog.Adaptors.InAcl.path))
    implementation(libs.spring.boot.starter.core)
    implementation(libs.spring.boot.starter.validation)
    testImplementation(testFixtures(project(Modules.Libs.CoreKernel.path)))
    testFixturesApi(project(Modules.Libs.CoreKernel.path))
    implementation("com.rometools:rome:1.18.0")
    implementation(libs.spring.boot.starter.web)
}
