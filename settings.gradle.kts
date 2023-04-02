rootProject.name = "Jinia's Log"

include(
    /**
     * ################
     * ## Jinia's Log##
     * ################
      */
    "boot", // framework 의존성을 가진 main module
    "system",
    "system:article:domain",
    "system:article:application",
    "system:article:adapter:persistence:article-rds",
    "system:user",
    "system:category",
    "system:imgupload",
    "system:comment",
    "system:chat",
    "system:notification",
    "system:seo",
    "system:local-lib",
    "system:shared-persistence-kernel",
)
