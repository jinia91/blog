package kr.co.jiniaslog.infra.events

import kr.co.jiniaslog.shared.core.context.RegisteredEvent
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter

object DomainEventScanner {
    fun findAnnotatedEventClasses(basePackage: String?): Set<Class<*>> {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(RegisteredEventTypeFilter())
        val beanDefinitions: Set<BeanDefinition> = scanner.findCandidateComponents(basePackage!!)
        val registeredEventClasses: MutableSet<Class<*>> = HashSet()
        for (beanDefinition in beanDefinitions) {
            try {
                registeredEventClasses.add(Class.forName(beanDefinition.beanClassName))
            } catch (e: ClassNotFoundException) {
                throw IllegalStateException("Failed to load class", e)
            }
        }
        return registeredEventClasses
    }
}

internal class RegisteredEventTypeFilter : AbstractTypeHierarchyTraversingFilter(false, false) {
    override fun matchClassName(className: String): Boolean {
        return false
    }

    override fun matchSuperClass(superClassName: String): Boolean {
        return false
    }

    override fun matchInterface(interfaceName: String): Boolean {
        return false
    }

    override fun matchSelf(metadataReader: MetadataReader): Boolean {
        return metadataReader.annotationMetadata.hasAnnotation(RegisteredEvent::class.java.name)
    }
}
