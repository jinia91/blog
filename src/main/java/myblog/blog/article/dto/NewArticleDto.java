package myblog.blog.article.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import myblog.blog.tags.domain.Tags;
import myblog.blog.tags.dto.TagsDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class NewArticleDto {

    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private String toc;
    @NotBlank
    private Long memberId;

    private String thumbnailUrl;

    private String category;
    private String tags;


}
