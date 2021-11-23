package myblog.blog.comment.dto;

import lombok.Data;

@Data
public class CommentForm {

private String content;
private boolean secret;

}
