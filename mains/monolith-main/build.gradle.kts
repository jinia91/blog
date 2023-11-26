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

val blog = listOf(
    project(":service:blog:core"),
).also {
//    moduleBlocks.addAll(it)
}

val memo = listOf(
    project(":service:memo:core"),
    project(":service:memo:adapter:out-file"),
    project(":service:memo:adapter:out-rdb"),
    project(":service:memo:adapter:in-http"),
    project(":service:memo:adapter:in-websocket"),
).also {
    moduleBlocks.addAll(it)
}

dependencies {
    moduleBlocks.forEach {
        implementation(it)
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