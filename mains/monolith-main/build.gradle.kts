plugins {
    springBootConventions
}

/**
 * ###################################
 * ## Monolith Bootstrap Dependency ##
 * ###################################
 */
// cross-cutting concern for main boot, similar to infrastructure
val shared = mutableListOf(
    project(Modules.Libs.GlobalLogging.path),
    project(Modules.Libs.SnowflakeIdGenerator.path),
    project(Modules.Libs.RestKernel.path),
    project(Modules.Service.MessageNexus.path),
)

val libs = mutableListOf(
    "org.springframework.boot:spring-boot-starter-security",
)

val blogService = mutableListOf(
    project(Modules.Service.Blog.Domain.path),
    project(Modules.Service.Blog.Adaptors.InHttp.path),
    project(Modules.Service.Blog.Adaptors.OutUser.path),
    project(Modules.Service.Blog.Adaptors.OutMemo.path),
)

val memoService = mutableListOf(
    project(Modules.Service.Memo.Domain.path),
    project(Modules.Service.Memo.Application.path),
    project(Modules.Service.Memo.Adaptors.InHttp.path),
    project(Modules.Service.Memo.Adaptors.InWebsocket.path),
    project(Modules.Service.Memo.Adaptors.Persistence.path),
)

val mediaService = mutableListOf(
    project(Modules.Service.Media.Domain.path),
    project(Modules.Service.Media.Adaptors.InHttp.path),
    project(Modules.Service.Media.Adaptors.OutGithub.path),
)

val authUserService = mutableListOf(
    project(Modules.Service.AuthUser.Domain.path),
    project(Modules.Service.AuthUser.Application.path),
    project(Modules.Service.AuthUser.Adaptors.OutGoogle.path),
    project(Modules.Service.AuthUser.Adaptors.InHttp.path),
    project(Modules.Service.AuthUser.Adaptors.Cache.path),
    project(Modules.Service.AuthUser.Adaptors.Persistence.path),
)

var moduleBlocks = mutableListOf<Project>()
    .apply {
        addAll(shared)
        addAll(blogService)
        addAll(memoService)
        addAll(mediaService)
        addAll(authUserService)
    }

var integrationTest = mutableListOf(
    "org.testcontainers:testcontainers:1.19.8",
    "org.testcontainers:junit-jupiter:1.19.8",
    "org.testcontainers:neo4j:1.19.8",
    "org.testcontainers:mysql:1.19.8",
    "io.rest-assured:rest-assured:5.4.0",
    "org.springframework.cloud:spring-cloud-contract-wiremock:4.1.0",
)

dependencies {
    moduleBlocks.forEach {
        implementation(it)
    }
    moduleBlocks.forEach {
        kover(it)
    }
    libs.forEach {
        implementation(it)
    }
    libs.forEach {
        kover(it)
    }
    integrationTest.forEach {
        testImplementation(it)
    }
    testImplementation(testFixtures(project(Modules.Service.Memo.Adaptors.Persistence.path)))
    testImplementation(project(path = Modules.Service.Memo.Domain.path, configuration = "testArtifact"))
    testImplementation(testFixtures(project(Modules.Service.AuthUser.Application.path)))
}

tasks {
    register("testAll") {
        dependsOn(moduleBlocks.map { it.tasks.named("test") })
        dependsOn(test)
    }
    bootJar {
        enabled = true
    }
}
