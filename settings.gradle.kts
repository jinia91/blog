rootProject.name = "Jinia's Log"

include(
    /**
     * ################
     * ## Jinia's Log##
     * ################
      */
    "boot", // framework 의존성을 가진 main module
    "system",
    "system:article",
    "system:article:domain",
    "system:article:adapter",
    "system:article:application",
    "system:user",
    "system:category",
    "system:imgupload",
    "system:comment",
    "system:chat",
    "system:notification",
    "system:seo",
    "system:lib",
)
