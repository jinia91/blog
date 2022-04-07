package myblog.blog.article.application.port.outgoing;

import myblog.blog.article.domain.Article;
import myblog.blog.category.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface ArticleRepositoryPort {
    List<Article> findTop6ByOrderByHitDesc();
    List<Article> findTop6ByCategoryOrderByIdDesc(Category category);
    List<Article> findByOrderByIdDesc(int page, int size);
    List<Article> findByOrderByIdDesc(Long articleId, int size);
    List<Article> findBySubCategoryOrderByIdDesc(int page, int size, String category);
    List<Article> findBySuperCategoryOrderByIdDesc(int page, int size, String category);
    Article findArticleByIdFetchCategoryAndTags(Long articleId);
    Page<Article> findAllByArticleTagsOrderById(Pageable pageable, String tag);
    Page<Article> findAllByKeywordOrderById(Pageable pageable, String keyword);
    List<Article> findAllByOrderByIdDesc();
    Optional<Article> findById(Long articleId);
    void save(Article newArticle);
    void deleteArticle(Long articleId);
}
