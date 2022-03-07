package myblog.blog.tags.queries;

import lombok.RequiredArgsConstructor;
import myblog.blog.shared.utils.MapperUtils;
import myblog.blog.tags.domain.Tags;
import myblog.blog.tags.dto.TagsDto;
import myblog.blog.tags.repository.ArticleTagListsRepository;
import myblog.blog.tags.repository.TagsRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
public class TagsQueries {
    private final TagsRepository tagsRepository;
    private final ArticleTagListsRepository articleTagListsRepository;

    public List<TagsDto> findAllTagDtos(){
        List<Tags> tags = tagsRepository.findAll();
        return tags.stream()
                .map(tag -> MapperUtils.getModelMapper().map(tag, TagsDto.class))
                .collect(Collectors.toList());
    }
}
