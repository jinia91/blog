package kr.co.jiniaslog.infra.commandrecover

import kr.co.jiniaslog.shared.core.infra.CacheClient
import kr.co.jiniaslog.shared.core.infra.ObjectMapperUtils
import org.springframework.stereotype.Component

@Component
class CommandFailureLogStore(
    private val cacheClient: CacheClient,
) {
    private val LOG_MAP_KEY = "CommandFailureLogMap"

    fun store(commandFailureLog: CommandFailureLog) {
        val payload = ObjectMapperUtils.defaultMapper.writeValueAsString(commandFailureLog)
        cacheClient.saveInMap(LOG_MAP_KEY, commandFailureLog.id, payload)
    }

    fun selectById(id: Long): CommandFailureLog? {
        return cacheClient.findInMap<Long, String>(LOG_MAP_KEY, id)?.let { ObjectMapperUtils.defaultMapper.readValue(it, CommandFailureLog::class.java) }
    }

    fun delete(id: Long) {
        cacheClient.deleteInMap<Long, String>(LOG_MAP_KEY, id)
    }

    fun findAll(): List<CommandFailureLog> {
        return cacheClient.findAllInMap<Long, String>(LOG_MAP_KEY).toList().map { ObjectMapperUtils.defaultMapper.readValue(it, CommandFailureLog::class.java) }
    }
}
