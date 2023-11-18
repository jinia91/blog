
dependencies {
    implementation(project(":libs:core-kernel"))
    implementation(project(":service:message-nexus"))
    implementation("org.bitbucket.cowwoc:diff-match-patch:1.2")
    testImplementation(testFixtures(project(":libs:core-kernel")))
}

tasks.test {
    extensions.configure(JacocoTaskExtension::class) {
        destinationFile = file("$buildDir/jacoco/jacoco.exec")
    }
    finalizedBy("jacocoTestReport")
}
