package myblog.blog.article.application.port.incomming;

import myblog.blog.article.domain.TempArticle;

import java.util.Optional;

public interface TempArticleUseCase {
    void saveTemp(TempArticle tempArticle);
    Optional<TempArticle> getTempArticle();
    void deleteTemp();
}
