plugins {
    conventions
}

dependencies {
    implementation(project(Modules.Libs.CoreKernel.path))
    implementation(project(Modules.Service.MessageNexus.path))
    implementation("org.apache.commons:commons-imaging:1.0.0-alpha5")
    testImplementation(testFixtures(project(Modules.Libs.CoreKernel.path)))
}
