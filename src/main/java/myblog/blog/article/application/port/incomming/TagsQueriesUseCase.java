package myblog.blog.article.application.port.incomming;

import myblog.blog.article.model.TagsDto;

import java.util.List;

public interface TagsQueriesUseCase {
    List<TagsDto> findAllTagDtos();
}
