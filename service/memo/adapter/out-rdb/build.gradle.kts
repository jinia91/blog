plugins {
    springBootConventions
}

dependencies {
    implementation(project(":service:memo:core"))
    implementation(project(":libs:core-kernel"))
    implementation(project(":libs:rdb-kernel"))
}