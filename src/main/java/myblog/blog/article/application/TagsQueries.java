package myblog.blog.article.application;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.application.port.outgoing.TagRepositoryPort;
import myblog.blog.article.domain.Tags;
import myblog.blog.article.application.port.incomming.TagsQueriesUseCase;
import myblog.blog.shared.utils.MapperUtils;
import myblog.blog.article.application.port.incomming.response.TagsResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagsQueries implements TagsQueriesUseCase {
    private final TagRepositoryPort tagRepositoryPort;
    private final ArticleDtoMapper articleDtoMapper;

    public List<TagsResponse> findAllTagDtos(){
        var tags = tagRepositoryPort.findAll();
        return tags.stream()
                .map(articleDtoMapper::of)
                .collect(Collectors.toList());
    }
}
