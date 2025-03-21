plugins {
    springBootConventions
}

dependencies {
    implementation(project(Modules.Service.Memo.Core.path))
    implementation(project(Modules.Service.AuthUser.Adaptors.InAcl.path))
    implementation(project(Modules.Libs.CoreKernel.path))
}
