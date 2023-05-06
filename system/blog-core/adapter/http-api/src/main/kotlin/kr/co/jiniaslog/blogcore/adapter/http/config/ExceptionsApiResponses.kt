package kr.co.jiniaslog.blogcore.adapter.http.config

import java.lang.annotation.Inherited

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class ExceptionsApiResponses(vararg val value: ExceptionApiResponse = [])
