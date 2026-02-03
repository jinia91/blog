plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.ai"

dependencies {
    implementation(project(Modules.Service.AiSecondBrain.Core.path))
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Libs.RdbKernel.path))

    implementation(platform(libs.spring.ai.bom))
    implementation(libs.spring.ai.openai.starter)
    implementation(libs.spring.ai.chroma.store)
}
