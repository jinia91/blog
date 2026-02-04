plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.ai"

dependencies {
    implementation(project(Modules.Service.AiSecondBrain.Core.path))
    implementation(project(Modules.Libs.CoreKernel.path))

    implementation(platform(libs.spring.ai.bom))
    implementation(libs.spring.ai.chroma.store)
    // Google GenAI embedding 사용
    implementation(libs.spring.ai.google.genai.embedding.starter)
}
