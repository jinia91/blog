plugins {
    springBootConventions
    `kotlin-jpa`
    koverFeatures
}

group = "kr.co.jiniaslog.blog"

dependencies {
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Service.MessageNexus.path))
    implementation(project(Modules.Libs.JpaKernel.path))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("mysql:mysql-connector-java:8.0.32")
    implementation("org.springframework.boot:spring-boot-starter")

    testImplementation(testFixtures(project(Modules.Libs.CoreKernel.path)))
    testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter-kotlin:1.0.12")

}
