package myblog.blog.article.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.article.repository.ArticleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;

    public Long writeArticle(NewArticleDto newArticleDto){

        Article article = modelMapper.map(newArticleDto, Article.class);

        articleRepository.save(article);

        return article.getId();

    }




}
