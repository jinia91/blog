val jar: Jar by tasks
jar.enabled = true
jar.archiveFileName.set("${project.name}.jar")

dependencies{
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.1")
}