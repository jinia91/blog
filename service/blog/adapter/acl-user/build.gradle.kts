plugins {
    springBootConventions
}

dependencies {
    implementation(project(":service:blog:core"))
    implementation(project(":service:user:core"))
    implementation(project(":libs:core-kernel"))
}