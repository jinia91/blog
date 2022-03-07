package myblog.blog.tags.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import myblog.blog.article.domain.*;
import myblog.blog.tags.domain.*;
import myblog.blog.shared.utils.MapperUtils;
import myblog.blog.tags.repository.*;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TagsService {
    private final TagsRepository tagsRepository;
    private final ArticleTagListsRepository articleTagListsRepository;
    /*
        - Json 객체로 넘어온 태그들을 파싱해서 신규 태그인경우 저장
    */
    public void createNewTagsAndArticleTagList(String names, Article article) {
        List<Map<String,String>> tagsDtoArrayList = MapperUtils.getGson().fromJson(names, ArrayList.class);
        for (var tagDto : tagsDtoArrayList) {
            Tags tag = findOrCreateTagFrom(tagDto);
            articleTagListsRepository.save(new ArticleTagList(article, tag));
        }
    }

    private Tags findOrCreateTagFrom(Map<String, String> tags) {
        return tagsRepository.findByName(tags.get("value"))
        .orElseGet(() -> tagsRepository.save(new Tags(tags.get("value"))));
    }

    public void deleteAllTagsWith(Article article){
        articleTagListsRepository.deleteByArticle(article);
    }
}
