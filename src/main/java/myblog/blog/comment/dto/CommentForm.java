package myblog.blog.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/*
    - 댓글 작성 폼
*/
@Data
public class CommentForm {

    @NotBlank(message = "댓글 내용을 작성해주세요")
    @Size(min = 1,max = 250, message = "댓글은 255자 이내로 작성해주세요")
    private String content;
    private boolean secret;

}
