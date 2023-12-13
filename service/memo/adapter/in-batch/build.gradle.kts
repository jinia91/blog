
plugins {
    springBootConventions
}

dependencies {
    implementation(project(":service:memo:core"))
    implementation(project(":libs:core-kernel"))
}