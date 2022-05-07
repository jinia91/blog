package myblog.blog.article.application;

import myblog.blog.article.application.port.incomming.response.*;
import myblog.blog.article.domain.Article;
import myblog.blog.article.domain.Tags;
import myblog.blog.category.appliacation.port.incomming.response.CategorySimpleDto;
import myblog.blog.category.domain.Category;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ArticleDtoMapper {
    ArticleResponseForCardBox cardBox(Article article);
    @Mappings({
        @Mapping(target = "articleTagList",ignore = true)
    })
    ArticleResponseForEdit edit(Article article);
    @Mappings({
            @Mapping(target = "tags",ignore = true),
            @Mapping(source = "article.category.title", target = "category"),
            @Mapping(source = "article.member.id", target = "memberId"),
    })
    ArticleResponseForDetail detail(Article article);
    ArticleResponseByCategory category(Article article);
    TagsResponse of(Tags tag);
    @Mappings({
            @Mapping(target = "count",ignore = true),
            @Mapping(target = "POrder",ignore = true),
            @Mapping(target = "COrder",ignore = true)
    })
    CategorySimpleDto categorySimpleDto(Category category);
}