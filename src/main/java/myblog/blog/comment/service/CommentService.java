package myblog.blog.comment.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.comment.domain.Comment;
import myblog.blog.comment.dto.CommentDto;
import myblog.blog.comment.dto.CommentForm;
import myblog.blog.comment.repository.CommentRepository;
import myblog.blog.member.doamin.Member;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    public List<CommentDto> getCommentList(Long articleId){

        List<Comment> commentsByArticleId = commentRepository.findCommentsByArticleId(articleId);

        return CommentDto.createFrom(commentsByArticleId,0);

    }

    public void savePComment(CommentForm commentForm, Member member, Article article){

        Comment comment = Comment.builder()
                .article(article)
                .content(commentForm.getContent())
                .tier(0)
                .pOrder(commentRepository.countCommentsByArticleAndTier(article,0)+1)
                .member(member)
                .build();

        commentRepository.save(comment);

    }


    public void saveCComment(CommentForm commentForm, Member member, Article article, Integer pOrder) {

        Comment comment = Comment.builder()
                .article(article)
                .content(commentForm.getContent())
                .tier(1)
                .pOrder(pOrder)
                .member(member)
                .build();

        commentRepository.save(comment);

    }
}
