package myblog.blog.article.application.port.incomming.response;

import lombok.Data;

    /*
        - 뷰단 사용을 위한 DTO
    */
@Data
public class TagsResponse {
    private String name;
    public TagsResponse(){}
    public TagsResponse(String name) {
        this.name = name;
    }
}
