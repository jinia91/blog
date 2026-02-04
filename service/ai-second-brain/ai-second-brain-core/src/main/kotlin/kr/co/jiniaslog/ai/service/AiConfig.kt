package kr.co.jiniaslog.ai.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AiConfig {

    @Bean
    fun chatClient(@Qualifier("googleGenAiChatModel") chatModel: ChatModel): ChatClient {
        return ChatClient.builder(chatModel).build()
    }
}
