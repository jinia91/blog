package myblog.blog.article.application.port.incomming;

import myblog.blog.article.adapter.incomming.web.ArticleForm;
import myblog.blog.article.domain.Article;
import myblog.blog.category.domain.Category;
import myblog.blog.member.doamin.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ArticleUseCase {
    Long writeArticle(ArticleForm articleForm, Member member);
    void editArticle(Long articleId, ArticleForm articleForm);
    void deleteArticle(Long articleId);
    List<Article> getPopularArticles();
    List<Article> getRecentArticles(Long lastArticleId);
    Slice<Article> getArticlesByCategory(String category, Integer tier, Integer page);
    Article readArticle(Long id);
    List<Article> getTotalArticle();
    List<Article> getArticlesByCategoryForDetailView(Category category);
    Page<Article> getArticlesByTag(String tag, Integer page);
    Page<Article> getArticlesByKeyword(String keyword, Integer page);
    void addHit(Article article);
    void backupArticle(Long articleId);

}
