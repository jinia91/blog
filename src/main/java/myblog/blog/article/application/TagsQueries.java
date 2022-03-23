package myblog.blog.article.application;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.application.port.outgoing.TagRepositoryPort;
import myblog.blog.article.domain.Tags;
import myblog.blog.shared.utils.MapperUtils;
import myblog.blog.article.dto.TagsDto;
import myblog.blog.article.adapter.outgoing.persistence.JpaTagsRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
public class TagsQueries {
    private final TagRepositoryPort tagRepositoryPort;

    public List<TagsDto> findAllTagDtos(){
        List<Tags> tags = tagRepositoryPort.findAll();
        return tags.stream()
                .map(tag -> MapperUtils.getModelMapper().map(tag, TagsDto.class))
                .collect(Collectors.toList());
    }
}
