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
    "libs:rest-kernel",
    "libs:websocket-kernel",
    "libs:messaging-kernel",
    "libs:snowflake-id-generator",

    // blog
    "service:blog:blog-core",
    "service:blog:adapter:blog-in-http",
    "service:blog:adapter:blog-in-acl",
    "service:blog:adapter:blog-in-batch",
    "service:blog:adapter:blog-in-message",
    "service:blog:adapter:blog-in-websocket",
    "service:blog:adapter:blog-out-rdb",
    "service:blog:adapter:blog-out-user",
    "service:blog:adapter:blog-out-memo",
    "service:blog:adapter:blog-out-mysql",
    "service:blog:adapter:blog-out-es",

    // memo
    "service:memo:memo-core",
    "service:memo:adapter:memo-in-http",
    "service:memo:adapter:memo-in-acl",
    "service:memo:adapter:memo-in-websocket",
    "service:memo:adapter:memo-in-message",
    "service:memo:adapter:memo-in-batch",
    "service:memo:adapter:memo-out-mysql",
    "service:memo:adapter:memo-out-user",

    // media
    "service:media:media-domain",
    "service:media:media-application",
    "service:media:adapter:media-in-http",
    "service:media:adapter:media-out-github",

    // user-auth
    "service:user-auth:user-auth-domain",
    "service:user-auth:user-auth-application",
    "service:user-auth:adapter:user-auth-in-http",
    "service:user-auth:adapter:user-auth-in-acl",
    "service:user-auth:adapter:user-auth-out-google",
    "service:user-auth:adapter:user-auth-out-mysql",
    "service:user-auth:adapter:user-auth-out-cache",

    // comment
    "service:comment",
    "service:comment:adapter:comment-in-http",
    "service:comment:comment-core",
    "service:comment:adapter:comment-out-mysql",
    "service:comment:adapter:comment-out-blog",
    "service:comment:adapter:comment-out-user",

    // admin
    "service:admin:admin-core",
    "service:admin:adapter:admin-in-http",

    "service:seo",
    "service:message-nexus",
)
