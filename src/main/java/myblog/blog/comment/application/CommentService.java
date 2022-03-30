package myblog.blog.comment.application;

import myblog.blog.article.application.port.incomming.ArticleUseCase;
import myblog.blog.comment.application.port.incomming.CommentUseCase;
import myblog.blog.comment.application.port.outgoing.CommentRepositoryPort;

import myblog.blog.comment.domain.Comment;
import myblog.blog.article.domain.Article;
import myblog.blog.member.doamin.Member;

import lombok.RequiredArgsConstructor;
import myblog.blog.member.application.port.incomming.MemberQueriesUseCase;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService implements CommentUseCase {
    private final ArticleUseCase articleUseCase;
    private final MemberQueriesUseCase memberQueriesUseCase;
    private final CommentRepositoryPort commentRepositoryPort;

    /*
        - 부모 댓글 저장
    */
    @CacheEvict(value = "layoutRecentCommentCaching", allEntries = true)
    @Override
    public void savePComment(String content, boolean secret, Long memberId, Long articleId){
        Member member = memberQueriesUseCase.findById(memberId);
        Article article = articleUseCase.getArticle(articleId);

        Comment comment = Comment.builder()
                .article(article)
                .content(content)
                .tier(0)
                .pOrder(commentRepositoryPort.countCommentsByArticleAndTier(article,0)+1)
                .member(member)
                .secret(secret)
                .build();
        commentRepositoryPort.save(comment);

    }

    /*
        - 자식 댓글 저장
    */
    @CacheEvict(value = "layoutRecentCommentCaching", allEntries = true)
    @Override
    public void saveCComment(String content, boolean secret, Long memberId, Long articleId, Long parentId) {
        Member member = memberQueriesUseCase.findById(memberId);
        Article article = articleUseCase.getArticle(articleId);
        Comment pComment = commentRepositoryPort.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("NotfoundParentCommentException"));

        Comment comment = Comment.builder()
                .article(article)
                .content(content)
                .tier(1)
                .pOrder(pComment.getPOrder())
                .member(member)
                .parents(pComment)
                .secret(secret)
                .build();

        commentRepositoryPort.save(comment);
    }

    /*
        - 댓글 삭제
    */
    @CacheEvict(value = "layoutRecentCommentCaching", allEntries = true)
    @Override
    public void deleteComment(Long commentId){
        commentRepositoryPort.deleteComment(commentId);
    }
}
