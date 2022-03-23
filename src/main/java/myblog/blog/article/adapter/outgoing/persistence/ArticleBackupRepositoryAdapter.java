package myblog.blog.article.adapter.outgoing.persistence;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.application.port.outgoing.ArticleBackupRepositoryPort;
import myblog.blog.article.domain.Article;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleBackupRepositoryAdapter implements ArticleBackupRepositoryPort {

    private final GithubRepoArticleRepository githubRepoArticleRepository;

    @Override
    public void backup(Article article) {githubRepoArticleRepository.pushArticleToGithub(article);}
}
