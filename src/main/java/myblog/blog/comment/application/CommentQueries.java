package myblog.blog.comment.application;

import lombok.RequiredArgsConstructor;
import myblog.blog.comment.application.port.incomming.CommentQueriesUseCase;
import myblog.blog.comment.application.port.incomming.response.CommentDto;
import myblog.blog.comment.application.port.incomming.response.CommentDtoForLayout;
import myblog.blog.comment.application.port.outgoing.CommentRepositoryPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentQueries implements CommentQueriesUseCase {
    private final CommentRepositoryPort commentRepositoryPort;

    /*
        - 아티클에 달린 댓글 전체 가져오기
    */
    @Override
    public List<CommentDto> getCommentList(Long articleId){
        return CommentDto.createCommentListFrom(commentRepositoryPort.findCommentsByArticleId(articleId),0);
    }
    /*
        - 최신 댓글 5개 가져오기
          - 레이아웃 렌더링 성능 향상을 위해 캐싱작업
             카테고리 변경 / 아티클 변경이 존재할경우 레이아웃 캐시 초기화
             DTO 매핑 로직 서비스단에서 처리
    */
    @Cacheable(value = "layoutRecentCommentCaching", key = "0")
    @Override
    public List<CommentDtoForLayout> recentCommentListForLayout(){
       return commentRepositoryPort.findTop5ByOrderByIdDesc()
               .stream()
                .map(comment ->
                        new CommentDtoForLayout(comment.getId(), comment.getArticle().getId(), comment.getContent(), comment.isSecret()))
                .collect(Collectors.toList());
    }
}
