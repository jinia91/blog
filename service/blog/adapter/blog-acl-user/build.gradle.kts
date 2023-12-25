plugins {
    springBootConventions
}

group = "kr.co.jiniaslog.blog"

dependencies {
    implementation(project(":service:blog:blog-core"))
    implementation(project(":service:user:core"))
    implementation(project(":libs:core-kernel"))
}