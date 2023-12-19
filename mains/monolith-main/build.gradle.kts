plugins {
    springBootConventions
}

// add service's modules
var moduleBlocks = mutableListOf<Project>()

val infra = mutableListOf(
    project(":infra:monolith-infra"),
).also {
    moduleBlocks.addAll(it)
}

val blog = mutableListOf(
    project(":service:blog:blog-core"),
).also {
    moduleBlocks.addAll(it)
}


val memo = mutableListOf(
    project(":service:memo:adapter:memo-persistence"),
    project(":service:memo:adapter:memo-in-http"),
    project(":service:memo:adapter:memo-in-websocket"),
    project(":service:memo:memo-core"),
).also {
    moduleBlocks.addAll(it)
}

val media = mutableListOf(
    project(":service:media:media-core"),
    project(":service:media:adapter:media-out-github"),
    project(":service:media:adapter:media-in-http"),
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

tasks.bootJar {
    enabled = true
}