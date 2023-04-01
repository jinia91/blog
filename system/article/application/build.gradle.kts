val jar: Jar by tasks
jar.enabled = true
jar.archiveFileName.set("${project.name}.jar")

dependencies {
    api(project(":system:article:domain"))
}
