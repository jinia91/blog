package myblog.blog.article.repository;

import myblog.blog.article.domain.Article;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleRepositoryTest {

    @Autowired
    ArticleRepository articleRepository;

    @Test
    public void 카테고리별검색() throws Exception {
        // given
        PageRequest of = PageRequest.of(0, 5);


        // when

        Slice<Article> articles = articleRepository.findByCategoryOrderByIdDesc(of, "child");

        // then

        assertThat(articles.getContent()).isNotNull();

    }

}