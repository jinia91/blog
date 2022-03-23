package myblog.blog.article.application.port.incomming;

import myblog.blog.article.application.port.request.ArticleCreateRequest;
import myblog.blog.article.application.port.request.ArticleEditRequest;
import myblog.blog.article.domain.Article;

import java.util.List;

public interface ArticleUseCase {
    Long writeArticle(ArticleCreateRequest articleCreateRequest);
    void editArticle(ArticleEditRequest articleEditRequest);
    void deleteArticle(Long articleId);
    void addHit(Long articleId);
    void backupArticle(Long articleId);
    Article getArticle(Long id);
    List<Article> getTotalArticle();
}
