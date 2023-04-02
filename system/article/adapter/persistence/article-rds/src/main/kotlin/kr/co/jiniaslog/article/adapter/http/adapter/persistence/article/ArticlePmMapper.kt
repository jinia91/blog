package kr.co.jiniaslog.article.adapter.http.adapter.persistence.article

import kr.co.jiniaslog.article.adapter.http.domain.Article
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
)
interface ArticlePmMapper {
    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "writerId.value", target = "writerId")
    fun toPm(article: Article): ArticlePM
}
