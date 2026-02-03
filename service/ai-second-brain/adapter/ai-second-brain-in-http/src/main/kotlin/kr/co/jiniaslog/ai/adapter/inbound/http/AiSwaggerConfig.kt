package kr.co.jiniaslog.ai.adapter.inbound.http

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.tags.Tag
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AiSwaggerConfig {

    @Bean
    fun aiOpenApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("AI Second Brain API")
                    .description("AI 기반 챗봇 및 메모 추천 API")
                    .version("1.0.0")
            )
            .addTagsItem(Tag().name("Chat").description("AI 챗봇 관련 API"))
            .addTagsItem(Tag().name("Recommend").description("메모 추천 API"))
            .addTagsItem(Tag().name("Sync").description("임베딩 동기화 API"))
    }
}
