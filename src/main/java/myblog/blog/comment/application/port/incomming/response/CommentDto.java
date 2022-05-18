package myblog.blog.comment.application.port.incomming.response;

import lombok.Getter;
import lombok.Setter;
import myblog.blog.comment.domain.Comment;

import java.time.LocalDateTime;
import java.util.*;


/*
    - 트리구조 댓글 DTO
*/
@Getter
@Setter
public class CommentDto {

    private Long id;
    private int tier;
    private Long parentId;
    private String username;
    private Long memberId;
    private String picUrl;
    private String content;
    private boolean secret;
    private LocalDateTime createdDate;

    // 트리 구조를 갖기위한 리스트
    private List<CommentDto> commentDtoList = new ArrayList<>();

    /*
        - 재귀 호출용 스태틱 생성 메서드
                1. DTO객체 생성후 소스를 큐처리로 순차적 매핑
                2. Depth 변화시 재귀 호출 / 재귀 탈출
                3. 탈출시 상위 카테고리 list로 삽입하여 트리구조 작성
    */
    public static List<CommentDto> createCommentListFrom(List<Comment> commentSource, int dept) {

        ArrayList<CommentDto> commentDtoList = new ArrayList<>();

        while (true) {
            if (commentSource.isEmpty()) {
                return commentDtoList;
            }

            Comment comment = commentSource.get(0);

            if (comment.getTier() == dept) {
                commentDtoList.add(new CommentDto(comment));
                commentSource.remove(0);
            } else if (comment.getTier() > dept) {
                List<CommentDto> childList = createCommentListFrom(commentSource, dept + 1);
                commentDtoList.get(commentDtoList.size() - 1)
                        .setCommentDtoList(childList);
            } else {
                return commentDtoList;
            }
        }
    }

    // 매핑 생성
    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.tier = comment.getTier();
        this.parentId = Optional.ofNullable(comment.getParents()).orElseGet(() -> comment).getId();
        this.username = comment.getMember().getUsername();
        this.picUrl = comment.getMember().getPicUrl();
        this.content = comment.getContent();
        this.secret = comment.isSecret();
        this.createdDate = comment.getCreatedDate();
        this.memberId = comment.getMember().getId();
    }

}
