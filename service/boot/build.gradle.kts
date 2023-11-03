plugins {
    springBootConventions
}

var leafModules = mutableListOf<Project>()
// add system's LeafModule
parent?.let {
    it.subprojects.forEach { subProject ->
        val isLeafModule = subProject.subprojects.isEmpty()
        if (isLeafModule && subProject.name != "boot") {
            dependencies {
                implementation(project(subProject.path))
            }
            leafModules.add(subProject)
        }
    }
}

tasks.register("testAll") {
    dependsOn(leafModules.map { it.tasks.named("test") })
    dependsOn(tasks.test)
}

tasks.getByName("jar") {
    enabled = false
}

tasks.bootJar {
    enabled = true
}