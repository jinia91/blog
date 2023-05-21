plugins {
    kotlin("plugin.allopen") version "1.8.0"
}

allOpen {
    annotation("kr.co.jiniaslog.shared.core.context.UseCaseInteractor")
}

val jar: Jar by tasks
jar.enabled = true
jar.archiveFileName.set("${project.name}.jar")

dependencies {
    api(project(":system:blog-core:domain"))
    testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter:0.5.0")
}
