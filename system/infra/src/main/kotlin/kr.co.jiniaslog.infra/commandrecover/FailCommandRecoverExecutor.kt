package kr.co.jiniaslog.infra.commandrecover

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.lang.reflect.InvocationTargetException

private val log = KotlinLogging.logger { }

@Component
class FailCommandRecoverExecutor(
    private val context: ApplicationContext,
    private val storage: CommandFailureLogStore,
    private val objectMapper: ObjectMapper,
) {
    fun recoverAll() {
        val failureLogs = storage.findAll()
        failureLogs.forEach {
            recover(it.id)
        }
    }

    fun recover(id: Long) {
        try {
            val failureLog = storage.selectById(id)!!
            val handlerClass = Class.forName(failureLog.useCaseExecutorClass)
            val executorBean = context.getBean(handlerClass)
            val commandClazz = Class.forName(failureLog.commandClass)
            val method = handlerClass.getMethod(failureLog.method, commandClazz)
            val command = objectMapper.readValue(failureLog.command, commandClazz)
            method.invoke(executorBean, command)
        } catch (e: InvocationTargetException) {
            log.error("fail to recover commandLogId : $id, ${e.targetException}")
        }
        storage.delete(id)
    }
}
