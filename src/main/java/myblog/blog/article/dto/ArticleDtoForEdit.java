package myblog.blog.article.dto;

import lombok.Getter;
import lombok.Setter;
import myblog.blog.category.domain.Category;
import myblog.blog.tags.domain.ArticleTagList;
import myblog.blog.tags.dto.TagsDto;

import java.util.ArrayList;
import java.util.List;

/*
    - 아티클 수정 폼을 위한 DTO
*/
@Getter @Setter
public class ArticleDtoForEdit {

    private Long id;
    private String title;
    private String content;
    private String toc;
    private String thumbnailUrl;

    private List<String> articleTagList = new ArrayList<>();
    private Category category;
}
