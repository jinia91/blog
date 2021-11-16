package myblog.blog.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import myblog.blog.comment.domain.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class CommentDto {

    private Long id;
    private int tier;
    private Long parentId;
    private String username;
    private String picUrl;
    private String content;
    private boolean secret;
    private List<CommentDto> commentDtoList = new ArrayList<>();
    private LocalDateTime createdDate;

    public static List<CommentDto> createFrom(List<Comment> commentList, int dept) {

        ArrayList<CommentDto> commentDtoList = new ArrayList<>();

        while (true) {

            if (commentList.isEmpty()) {
                return commentDtoList;
            }

            Comment comment = commentList.get(0);

            if (comment.getTier() == dept) {
                commentDtoList.add(new CommentDto(comment));
                commentList.remove(0);
            } else if (comment.getTier() > dept) {
                List<CommentDto> childList = createFrom(commentList, dept + 1);
                commentDtoList.get(commentDtoList.size() - 1)
                        .setCommentDtoList(childList);
            } else {
                return commentDtoList;
            }
        }
    }


    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.tier = comment.getTier();
        this.parentId = Optional.ofNullable(comment.getParents()).orElseGet(() -> comment).getId();
        this.username = comment.getMember().getUsername();
        this.picUrl = comment.getMember().getPicUrl();
        this.content = comment.getContent();
        this.secret = comment.isSecret();
        this.createdDate = comment.getCreatedDate();
    }


}
