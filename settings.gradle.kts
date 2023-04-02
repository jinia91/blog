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
    "system:article:adapter:http-api",
    "system:article:adapter:acl-user",
    "system:user:domain",
    "system:user:application",
    "system:user:adapter:persistence:user-rds",
    "system:user:adapter:http-api",
    "system:category",
    "system:imgupload",
    "system:comment",
    "system:chat",
    "system:notification",
    "system:seo",
    "system:local-lib",
    "system:shared-persistence-kernel",
)
