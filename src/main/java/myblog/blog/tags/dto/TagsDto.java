package myblog.blog.tags.dto;

import lombok.Data;

@Data
public class TagsDto {

    private String name;

    public TagsDto(){}

    public TagsDto(String name) {
        this.name = name;
    }
}
