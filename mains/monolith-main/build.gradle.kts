plugins {
    springBootConventions
    koverReport
    observability
}

/**
 *  Monolith Bootstrap Dependency
 */
val shared = mutableListOf(
    project(Modules.Libs.SnowflakeIdGenerator.path),
    project(Modules.Libs.RdbKernel.path),
    project(Modules.Libs.RestKernel.path),
    project(Modules.Service.MessageNexus.path),
)

val blogService = mutableListOf(
    project(Modules.Service.Blog.Core.path),
    project(Modules.Service.Blog.Adaptors.InHttp.path),
    project(Modules.Service.Blog.Adaptors.InBatch.path),
    project(Modules.Service.Blog.Adaptors.InAcl.path),
    project(Modules.Service.Blog.Adaptors.InWebsocket.path),
    project(Modules.Service.Blog.Adaptors.OutUser.path),
    project(Modules.Service.Blog.Adaptors.OutMemo.path),
    project(Modules.Service.Blog.Adaptors.OutMySql.path),
    project(Modules.Service.Blog.Adaptors.OutEs.path),
)

val memoService = mutableListOf(
    project(Modules.Service.Memo.Core.path),
    project(Modules.Service.Memo.Adaptors.InHttp.path),
    project(Modules.Service.Memo.Adaptors.InWebsocket.path),
    project(Modules.Service.Memo.Adaptors.InBatch.path),
    project(Modules.Service.Memo.Adaptors.OutMySql.path),
    project(Modules.Service.Memo.Adaptors.OutUser.path),
)

val mediaService = mutableListOf(
    project(Modules.Service.Media.Domain.path),
    project(Modules.Service.Media.Application.path),
    project(Modules.Service.Media.Adaptors.InHttp.path),
    project(Modules.Service.Media.Adaptors.OutGithub.path),
)

val authUserService = mutableListOf(
    project(Modules.Service.AuthUser.Domain.path),
    project(Modules.Service.AuthUser.Application.path),
    project(Modules.Service.AuthUser.Adaptors.OutGoogle.path),
    project(Modules.Service.AuthUser.Adaptors.InHttp.path),
    project(Modules.Service.AuthUser.Adaptors.OutCache.path),
    project(Modules.Service.AuthUser.Adaptors.OutMySql.path),
)

val seoService = mutableListOf(
    project(Modules.Service.Seo.Core.path),
)

val commentService = mutableListOf(
    project(Modules.Service.Comment.Core.path),
    project(Modules.Service.Comment.Adaptors.InHttp.path),
    project(Modules.Service.Comment.Adaptors.OutMySql.path),
    project(Modules.Service.Comment.Adaptors.OutUser.path),
    project(Modules.Service.Comment.Adaptors.OutBlog.path),
)

val aiSecondBrainService = mutableListOf(
    project(Modules.Service.AiSecondBrain.Core.path),
    project(Modules.Service.AiSecondBrain.Adaptors.InHttp.path),
    project(Modules.Service.AiSecondBrain.Adaptors.InMessage.path),
    project(Modules.Service.AiSecondBrain.Adaptors.OutChromaDb.path),
    project(Modules.Service.AiSecondBrain.Adaptors.OutMySql.path),
    project(Modules.Service.AiSecondBrain.Adaptors.OutMemo.path),
)

var moduleBlocks = mutableListOf<Project>()
    .apply {
        addAll(shared)
        addAll(blogService)
        addAll(memoService)
        addAll(mediaService)
        addAll(authUserService)
        addAll(seoService)
        addAll(commentService)
        addAll(aiSecondBrainService)
    }

val bootLib = mutableListOf(
    libs.spring.boot.starter.security,
    libs.spring.boot.starter.web,
    libs.spring.boot.starter.actuator,
    libs.mysql.connector.j,
    libs.h2
)

var integrationTestLib = mutableListOf(
    libs.testcontainers,
    libs.testcontainers.junit5,
    libs.testcontainers.mysql,
    libs.testcontainers.redis,
    libs.restassured,
    libs.wiremock,
    libs.mockkbean,
    libs.spring.boot.starter.websocket,
    libs.spring.ai.google.genai.embedding.starter,
    libs.spring.ai.chroma.store
)

dependencies {
    // project
    moduleBlocks.forEach {
        implementation(it)
        kover(it)
    }

    // Spring AI BOM for test
    testImplementation(platform(libs.spring.ai.bom))

    testImplementation(testFixtures(project(Modules.Service.Memo.Core.path)))
    testImplementation(testFixtures(project(Modules.Service.AuthUser.Application.path)))
    testImplementation(testFixtures(project(Modules.Service.Blog.Core.path)))
    testImplementation(testFixtures(project(Modules.Service.Comment.Core.path)))
    testImplementation(testFixtures(project(Modules.Service.AiSecondBrain.Core.path)))
    // external libs
    bootLib.forEach {
        implementation(it)
    }
    integrationTestLib.forEach {
        testImplementation(it)
    }
    testImplementation(kotlin("test"))
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

koverReport {
    verify {
        filters {
            excludes {
                classes(
                    "kr.co.jiniaslog.App",
                    "kr.co.jiniaslog.AppKt",
                    "*.Q*", // 어노테이션, 패키지 필터가 적용이 안되서 q 파일 임시로 제외
                    "*.*TestFixtures",
                )
            }
        }
    }
    filters {
        excludes {
            classes(
                "kr.co.jiniaslog.App",
                "kr.co.jiniaslog.AppKt",
                "*.Q*",
                "*.*TestFixtures",
            )
        }
    }
}
