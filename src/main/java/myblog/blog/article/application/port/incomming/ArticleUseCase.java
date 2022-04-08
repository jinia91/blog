package myblog.blog.article.application.port.incomming;

import myblog.blog.article.application.port.incomming.request.ArticleCreateCommand;
import myblog.blog.article.application.port.incomming.request.ArticleEditCommand;
import myblog.blog.article.domain.Article;

import java.util.List;

public interface ArticleUseCase {
    Long writeArticle(ArticleCreateCommand articleCreateCommand);
    void editArticle(ArticleEditCommand articleEditCommand);
    void deleteArticle(Long articleId);
    void addHit(Long articleId);
    void backupArticle(Long articleId);
    Article getArticle(Long id);
    List<Article> getTotalArticle();
}
