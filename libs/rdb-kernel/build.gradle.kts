plugins {
    springBootConventions
    `kotlin-kapt`
    kotlin("plugin.jpa")
}

dependencies {
    api(project(Modules.Libs.CoreKernel.path))
    api(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.spy)
    implementation(libs.mysql.connector.j)
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
}
