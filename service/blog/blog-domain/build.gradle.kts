plugins {
    springBootConventions
    `kotlin-jpa`
    `kotlin-kapt`
}

group = "kr.co.jiniaslog.blog"

dependencies {
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Service.MessageNexus.path))
    implementation(project(Modules.Libs.RdbKernel.path))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter")

    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    implementation("com.h2database:h2:2.2.224")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation(testFixtures(project(Modules.Libs.CoreKernel.path)))
    testFixturesApi("org.springframework.boot:spring-boot-starter-test")
    testFixturesApi("io.kotest:kotest-assertions-core:5.8.0")
    testFixturesApi("io.mockk:mockk:1.13.8")
    testFixturesApi(project(Modules.Libs.CoreKernel.path))
}
