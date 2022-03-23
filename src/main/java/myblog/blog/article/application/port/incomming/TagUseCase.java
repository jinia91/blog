package myblog.blog.article.application.port.incomming;

import myblog.blog.article.domain.Article;

public interface TagUseCase {
    void createNewTagsAndArticleTagList(String names, Article article);
    void deleteAllTagsWith(Article article);
}
