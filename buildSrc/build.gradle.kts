plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.bundles.kotlin.plugins)
    implementation(libs.bundles.spring.plugins)
    implementation(libs.kover.plugin)
    implementation(libs.kapt.plugin)
}
