data class Module(val path: String)

object Modules {
    object Mains {
        val MonolithMain = Module(":mains:monolith-main")
    }

    object Service {
        object Memo {
            val Core = Module(":service:memo:memo-core")

            enum class Adaptors(val path: String) {
                InAcl(":service:memo:adapter:memo-in-acl"),
                InHttp(":service:memo:adapter:memo-in-http"),
                InWebsocket(":service:memo:adapter:memo-in-websocket"),
                InBatch(":service:memo:adapter:memo-in-batch"),
                OutMySql(":service:memo:adapter:memo-out-mysql"),
                OutUser(":service:memo:adapter:memo-out-user")
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
                InBatch(":service:blog:adapter:blog-in-batch"),
                InAcl(":service:blog:adapter:blog-in-acl"),
                InHttp(":service:blog:adapter:blog-in-http"),
                InWebsocket(":service:blog:adapter:blog-in-websocket"),
                OutUser(":service:blog:adapter:blog-out-user"),
                OutMemo(":service:blog:adapter:blog-out-memo"),
                OutMySql(":service:blog:adapter:blog-out-mysql"),
                OutEs(":service:blog:adapter:blog-out-es")
            }
        }

        object AuthUser {
            val Domain = Module(":service:user-auth:user-auth-domain")
            val Application = Module(":service:user-auth:user-auth-application")

            enum class Adaptors(val path: String) {
                InAcl(":service:user-auth:adapter:user-auth-in-acl"),
                InHttp(":service:user-auth:adapter:user-auth-in-http"),
                OutGoogle(":service:user-auth:adapter:user-auth-out-google"),
                OutMySql(":service:user-auth:adapter:user-auth-out-mysql"),
                OutCache(":service:user-auth:adapter:user-auth-out-cache")
            }
        }

        object Seo {
            val Core = Module(":service:seo")
        }

        object Comment {
            val Core = Module(":service:comment:comment-core")
            enum class Adaptors(val path: String) {
                InHttp(":service:comment:adapter:comment-in-http"),
                OutMySql(":service:comment:adapter:comment-out-mysql"),
                OutBlog(":service:comment:adapter:comment-out-blog"),
                OutUser(":service:comment:adapter:comment-out-user")
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
