import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    springBootConventions
    id("org.flywaydb.flyway") version "5.2.4"
}

group = "kr.co.jiniaslog"
version = "2.0.0"

var leafModules = mutableListOf<Project>()

// add system's LeafModule
findProject(":system")?.let {
    it.subprojects.forEach { subProject ->
        val isLeafModule = subProject.subprojects.isEmpty()
        if (isLeafModule) {
            dependencies {
                implementation(project(subProject.path))
            }
            leafModules.add(subProject)
        }
    }
}

dependencies {
    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
    testImplementation("org.testcontainers:mysql:1.17.6")
    testImplementation("io.rest-assured:rest-assured:5.3.0")
}

tasks.register("testAll") {
    dependsOn(leafModules.map { it.tasks.named("test") })
    dependsOn(tasks.test)
}

tasks.getByName("jar") {
    enabled = false
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    enabled = true
}
