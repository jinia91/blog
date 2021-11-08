package myblog.blog.article.service;

import myblog.blog.article.dto.NewArticleDto;
import myblog.blog.article.repository.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;


@SpringBootTest
@Transactional
@Rollback(value = false)
class ArticleServiceTest {

    @Autowired
    ArticleService articleService;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    public void 게시글테스트() throws Exception {
        // given
        NewArticleDto newArticleDto = new NewArticleDto();
        newArticleDto.setTitle("abs");
        newArticleDto.setMemberId(1L);
        newArticleDto.setToc("df");
        newArticleDto.setContent("sdfsf");

        // when
        Long articleId = articleService.writeArticle(newArticleDto);

        // then


        System.out.println(articleRepository.findById(articleId).get().getContent());

    }


}