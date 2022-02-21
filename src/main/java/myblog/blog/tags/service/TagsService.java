package myblog.blog.tags.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.tags.domain.ArticleTagList;
import myblog.blog.tags.domain.Tags;
import myblog.blog.tags.repository.ArticleTagListsRepository;
import myblog.blog.tags.repository.TagsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        ArrayList<Map<String,String>> tagsDtoArrayList = gson.fromJson(names, ArrayList.class);

        // JsonString -> tag
        for (Map<String,String> tags : tagsDtoArrayList) {

            // 신규태그인경우 저장 아닌경우 그대로 조회
            Tags tag =
                    tagsRepository
                    .findByName(tags.get("value"))
                    .orElseGet(() ->
                            tagsRepository
                            .save(Tags.builder().name(tags.get("value")).build()));

            // 아티클 연관 태그로 저장
            articleTagListsRepository.save(ArticleTagList.builder()
                    .article(article)
                    .tags(tag).build());
        }
    }

    /*
        - 전체 태그 조회
    */
    public List<Tags> findAllTags(){
        return tagsRepository.findAll();
    }

    /*
        - 아티클 연관 태그 모두 삭제
    */
    public void deleteArticleTags(Article article){
        articleTagListsRepository.deleteByArticle(article);
    }
}
