package myblog.blog.comment.controller;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.article.service.ArticleService;
import myblog.blog.comment.dto.CommentDto;
import myblog.blog.comment.dto.CommentForm;
import myblog.blog.comment.service.CommentService;
import myblog.blog.member.auth.PrincipalDetails;
import myblog.blog.member.doamin.Member;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final ArticleService articleService;

    @GetMapping("/comment/list/{articleId}")
    public List<CommentDto> getCommentList(@PathVariable Long articleId){

        List<CommentDto> commentList = commentService.getCommentList(articleId);

        return commentList;

    }

    @PostMapping("/comment/write")
    public List<CommentDto> getCommentList(@RequestParam Long articleId,
                                           @RequestParam(required = false) Long parentId,
                                           @RequestBody CommentForm commentForm,
                                           Authentication authentication){

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Member member = principal.getMember();
        Article article = articleService.findArticleById(articleId);

        if(parentId != null){
            commentService.saveCComment(commentForm, member, article, parentId);
        }
        else {
            commentService.savePComment(commentForm, member, article);
        }

        List<CommentDto> commentList = commentService.getCommentList(articleId);
        return commentList;


    }

    @PostMapping("/comment/delete")
    public List<CommentDto> deleteComment(@RequestParam Long articleId,
                            @RequestParam Long commentId,
                            Authentication authentication) {

        commentService.deleteComment(commentId);
        List<CommentDto> commentList = commentService.getCommentList(articleId);
        return commentList;
    }

}
