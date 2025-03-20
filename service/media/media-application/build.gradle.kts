plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.media"

dependencies {
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Service.Media.Domain.path))
    implementation(project(Modules.Service.MessageNexus.path))
}
