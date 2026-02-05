plugins {
    springBootConventions
    `kotlin-jpa`
    `kotlin-kapt`
}

group = "kr.co.jiniaslog.ai"

dependencies {
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.RdbKernel.path))
    implementation(project(Modules.Service.MessageNexus.path))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.core)
    implementation(libs.spring.boot.starter.validation)

    // Spring AI - LLM 비즈니스 로직에서 직접 사용 (Google GenAI Gemini)
    implementation(platform(libs.spring.ai.bom))
    implementation(libs.spring.ai.google.genai.starter)

    // QueryDSL - Q클래스 생성
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")

    testImplementation(testFixtures(project(Modules.Libs.CoreKernel.path)))
    testFixturesApi(project(Modules.Libs.CoreKernel.path))
}
