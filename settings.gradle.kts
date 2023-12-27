rootProject.name = "Jinia's Log"

include(
    /**
     * ################
     * ## Jinia's Log##
     * ################
     */

    // framework 의존성을 가지고 순수하게 애플리케이션을 실행시키는 main module
    "mains:monolith-main",

    // 서비스 공용 라이브러리
    "libs:core-kernel",
    "libs:messaging-handler-generator",
    "libs:rdb-kernel",
    "libs:http-kernel",
    "libs:messaging-kernel",
    "libs:global-logging",
    "libs:snowflake-id-generator",

    // blog
    "service:blog:blog-core",
    "service:blog:adapter:blog-out-rdb",
    "service:blog:adapter:blog-in-http",
    "service:blog:adapter:blog-in-message",
    "service:blog:adapter:blog-acl-user",

    // memo
    "service:memo:memo-core",
    "service:memo:memo-queries",
    "service:memo:adapter:memo-in-http",
    "service:memo:adapter:memo-in-websocket",
    "service:memo:adapter:memo-in-message",
    "service:memo:adapter:memo-in-batch",
    "service:memo:adapter:memo-out-persistence",

    //media
    "service:media:media-core",
    "service:media:adapter:media-in-http",
    "service:media:adapter:media-out-github",

    "service:user:core",
    "service:comment",
    "service:media",
    "service:seo",
    "service:message-nexus",
)

