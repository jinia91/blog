val jar: Jar by tasks
jar.enabled = true
jar.archiveFileName.set("${project.name}.jar")

dependencies {
    api(project(":system:blog-core:domain"))
//    implementation(project(":system:shared-application-infra-kernel"))
}
