package myblog.blog.article.adapter.outgoing.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import myblog.blog.article.application.port.outgoing.TempArticleRepositoryPort;
import myblog.blog.article.domain.TempArticle;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TempArticleRepositoryAdapter implements TempArticleRepositoryPort {

    private final JpaTempArticleRepository jpaTempArticleRepository;

    @Override
    public void save(TempArticle tempArticle) {
        jpaTempArticleRepository.save(tempArticle);
    }

    @Override
    public Optional<TempArticle> findById(long id) {
        return jpaTempArticleRepository.findById(id);
    }

    @Override
    public void delete(TempArticle tempArticle) {
        jpaTempArticleRepository.delete(tempArticle);
    }
}
