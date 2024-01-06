plugins {
    springBootConventions
}

val libs = mutableListOf(
    project(Modules.Libs.GlobalLogging.path),
    project(Modules.Libs.SnowflakeIdGenerator.path),
)

val blogService = mutableListOf(
    project(Modules.Service.Blog.Core.path),
)

val memoService = mutableListOf(
    project(Modules.Service.Memo.Core.path),
    project(Modules.Service.Memo.ReadOnly.path),
    project(Modules.Service.Memo.Adaptors.InHttp.path),
    project(Modules.Service.Memo.Adaptors.InWebsocket.path),
    project(Modules.Service.Memo.Adaptors.Persistence.path),
)

val mediaService = mutableListOf(
    project(Modules.Service.Media.Core.path),
    project(Modules.Service.Media.Adaptors.InHttp.path),
    project(Modules.Service.Media.Adaptors.OutGithub.path),
)

val authUserService = mutableListOf(
    project(Modules.Service.AuthUser.Core.path),
    project(Modules.Service.AuthUser.Infra.path),
    project(Modules.Service.AuthUser.Adaptors.OutGoogle.path),
    project(Modules.Service.AuthUser.Adaptors.InHttp.path),
    project(Modules.Service.AuthUser.Adaptors.Persistence.path),
)

var moduleBlocks = mutableListOf<Project>()
    .apply {
        addAll(libs)
        addAll(blogService)
        addAll(memoService)
        addAll(mediaService)
        addAll(authUserService)
    }

var integrationTest = mutableListOf(
    "org.testcontainers:testcontainers:1.19.3",
    "org.testcontainers:junit-jupiter:1.19.3",
    "org.testcontainers:neo4j:1.19.3",
    "io.kotest.extensions:kotest-extensions-testcontainers:2.0.2",
    "io.rest-assured:rest-assured:5.4.0",
)

dependencies {
    moduleBlocks.forEach {
        implementation(it)
    }
    integrationTest.forEach {
        testImplementation(it)
    }
    testImplementation(testFixtures(project(Modules.Service.Memo.Adaptors.Persistence.path)))
}

tasks.register("testAll") {
    dependsOn(moduleBlocks.map { it.tasks.named("test") })
    dependsOn(tasks.test)
}

tasks.bootJar {
    enabled = true
}