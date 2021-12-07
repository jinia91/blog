package myblog.blog.comment.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.comment.domain.Comment;
import myblog.blog.comment.dto.CommentForm;
import myblog.blog.comment.repository.CommentRepository;
import myblog.blog.comment.repository.NaCommentRepository;
import myblog.blog.member.doamin.Member;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final NaCommentRepository naCommentRepository;

    /*
        - 아티클에 달린 댓글 전체 가져오기
    */
    public List<Comment> getCommentList(Long articleId){
        return commentRepository.findCommentsByArticleId(articleId);
    }

    /*
        - 부모 댓글 저장
    */
    public void savePComment(CommentForm commentForm, Member member, Article article){

        Comment comment = Comment.builder()
                .article(article)
                .content(removeDuplicatedEnter(commentForm))
                .tier(0)
                .pOrder(commentRepository.countCommentsByArticleAndTier(article,0)+1)
                .member(member)
                .secret(commentForm.isSecret())
                .build();

        commentRepository.save(comment);

    }

    /*
        - 자식 댓글 저장
    */
    public void saveCComment(CommentForm commentForm, Member member, Article article, Long parentId) {

        Comment pComment = commentRepository.findById(parentId).get();

        Comment comment = Comment.builder()
                .article(article)
                .content(removeDuplicatedEnter(commentForm))
                .tier(1)
                .pOrder(pComment.getPOrder())
                .member(member)
                .parents(pComment)
                .secret(commentForm.isSecret())
                .build();

        commentRepository.save(comment);

    }

    /*
        - 댓글 삭제
    */
    public void deleteComment(Long commentId){
        naCommentRepository.deleteComment(commentId);
    }

    /*
        - 최신 댓글 5개 가져오기
    */
    public List<Comment> recentCommentList(){
       return commentRepository.findTop5ByOrderByIdDesc();
    }

    /*
    - 중복 개행 개행 하나로 압축 알고리즘
    */
    private String removeDuplicatedEnter(CommentForm commentForm) {

        char[] contentBox = new char[commentForm.getContent().length()];
        int idx = 0;
        String zipWord = "\n\n";

        for(int i = 0; i< commentForm.getContent().length(); i++){

            contentBox[idx] = commentForm.getContent().charAt(i);

            if(contentBox[idx] == '\n'&&idx >= 1){

                int tempIdx = idx;
                int length = 1;
                boolean isRemove = true;

                for(int j = 0; j<2; j++){

                    if(contentBox[tempIdx--] != zipWord.charAt(length--)){

                        isRemove = false;
                        break;

                    }

                }
                if(isRemove) idx -= 1;
            }
            idx++;
        }
        return String.valueOf(contentBox).trim();
    }
}
