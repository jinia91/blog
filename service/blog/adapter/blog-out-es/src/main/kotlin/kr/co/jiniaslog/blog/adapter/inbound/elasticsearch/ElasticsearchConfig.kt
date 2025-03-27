package kr.co.jiniaslog.blog.adapter.inbound.elasticsearch

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration
import java.time.Duration

@Configuration
class ElasticsearchConfig : ElasticsearchConfiguration() {
    @Value("\${spring.elasticsearch.uris}")
    private lateinit var uris: String

    @Value("\${spring.elasticsearch.connection-timeout}")
    private lateinit var connectTimeout: String

    @Value("\${spring.elasticsearch.socket-timeout}")
    private lateinit var socketTimeout: String

    override fun clientConfiguration(): ClientConfiguration {
        return ClientConfiguration.builder()
            .connectedTo(uris)
            .withConnectTimeout(Duration.ofMillis(connectTimeout.toLong()))
            .withSocketTimeout(Duration.ofMillis(socketTimeout.toLong()))
            .build()
    }
}
