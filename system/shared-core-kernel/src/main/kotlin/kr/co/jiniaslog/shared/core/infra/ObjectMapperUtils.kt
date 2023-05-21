package kr.co.jiniaslog.shared.core.infra

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDateTime

object ObjectMapperUtils {
    val defaultMapper = jacksonObjectMapper().apply {
        registerModule(
            SimpleModule().apply {
                this.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer.INSTANCE)
                this.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer.INSTANCE)
            },
        )
    }
}
