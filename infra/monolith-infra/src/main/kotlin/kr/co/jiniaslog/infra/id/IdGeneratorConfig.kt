package kr.co.jiniaslog.infra.id

import kr.co.jiniaslog.shared.core.domain.IdGenerator
import kr.co.jiniaslog.shared.core.domain.IdUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class IdGeneratorConfig {
    companion object {
        const val MACHINE_SEQUENCE_BIT_SIZE = 7
    }

    @Bean
    fun idGenerator(
//        allocator: ServerIdAllocator
    ): IdGenerator {
        val serverId = 1
//            allocator.allocate() : 분산환경에서 ServerId 할당기, 현재는 하드 코딩
        val impl = SnowFlake53StrategyIdGeneratorImpl(serverId, MACHINE_SEQUENCE_BIT_SIZE)
        IdUtils.idGenerator = impl
        return impl
    }
}
