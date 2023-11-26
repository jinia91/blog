plugins {
    springBootConventions
}

dependencies {
    implementation(project(":service:blog:core"))
    implementation(project(":libs:core-kernel"))
    implementation(project(":libs:http-kernel"))
}