plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

java.sourceCompatibility = JavaVersion.VERSION_17

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.bundles.kotlin.plugins)
    implementation(libs.bundles.spring.plugins)
    implementation(libs.kotlinter.gradle) {
        exclude(group = "io.github.microutils", module = "kotlin-logging-jvm")
    }
}
