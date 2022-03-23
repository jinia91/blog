package myblog.blog.article.application;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.application.port.outgoing.TagRepositoryPort;
import myblog.blog.article.domain.Tags;
import myblog.blog.article.application.port.incomming.TagsQueriesUseCase;
import myblog.blog.shared.utils.MapperUtils;
import myblog.blog.article.application.port.response.TagsResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
public class TagsQueries implements TagsQueriesUseCase {
    private final TagRepositoryPort tagRepositoryPort;

    public List<TagsResponse> findAllTagDtos(){
        List<Tags> tags = tagRepositoryPort.findAll();
        return tags.stream()
                .map(tag -> MapperUtils.getModelMapper().map(tag, TagsResponse.class))
                .collect(Collectors.toList());
    }
}
