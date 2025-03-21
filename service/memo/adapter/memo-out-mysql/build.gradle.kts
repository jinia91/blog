plugins {
    springBootConventions
    `kotlin-jpa`
    `kotlin-kapt`
}

dependencies {
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.RdbKernel.path))
    implementation(project(Modules.Service.Memo.Core.path))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.4")
}
