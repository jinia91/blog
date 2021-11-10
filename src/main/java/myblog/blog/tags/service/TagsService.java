package myblog.blog.tags.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.article.repository.ArticleRepository;
import myblog.blog.tags.domain.ArticleTagList;
import myblog.blog.tags.domain.Tags;
import myblog.blog.tags.dto.TagsDto;
import myblog.blog.tags.repository.ArticleTagListsRepository;
import myblog.blog.tags.repository.TagsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class TagsService {

    private final TagsRepository tagsRepository;
    private final ArticleTagListsRepository articleTagListsRepository;

    public void createNewTagsAndArticleTagList(String names, Article article) {

        Gson gson = new Gson();
        ArrayList<Map> tagsDtoArrayList = gson.fromJson(names, ArrayList.class);

        for (Map tags : tagsDtoArrayList) {

            Tags tag = tagsRepository.findByName(tags.get("value").toString());
            if (tag == null) {
                tag = tagsRepository.save(Tags.builder().name(tags.get("value").toString()).build());

            }
            articleTagListsRepository.save(ArticleTagList.builder()
                    .article(article)
                    .tags(tag)
                    .build());
        }

    }
}
