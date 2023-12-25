
data class Module(val path: String)

object Modules {
    object Service{
        object Memo {
            val Core = Module(":service:memo:memo-core")
            val ReadOnly = Module(":service:memo:memo-queries")
            enum class Adaptors(val path: String) {
                InHttp(":service:memo:adapter:memo-in-http"),
                InWebsocket(":service:memo:adapter:memo-in-websocket"),
                Persistence(":service:memo:adapter:memo-persistence")
                ;
            }
        }

        object Media {
            val Core = Module(":service:media:media-core")
            enum class Adaptors(val path: String) {
                InHttp(":service:media:adapter:media-in-http"),
                OutGithub(":service:media:adapter:media-out-github")
                ;
            }
        }

        object Blog {
            val Core = Module(":service:blog:blog-core")
            enum class Adaptors(val path: String) {
                InHttp(":service:blog:adapter:blog-in-http"),
                Persistence(":service:blog:adapter:blog-persistence")
                ;
            }
        }

        val MessageNexus = Module(":service:message-nexus")
    }

    object Infra {
        val MONOLITH = Module(":infra:monolith-infra")
    }

    object Libs {
        val GlobalLogging = Module(":libs:global-logging")
        val CoreKernel = Module(":libs:core-kernel")
        val HttpKernel = Module(":libs:http-kernel")
    }
}
