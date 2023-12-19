rootProject.name = "Jinia's Log"

// Enable parallel execution for tasks
gradle.startParameter.isParallelProjectExecutionEnabled = true

include(
    /**
     * ################
     * ## Jinia's Log##
     * ################
      */

    // framework 의존성을 가지고 순수하게 애플리케이션을 실행시키는 main module
    "mains:monolith-main",

    // framework의 실제 필요한 infra 모듈
    "infra:monolith-infra",

    // 서비스 공용 라이브러리
    "libs:core-kernel",
    "libs:messaging-handler-generator",
    "libs:rdb-kernel",
    "libs:http-kernel",
    "libs:messaging-kernel",

    // blog
    "service:blog:core",
    "service:blog:adapter:out-rdb",
    "service:blog:adapter:in-http",
    "service:blog:adapter:in-message",
    "service:blog:adapter:acl-user",

    // memo
    "service:memo:core",
    "service:memo:adapter:in-http",
    "service:memo:adapter:in-websocket",
    "service:memo:adapter:in-message",
    "service:memo:adapter:in-batch",
    "service:memo:adapter:persistence",

    //media
    "service:media:core",
    "service:media:adapter:in-http",
    "service:media:adapter:out-github",

    "service:user:core",
    "service:comment",
    "service:media",
    "service:seo",
    "service:message-nexus",
)