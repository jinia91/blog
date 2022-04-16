package myblog.blog.article.application.port.incomming;

import myblog.blog.article.application.port.incomming.response.TempArticleDto;

public interface TempArticleUseCase {
    void saveTemp(String tempArticleContents);
    TempArticleDto getTempArticle();
    void deleteTemp();
}
