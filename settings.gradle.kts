rootProject.name = "Jinia's Log"

// Enable parallel execution for tasks
gradle.startParameter.isParallelProjectExecutionEnabled = true

include(
    /**
     * ################
     * ## Jinia's Log##
     * ################
      */
    "boot", // framework 의존성을 가진 main module
    "system",
    "system:blog-core:domain",
    "system:blog-core:application",
    "system:blog-core:adapter:persistence:core-rds",
    "system:blog-core:adapter:http-api",
    "system:blog-core:adapter:acl-user",
    "system:blog-core:adapter:messaging",
    "system:user:domain",
    "system:user:application",
    "system:user:adapter:persistence:user-rds",
    "system:user:adapter:http-api",
    "system:media",
    "system:chat",
    "system:notification",
    "system:seo",
    "system:shared-core-kernel",
    "system:shared-persistence-kernel",
    "system:shared-web-kernel",
    "system:shared-messaging-kernel",
    "system:shared-cache-kernel",
)
