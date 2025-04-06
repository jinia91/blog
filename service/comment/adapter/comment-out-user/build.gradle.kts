plugins {
    springBootConventions
}

dependencies {
    implementation(project(Modules.Service.Comment.Core.path))
    implementation(project(Modules.Service.AuthUser.Adaptors.InAcl.path))
    implementation(project(Modules.Libs.CoreKernel.path))
}
