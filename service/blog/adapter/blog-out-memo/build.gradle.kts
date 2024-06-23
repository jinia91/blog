plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.blog"

dependencies {
    implementation(project(Modules.Service.Blog.Domain.path))
    implementation(project(Modules.Service.Memo.Adaptors.InAcl.path))
    implementation(project(Modules.Libs.CoreKernel.path))
}
