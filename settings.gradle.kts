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

    // 서비스
    "service:blog:core",
    "service:blog:adapter:out-rdb",
    "service:blog:adapter:in-http",
    "service:blog:adapter:in-message",
    "service:blog:adapter:acl-user",

    "service:memo:core",

    "service:user:core",
    "service:comment",
    "service:media",
    "service:seo",
    "service:message-nexus",
)