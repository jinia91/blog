package myblog.blog.article.application.port.incomming;

public interface TempArticleUseCase {
    void saveTemp(String tempArticleContents);
    TempArticleDto getTempArticle();
    void deleteTemp();
}
