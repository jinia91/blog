package myblog.blog.comment.application;

import myblog.blog.article.application.port.incomming.ArticleUseCase;
import myblog.blog.comment.application.port.incomming.CommentUseCase;
import myblog.blog.comment.application.port.incomming.CommentDto;
import myblog.blog.comment.application.port.incomming.CommentDtoForLayout;
import myblog.blog.comment.application.port.outgoing.CommentRepositoryPort;

import myblog.blog.comment.domain.Comment;
import myblog.blog.article.domain.Article;
import myblog.blog.member.doamin.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService implements CommentUseCase {
    private final ArticleUseCase articleUseCase;
    private final CommentRepositoryPort commentRepositoryPort;

    /*
        - 아티클에 달린 댓글 전체 가져오기
    */
    @Override
    public List<CommentDto> getCommentList(Long articleId){
        return CommentDto.listCreateFrom(commentRepositoryPort.findCommentsByArticleId(articleId),0);
    }

    /*
        - 부모 댓글 저장
    */
    @CacheEvict(value = "layoutRecentCommentCaching", allEntries = true)
    @Override
    public void savePComment(String content, boolean secret, Member member, Long articleId){

        Article article = articleUseCase.getArticle(articleId);

        Comment comment = Comment.builder()
                .article(article)
                .content(removeDuplicatedEnter(content))
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
    public void saveCComment(String content, boolean secret, Member member, Long articleId, Long parentId) {

        Article article = articleUseCase.getArticle(articleId);
        Comment pComment = commentRepositoryPort.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("NotfoundParentCommentException"));

        Comment comment = Comment.builder()
                .article(article)
                .content(removeDuplicatedEnter(content))
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

    /*
        - 최신 댓글 5개 가져오기
          - 레이아웃 렌더링 성능 향상을 위해 캐싱작업
             카테고리 변경 / 아티클 변경이 존재할경우 레이아웃 캐시 초기화
             DTO 매핑 로직 서비스단에서 처리
    */
    @Cacheable(value = "layoutRecentCommentCaching", key = "0")
    @Override
    public List<CommentDtoForLayout> recentCommentList(){
       return commentRepositoryPort.findTop5ByOrderByIdDesc()
               .stream()
                .map(comment ->
                        new CommentDtoForLayout(comment.getId(), comment.getArticle().getId(), comment.getContent(), comment.isSecret()))
                .collect(Collectors.toList());
    }

    /*
    - 중복 개행 개행 하나로 압축 알고리즘
    */
    private String removeDuplicatedEnter(String content) {

        char[] contentBox = new char[content.length()];
        int idx = 0;
        String zipWord = "\n\n";

        for(int i = 0; i< content.length(); i++){

            contentBox[idx] = content.charAt(i);

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
