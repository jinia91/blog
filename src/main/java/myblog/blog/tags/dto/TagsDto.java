package myblog.blog.tags.dto;

import lombok.Data;

    /*
        - 뷰단 사용을 위한 DTO
    */
@Data
public class TagsDto {

    private String name;

    public TagsDto(){}

    public TagsDto(String name) {
        this.name = name;
    }
}
