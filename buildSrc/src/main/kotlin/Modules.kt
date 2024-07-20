data class Module(val path: String)

object Modules {
    object Mains {
        val MonolithMain = Module(":mains:monolith-main")
    }

    object Service {
        object Memo {
            val Domain = Module(":service:memo:memo-domain")
            val Application = Module(":service:memo:memo-application")

            enum class Adaptors(val path: String) {
                InAcl(":service:memo:adapter:memo-in-acl"),
                InHttp(":service:memo:adapter:memo-in-http"),
                InWebsocket(":service:memo:adapter:memo-in-websocket"),
                OutNeo4j(":service:memo:adapter:memo-out-neo4j"),
            }
        }

        object Media {
            val Domain = Module(":service:media:media-domain")
            val Application = Module(":service:media:media-application")

            enum class Adaptors(val path: String) {
                InHttp(":service:media:adapter:media-in-http"),
                OutGithub(":service:media:adapter:media-out-github")
            }
        }

        object Blog {
            val Core = Module(":service:blog:blog-core")

            enum class Adaptors(val path: String) {
                InHttp(":service:blog:adapter:blog-in-http"),
                InWebsocket(":service:blog:adapter:blog-in-websocket"),
                OutUser(":service:blog:adapter:blog-out-user"),
                OutMemo(":service:blog:adapter:blog-out-memo"),
                OutPersistence(":service:blog:adapter:blog-out-persistence")
            }
        }

        object AuthUser {
            val Domain = Module(":service:user-auth:user-auth-domain")
            val Application = Module(":service:user-auth:user-auth-application")

            enum class Adaptors(val path: String) {
                InAcl(":service:user-auth:adapter:user-auth-in-acl"),
                InHttp(":service:user-auth:adapter:user-auth-in-http"),
                OutGoogle(":service:user-auth:adapter:user-auth-out-google"),
                OutH2(":service:user-auth:adapter:user-auth-out-h2"),
                OutCache(":service:user-auth:adapter:user-auth-out-cache")
            }
        }

        val MessageNexus = Module(":service:message-nexus")
    }

    object Libs {
        val RestKernel = Module(":libs:rest-kernel")
        val SnowflakeIdGenerator = Module(":libs:snowflake-id-generator")
        val CoreKernel = Module(":libs:core-kernel")
        val RdbKernel = Module(":libs:rdb-kernel")
        val WebsocketKernel = Module(":libs:websocket-kernel")
        val MessagingHandlerGenerator = Module(":libs:messaging-handler-generator")
    }
}
