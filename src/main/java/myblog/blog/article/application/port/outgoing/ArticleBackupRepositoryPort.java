package myblog.blog.article.application.port.outgoing;

import myblog.blog.article.domain.Article;

public interface ArticleBackupRepositoryPort {
    public void backup(Article article);
}
