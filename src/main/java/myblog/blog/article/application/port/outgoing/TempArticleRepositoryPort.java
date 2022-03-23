package myblog.blog.article.application.port.outgoing;

import myblog.blog.article.domain.TempArticle;

import java.util.Optional;

public interface TempArticleRepositoryPort {
    void save(TempArticle tempArticle);
    Optional<TempArticle> findById(long l);
    void delete(TempArticle tempArticle);
}
