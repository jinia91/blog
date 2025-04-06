plugins {
    springBootConventions
    `kotlin-jpa`
    `kotlin-kapt`
}

group = "kr.co.jiniaslog.comment"

dependencies {
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Service.MessageNexus.path))
    implementation(project(Modules.Libs.RdbKernel.path))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.core)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)

    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")

    testImplementation(testFixtures(project(Modules.Libs.CoreKernel.path)))
    testFixturesApi(project(Modules.Libs.CoreKernel.path))
}
