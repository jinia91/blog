package myblog.blog.article.application;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.adapter.outgoing.persistence.JpaArticleTagListsRepository;
import myblog.blog.article.adapter.outgoing.persistence.JpaTagsRepository;
import myblog.blog.article.application.port.incomming.TagUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import myblog.blog.article.domain.*;
import myblog.blog.shared.utils.MapperUtils;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TagsService implements TagUseCase {
    private final JpaTagsRepository jpaTagsRepository;
    private final JpaArticleTagListsRepository articleTagListsRepository;
    /*
        - Json 객체로 넘어온 태그들을 파싱해서 신규 태그인경우 저장
    */
    @Override
    public void createNewTagsAndArticleTagList(String names, Article article) {
        List<Map<String,String>> tagsDtoArrayList = MapperUtils.getGson().fromJson(names, ArrayList.class);
        for (var tagDto : tagsDtoArrayList) {
            var tag = findOrCreateTagFrom(tagDto);
            articleTagListsRepository.save(new ArticleTagList(article, tag));
        }
    }

    private Tags findOrCreateTagFrom(Map<String, String> tags) {
        return jpaTagsRepository.findByName(tags.get("value"))
        .orElseGet(() -> jpaTagsRepository.save(new Tags(tags.get("value"))));
    }

    @Override
    public void deleteAllTagsWith(Article article){
        articleTagListsRepository.deleteByArticle(article);
    }
}
