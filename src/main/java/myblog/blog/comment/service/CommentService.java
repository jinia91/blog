package myblog.blog.comment.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.comment.domain.Comment;
import myblog.blog.comment.dto.CommentDtoForLayout;
import myblog.blog.comment.dto.CommentForm;
import myblog.blog.comment.repository.CommentRepository;
import myblog.blog.comment.repository.NaCommentRepository;
import myblog.blog.member.doamin.Member;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
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
    @CacheEvict(value = "layoutRecentCommentCaching", allEntries = true)
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
    @CacheEvict(value = "layoutRecentCommentCaching", allEntries = true)
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
    @CacheEvict(value = "layoutRecentCommentCaching", allEntries = true)
    public void deleteComment(Long commentId){
        naCommentRepository.deleteComment(commentId);
    }

    /*
        - 최신 댓글 5개 가져오기
          - 레이아웃 렌더링 성능 향상을 위해 캐싱작업
             카테고리 변경 / 아티클 변경이 존재할경우 레이아웃 캐시 초기화
             DTO 매핑 로직 서비스단에서 처리
    */
    @Cacheable(value = "layoutRecentCommentCaching", key = "0")
    public List<CommentDtoForLayout> recentCommentList(){
       return commentRepository.findTop5ByOrderByIdDesc()
               .stream()
                .map(comment ->
                        new CommentDtoForLayout(comment.getId(), comment.getArticle().getId(), comment.getContent(), comment.isSecret()))
                .collect(Collectors.toList());
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
