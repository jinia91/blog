package myblog.blog.article.adapter.outgoing.persistence;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

import static myblog.blog.article.domain.QArticle.article;

@Repository
@RequiredArgsConstructor
public class QdslArticleRepository {

    private final JPAQueryFactory queryFactory;

    List<Article> findByOrderByIdDesc(Long articleId, int size) {
        return queryFactory
                .selectFrom(article)
                .where(cursorLt(articleId))
                .orderBy(article.id.desc())
                .limit(size)
                .fetch();
    }

    private Predicate cursorLt(Long articleId) {
        return articleId == 0L ? null : article.id.lt(articleId);
    }
}
