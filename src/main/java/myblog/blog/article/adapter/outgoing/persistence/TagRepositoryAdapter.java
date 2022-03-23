package myblog.blog.article.adapter.outgoing.persistence;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.application.port.outgoing.TagRepositoryPort;
import myblog.blog.article.domain.Tags;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TagRepositoryAdapter implements TagRepositoryPort {

    private final JpaTagsRepository jpaTagsRepository;

    @Override
    public List<Tags> findAll() {
        return jpaTagsRepository.findAll();
    }
}
