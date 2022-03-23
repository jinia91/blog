package myblog.blog.article.application.port.incomming;

import myblog.blog.article.application.port.response.TagsResponse;

import java.util.List;

public interface TagsQueriesUseCase {
    List<TagsResponse> findAllTagDtos();
}
