package myblog.blog.tags.dto;

import lombok.Data;

@Data
public class TagsDto {

    private String value;

    @Override
    public String toString() {
        return "{ value : " + value + "}";
    }
}
