package myblog.blog.tags.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.tags.domain.*;
import myblog.blog.tags.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TagsService {

    private final TagsRepository tagsRepository;
    private final ArticleTagListsRepository articleTagListsRepository;
    private final Gson gson;
    /*
        - Json 객체로 넘어온 태그들을 파싱해서 신규 태그인경우 저장
    */
    public void createNewTagsAndArticleTagList(String names, Article article) {
        List<Map<String,String>> tagsDtoArrayList = gson.fromJson(names, ArrayList.class);
        for (var tagDto : tagsDtoArrayList) {
            Tags tag = findOrCreateTagFrom(tagDto);
            articleTagListsRepository.save(new ArticleTagList(article, tag));
        }
    }

    private Tags findOrCreateTagFrom(Map<String, String> tags) {
        return tagsRepository.findByName(tags.get("value"))
        .orElseGet(() -> tagsRepository.save(new Tags(tags.get("value"))));
    }

    public List<Tags> findAllTags(){
        return tagsRepository.findAll();
    }

    public void deleteAllTagsWith(Article article){
        articleTagListsRepository.deleteByArticle(article);
    }
}
