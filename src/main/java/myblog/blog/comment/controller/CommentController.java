package myblog.blog.comment.controller;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.application.port.incomming.ArticleQueriesUseCase;
import myblog.blog.article.domain.Article;
import myblog.blog.article.application.ArticleService;
import myblog.blog.comment.dto.CommentDto;
import myblog.blog.comment.dto.CommentForm;
import myblog.blog.comment.service.CommentService;
import myblog.blog.shared.exception.CustomFormException;
import myblog.blog.member.auth.PrincipalDetails;
import myblog.blog.member.doamin.Member;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /*
        - 아티클 조회시 아티클에 달린 댓글들 전체 조회
    */
    @GetMapping("/comment/list/{articleId}")
    public List<CommentDto> getCommentList(@PathVariable Long articleId){
        return CommentDto.listCreateFrom(commentService.getCommentList(articleId),0);
    }

    /*
        - 댓글 작성 요청
    */
    @PostMapping("/comment/write")
    public List<CommentDto> getCommentList(@RequestParam Long articleId,
                                           @RequestParam(required = false) Long parentId,
                                           @Validated @RequestBody CommentForm commentForm, Errors errors,
                                           @AuthenticationPrincipal PrincipalDetails principal){
        if (errors.hasErrors()) {
            throw new CustomFormException(Objects.requireNonNull(errors.getFieldError()).getDefaultMessage());
        }

        Member member = principal.getMember();
        // 부모 댓글인지 자식댓글인지 분기로 저장
        if(parentId != null){
            commentService.saveCComment(commentForm, member, articleId, parentId);
        }
        else {
            commentService.savePComment(commentForm, member, articleId);
        }

        return CommentDto.listCreateFrom(commentService.getCommentList(articleId),0);
    }

    /*
        - 댓글 삭제 요청
    */
    @PostMapping("/comment/delete")
    public List<CommentDto> deleteComment(@RequestParam Long articleId,
                            @RequestParam Long commentId) {
        commentService.deleteComment(commentId);
        return CommentDto.listCreateFrom(commentService.getCommentList(articleId),0);
    }
}
