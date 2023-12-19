plugins {
    conventions
}

dependencies {
    implementation(project(":libs:core-kernel"))
    implementation(project(":service:message-nexus"))
    testImplementation(testFixtures(project(":libs:core-kernel")))
}