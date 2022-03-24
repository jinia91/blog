package myblog.blog.article.adapter.outgoing.persistence;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.application.port.outgoing.ArticleBackupRepositoryPort;
import myblog.blog.article.application.port.outgoing.ArticleRepositoryPort;
import myblog.blog.article.domain.Article;
import myblog.blog.category.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ArticleRepositoryAdapter implements ArticleRepositoryPort {

    private final JpaArticleRepository jpaArticleRepository;
    private final MybatisArticleRepository mybatisArticleRepository;

    @Override
    public List<Article> findTop6ByOrderByHitDesc() {
        return jpaArticleRepository.findTop6ByOrderByHitDesc();
    }

    @Override
    public List<Article> findTop6ByCategoryOrderByIdDesc(Category category) {
        return jpaArticleRepository.findTop6ByCategoryOrderByIdDesc(category);
    }

    @Override
    public Slice<Article> findByOrderByIdDesc(Pageable pageable) {
        return jpaArticleRepository.findByOrderByIdDesc(pageable);
    }

    @Override
    public List<Article> findByOrderByIdDescWithList(Pageable pageable) {
        return jpaArticleRepository.findByOrderByIdDescWithList(pageable);
    }

    @Override
    public List<Article> findByOrderByIdDesc(Long articleId, Pageable pageable) {
        return jpaArticleRepository.findByOrderByIdDesc(articleId, pageable);
    }

    @Override
    public Slice<Article> findBySubCategoryOrderByIdDesc(Pageable pageable, String category) {
        return jpaArticleRepository.findBySubCategoryOrderByIdDesc(pageable,category);
    }

    @Override
    public Slice<Article> findBySuperCategoryOrderByIdDesc(Pageable pageable, String category) {
        return jpaArticleRepository.findBySupCategoryOrderByIdDesc(pageable,category);
    }

    @Override
    public Article findArticleByIdFetchCategoryAndTags(Long articleId) {
        return jpaArticleRepository.findArticleByIdFetchCategoryAndTags(articleId);
    }

    @Override
    public Page<Article> findAllByArticleTagsOrderById(Pageable pageable, String tag) {
        return jpaArticleRepository.findAllByArticleTagsOrderById(pageable,tag);
    }

    @Override
    public Page<Article> findAllByKeywordOrderById(Pageable pageable, String keyword) {
        return jpaArticleRepository.findAllByArticleTagsOrderById(pageable, keyword);
    }

    @Override
    public List<Article> findAllByOrderByIdDesc() {
        return jpaArticleRepository.findAllByOrderByIdDesc();
    }

    @Override
    public Optional<Article> findById(Long articleId) {
        return jpaArticleRepository.findById(articleId);
    }

    @Override
    public void save(Article newArticle) {
        jpaArticleRepository.save(newArticle);
    }

    @Override
    public void deleteArticle(Long articleId) {
        mybatisArticleRepository.deleteArticle(articleId);
    }
}
