plugins {
    conventions
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
}

dependencies {
    implementation(project(":libs:core-kernel"))
    implementation(project(":libs:messaging-handler-generator"))
    ksp(project(":libs:messaging-handler-generator"))
}
