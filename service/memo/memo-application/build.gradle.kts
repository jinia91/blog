plugins {
    conventions
}

group = "kr.co.jiniaslog.memo"

dependencies {
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Service.Memo.Core.path))
    implementation(project(Modules.Service.MessageNexus.path))
}
