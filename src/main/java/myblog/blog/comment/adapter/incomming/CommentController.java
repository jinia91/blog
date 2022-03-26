package myblog.blog.comment.adapter.incomming;

import myblog.blog.comment.application.port.incomming.CommentQueriesUseCase;
import myblog.blog.comment.application.port.incomming.CommentUseCase;
import myblog.blog.comment.application.port.incomming.response.CommentDto;

import myblog.blog.member.application.port.incomming.response.PrincipalDetails;

import lombok.RequiredArgsConstructor;
import myblog.blog.member.application.port.incomming.response.MemberVo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentUseCase commentUseCase;
    private final CommentQueriesUseCase commentQueriesUseCase;

    /*
        - 아티클 조회시 아티클에 달린 댓글들 전체 조회
    */
    @GetMapping("/comment/list/{articleId}")
    List<CommentDto> getCommentList(@PathVariable Long articleId){
        return commentQueriesUseCase.getCommentList(articleId);
    }

    /*
        - 댓글 작성 요청
    */
    @PostMapping("/comment/write")
    List<CommentDto> getCommentList(@RequestParam Long articleId,
                                           @RequestParam(required = false) Long parentId,
                                           @Validated @RequestBody CommentForm commentForm, Errors errors,
                                           @AuthenticationPrincipal PrincipalDetails principal){
        if (errors.hasErrors()) {
            throw new InvalidCommentRequestException(Objects.requireNonNull(errors.getFieldError()).getDefaultMessage());
        }

        MemberVo member = principal.getMember();
        // 부모 댓글인지 자식댓글인지 분기로 저장
        if(parentId != null){
            commentUseCase.saveCComment(commentForm.getContent(), commentForm.isSecret(), member.getId(), articleId, parentId);
        }
        else {
            commentUseCase.savePComment(commentForm.getContent(), commentForm.isSecret(), member.getId(), articleId);
        }

        return commentQueriesUseCase.getCommentList(articleId);
    }

    /*
        - 댓글 삭제 요청
    */
    @PostMapping("/comment/delete")
    List<CommentDto> deleteComment(@RequestParam Long articleId,
                            @RequestParam Long commentId) {
        commentUseCase.deleteComment(commentId);
        return commentQueriesUseCase.getCommentList(articleId);
    }
}
