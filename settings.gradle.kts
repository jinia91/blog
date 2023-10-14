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

    "system:shared:core_kernel",
    "system:shared:messaging_kernel",
    "system:shared:rdb_kernel",
    "system:shared:http_kernel",

    "system:blog:core",
    "system:user:core",

    "system:media",
    "system:seo",
    "system:infra",
)