plugins {
    springBootConventions
}

// add service's modules
var moduleBlocks = mutableListOf<Project>()

val infra = listOf(
    project(":infra:monolith-infra"),
).also {
    moduleBlocks.addAll(it)
}

val lib = listOf(
    project(":libs:core-kernel"),
).also {
    moduleBlocks.addAll(it)
}

val blog = listOf(
    project(":service:blog:core"),
    project(":service:blog:adapter:out-rdb"),
    project(":service:blog:adapter:in-http"),
    project(":service:blog:adapter:in-message"),
).also {
    moduleBlocks.addAll(it)
}

dependencies {
    moduleBlocks.forEach() {
        implementation(project(it.path))
    }
}

tasks.register("testAll") {
    dependsOn(moduleBlocks.map { it.tasks.named("test") })
    dependsOn(tasks.test)
}

tasks.getByName("jar") {
    enabled = false
}

tasks.bootJar {
    enabled = true
}