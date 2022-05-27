package myblog.blog.category.appliacation;

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
public interface CategoryDtoMapper {
    ArticleResponseByCategory category(Article article);
    @Mappings({
            @Mapping(target = "count",ignore = true),
            @Mapping(target = "POrder",ignore = true),
            @Mapping(target = "COrder",ignore = true)
    })
    CategorySimpleDto categorySimpleDto(Category category);
}