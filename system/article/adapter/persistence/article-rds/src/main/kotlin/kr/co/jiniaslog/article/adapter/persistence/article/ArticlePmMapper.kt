package kr.co.jiniaslog.article.adapter.persistence.article

import kr.co.jiniaslog.article.domain.Article
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
)
interface ArticlePmMapper {
    fun toPm(article: Article): ArticlePM
}
