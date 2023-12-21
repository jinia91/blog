plugins {
    conventions
}

group = "kr.co.jiniaslog.memo"

dependencies {
    implementation(project(":libs:core-kernel"))
    implementation(project(":service:message-nexus"))
    testImplementation(testFixtures(project(":libs:core-kernel")))
}

tasks.test {
    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(file("$buildDir/jacoco/jacoco.exec"))
    }
    finalizedBy("jacocoTestReport")
}
