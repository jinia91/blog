package myblog.blog.article.application;

import lombok.RequiredArgsConstructor;

import myblog.blog.article.application.port.incomming.request.ArticleCreateRequest;
import myblog.blog.article.application.port.incomming.request.ArticleEditRequest;
import myblog.blog.article.application.port.incomming.ArticleUseCase;
import myblog.blog.article.application.port.incomming.TagUseCase;
import myblog.blog.category.appliacation.port.incomming.CategoryUseCase;
import myblog.blog.member.application.port.incomming.MemberUseCase;
import myblog.blog.article.application.port.outgoing.ArticleBackupRepositoryPort;
import myblog.blog.article.application.port.outgoing.ArticleRepositoryPort;

import myblog.blog.article.domain.Article;
import myblog.blog.category.domain.Category;
import myblog.blog.member.doamin.Member;


import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleService implements ArticleUseCase {

    private final TagUseCase tagUseCase;
    private final CategoryUseCase categoryUseCase;
    private final MemberUseCase memberUseCase;
    private final ArticleRepositoryPort articleRepositoryPort;
    private final ArticleBackupRepositoryPort articleBackupRepositoryPort;

    @Override
    @CacheEvict(value = {"layoutCaching", "layoutRecentArticleCaching","seoCaching"}, allEntries = true)
    public Long writeArticle(ArticleCreateRequest articleCreateRequest) {
        Member writer = memberUseCase.findById(articleCreateRequest.getMemberId());
        Category category = categoryUseCase.findCategory(articleCreateRequest.getCategory());
        Article newArticle = new Article(articleCreateRequest.getTitle(),
                articleCreateRequest.getContent(),
                articleCreateRequest.getToc(),
                writer,
                articleCreateRequest.getThumbnailUrl(),
                category);
        articleRepositoryPort.save(newArticle);
        tagUseCase.createNewTagsAndArticleTagList(articleCreateRequest.getTags(), newArticle);
        return newArticle.getId();
    }

    @Override
    @CacheEvict(value = {"layoutCaching", "layoutRecentArticleCaching","seoCaching"}, allEntries = true)
    public void editArticle(ArticleEditRequest articleEditRequest) {
        Article article = articleRepositoryPort.findById(articleEditRequest.getArticleId())
                .orElseThrow(() -> new IllegalArgumentException("NotFoundArticleException"));
        Category category = categoryUseCase.findCategory(articleEditRequest.getCategoryName());
        tagUseCase.deleteAllTagsWith(article);
        tagUseCase.createNewTagsAndArticleTagList(articleEditRequest.getTags(), article);
        article.edit(articleEditRequest.getContent(),
                articleEditRequest.getTitle(),
                articleEditRequest.getToc(),
                articleEditRequest.getThumbnailUrl(), category);
    }

    @Override
    @CacheEvict(value = {"layoutCaching", "layoutRecentArticleCaching","seoCaching"}, allEntries = true)
    public void deleteArticle(Long articleId) {
        articleRepositoryPort.deleteArticle(articleId);
    }

    @Override
    public void backupArticle(Long articleId) {
        Article article = articleRepositoryPort.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("NotFoundArticle"));
        articleBackupRepositoryPort.backup(article);
    }

    @Override
    public void addHit(Long articleId) {
        Article article = articleRepositoryPort.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("NotFoundArticleException"));
        article.addHit();
    }

    /*
    - 아티클 도메인으로 반환
    */
    @Override
    public Article getArticle(Long id){
        return articleRepositoryPort.findArticleByIdFetchCategoryAndTags(id);
    }
    /*
        - 모든 아티클 도메인으로 반환
    */
    @Override
    public List<Article> getTotalArticle(){
        return articleRepositoryPort.findAllByOrderByIdDesc();
    }
}
