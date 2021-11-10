package myblog.blog.article.repository;

import myblog.blog.article.domain.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.Repository;

public interface ArticlePagingRepository extends Repository<Article,Long> {

    Slice<Article> findBy(Pageable pageable);

}
