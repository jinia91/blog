package myblog.blog.article.application.port.outgoing;

import myblog.blog.article.domain.Tags;

import java.util.List;

public interface TagRepositoryPort {
    List<Tags> findAll();
}
