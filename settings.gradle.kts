rootProject.name = "Jinia's Log"

// Enable parallel execution for tasks
gradle.startParameter.isParallelProjectExecutionEnabled = true

include(
    /**
     * ################
     * ## Jinia's Log##
     * ################
      */

    // framework 의존성을 가지고 애플리케이션을 실행시키는 main module
    "service:boot",
    // 서비스 공용 라이브러리
    "service:libs:core_kernel",
    "service:libs:messaging_kernel",
    "service:libs:rdb_kernel",
    "service:libs:http_kernel",
    "service:blog:core",
    "service:user:core",
    "service:comment",
    "service:media",
    "service:seo",
)