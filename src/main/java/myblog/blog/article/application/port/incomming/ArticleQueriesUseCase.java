package myblog.blog.article.application.port.incomming;

import myblog.blog.article.application.port.incomming.response.ArticleResponseForCardBox;
import myblog.blog.article.application.port.incomming.response.ArticleResponseByCategory;
import myblog.blog.article.application.port.incomming.response.ArticleResponseForDetail;
import myblog.blog.article.application.port.incomming.response.ArticleResponseForEdit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ArticleQueriesUseCase {
    List<ArticleResponseForCardBox> getPopularArticles();
    List<ArticleResponseForCardBox> getRecentArticles(Long lastArticleId);
    List<ArticleResponseForCardBox> getArticlesByCategory(String category, int tier, int page);
    ArticleResponseForEdit getArticleForEdit(Long id);
    ArticleResponseForDetail getArticleForDetail(Long id);
    List<ArticleResponseByCategory> getArticlesByCategoryForDetailView(String category);
    Page<ArticleResponseForCardBox> getArticlesByTag(String tag, Integer page);
    Page<ArticleResponseForCardBox> getArticlesByKeyword(String keyword, Integer page);
}
