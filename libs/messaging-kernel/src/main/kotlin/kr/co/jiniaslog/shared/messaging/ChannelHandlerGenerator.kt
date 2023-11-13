package kr.co.jiniaslog.shared.messaging

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import kr.co.jiniaslog.message.nexus.event.Channel

class ChannelHandlerGenerator(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(Channel::class.java.canonicalName)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { classDeclaration ->
                generateChannelHandler(classDeclaration, codeGenerator)
            }
        return emptyList()
    }

    private fun generateChannelHandler(
        classDeclaration: KSClassDeclaration,
        codeGenerator: CodeGenerator,
    ) {
        val packageName = "kr.co.jiniaslog.shared.messaging.adapter.in"
        val className = classDeclaration.simpleName.asString()
        val fileName = "Abstract${className}ChannelHandler"

        // KotlinPoet를 사용하여 파일 내용을 생성
        val fileContent =
            buildString {
                appendLine("package $packageName")
                appendLine()
                appendLine("import org.springframework.integration.annotation.ServiceActivator")
                appendLine()
                appendLine("abstract class $fileName {")
                appendLine("    @ServiceActivator(inputChannel = \"${className}Channel\")")
                appendLine("    abstract fun handle(event: $className)")
                appendLine("}")
            }

        // CodeGenerator를 사용하여 파일 작성
        val file =
            codeGenerator.createNewFile(
                dependencies = Dependencies(aggregating = false, classDeclaration.containingFile!!),
                packageName = packageName,
                fileName = fileName,
            )
        file.write(fileContent.toByteArray())
        file.close()
    }
}
