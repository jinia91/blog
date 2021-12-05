package myblog.blog.article.repository;

import myblog.blog.article.domain.Article;
import myblog.blog.category.domain.Category;
import myblog.blog.category.repository.CategoryRepository;
import myblog.blog.member.doamin.Member;
import myblog.blog.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.sql.BatchUpdateException;
import java.util.List;

@SpringBootTest
@Transactional
class ArticleRepositoryTest {

    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager entityManager;


    @BeforeEach
    void 카테고리더미삽입(){

        // 카테고리 삽입

        // 필수 삽입 더미 카테고리
        Category category0 = Category.builder()
                .tier(0)
                .title("total")
                .pSortNum(0)
                .cSortNum(0)
                .build();
        categoryRepository.save(category0);

        // 부모 카테고리
        Category category1 = Category.builder()
                .tier(1)
                .title("카테고리 부모")
                .pSortNum(1)
                .cSortNum(0)
                .build();
        categoryRepository.save(category1);

        // 자식 카테고리
        Category category2 = Category.builder()
                .tier(2)
                .title("카테고리 자식")
                .pSortNum(1)
                .cSortNum(1)
                .parents(category1)
                .build();
        categoryRepository.save(category2);

        // 멤버 삽입은 postConstruct로


    }

    @Test
    void 글작성성공테스트() throws Exception {

        Category category2 = categoryRepository.findByTitle("카테고리 자식");
        Member admin = memberRepository.findById(1L).get();

        Article newArticle = Article.builder()
                .category(category2)
                .content("테스트")
                .thumbnailUrl("더미")
                .title("테스트")
                .member(admin)
                .toc(null)
                .build();


        // when

        Article savedArticle = articleRepository.save(newArticle);
        Long articleId = savedArticle.getId();

        entityManager.flush();
        entityManager.clear();

        // then

        Assertions
                .assertThat(articleRepository.findById(articleId).isPresent()).isTrue();

        Assertions.assertThat(savedArticle.getContent())
                .isEqualTo(articleRepository.findById(articleId).get().getContent());

    }

    @Test
    public void 글작성실패() throws Exception {
        // given
        Category category2 = categoryRepository.findByTitle("카테고리 자식");
        Member admin = memberRepository.findById(1L).get();

//      타이틀 없는 아티클
        Article nullTitle = Article.builder()
                .category(category2)
                .content("테스트")
                .thumbnailUrl("더미")
//                .title("테스트")
                .member(admin)
                .toc(null)
                .build();

        //썸네일 없는 아티클
        Article nullThumbnail = Article.builder()
                .category(category2)
                .content("테스트")
//                .thumbnailUrl("더미")
                .title("테스트")
                .member(admin)
                .toc(null)
                .build();

        // 내용 없는 아티클
        Article nullContents = Article.builder()
                .category(category2)
//                .content("테스트")
                .thumbnailUrl("더미")
                .title("테스트")
                .member(admin)
                .toc(null)
                .build();

        // 카테고리 없는 아티클
        Article nullCategory = Article.builder()
//                .category(category2)
                .content("테스트")
                .thumbnailUrl("더미")
                .title("테스트")
                .member(admin)
                .toc(null)
                .build();

        // 작성자 없는 아티클
        Article nullWriter = Article.builder()
                .category(category2)
                .content("테스트")
                .thumbnailUrl("더미")
                .title("테스트")
//                .member(admin)
                .toc(null)
                .build();


        // when

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () ->
                {
                    articleRepository.save(nullTitle);
                    entityManager.flush();
                });
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () ->
                {
                    articleRepository.save(nullThumbnail);
                    entityManager.flush();
                });
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () ->
                {
                    articleRepository.save(nullContents);
                    entityManager.flush();
                });
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () ->
                {
                    articleRepository.save(nullCategory);
                    entityManager.flush();
                });
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () ->
                {
                    articleRepository.save(nullWriter);
                    entityManager.flush();
                });

    }




}