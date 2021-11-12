package myblog.blog.article.service;

import myblog.blog.article.domain.Article;
import myblog.blog.article.repository.ArticlePagingRepository;
import myblog.blog.article.repository.ArticleRepository;
import myblog.blog.category.domain.Category;
import myblog.blog.category.dto.CategoryNormalDto;
import myblog.blog.category.dto.CategoryForMainView;
import myblog.blog.category.repository.CategoryRepository;
import myblog.blog.category.repository.NaCategoryRepository;
import myblog.blog.category.service.CategoryService;
import myblog.blog.member.repository.MemberRepository;
import myblog.blog.member.service.Oauth2MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;


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
    @Autowired
    ArticlePagingRepository articlePagingRepository;
    @Autowired
    Oauth2MemberService memberService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    NaCategoryRepository naCategoryRepository;

//    @BeforeEach
    void 더미게시글() {
        memberService.insertAdmin();

        Category ca1 = Category.builder()
                .title("CA1")
                .tier(1)
                .build();
        Category category1 = ca1;
        Category ca2 = Category.builder()
                .title("CA2")
                .tier(1)
                .build();
        Category category2 = ca2;
        Category category3 = Category.builder()
                .title("Java")
                .tier(2)
                .parents(ca1)
                .build();
        Category category4 = Category.builder()
                .title("Spring")
                .parents(ca1)
                .tier(2)
                .build();
        Category category5 = Category.builder()
                .title("Jpa")
                .parents(ca2)
                .tier(2)
                .build();

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        categoryRepository.save(category4);
        categoryRepository.save(category5);

        String[] arr = {"Java", "Spring", "Jpa"};

        int n = 100;
        while (n-- > 0) {

            articleRepository.save(Article.builder()
                    .title(UUID.randomUUID().toString())
                    .content(String.valueOf(100 - n))
                    .thumbnailUrl("https://picsum.photos/600/59" + (int) (Math.random() * 10))
                    .category(categoryService.findCategory(arr[(int) (Math.random() * 3)]))
                    .member(memberRepository.findById(1L).get())
                    .build());

        }
    }

//    @Test
//    public void 게시글테스트() throws Exception {

//        List<Article> popularArticle = articleRepository.findTop6ByOrderByHitDesc();
//
//        for (Article article : popularArticle) {
//            System.out.println("article.getId() = " + article.getId());
//        }
//
//        List<Article> top5ByOOrderByCreatedDateDesc = articleRepository.findTop5ByOrderByCreatedDateDesc();
//
//        for (Article article : top5ByOOrderByCreatedDateDesc) {
//            System.out.println("article.getCreatedDate() = " + article.getCreatedDate());
//
//
//        }

//        entityManager.clear();

//        PageRequest createdDate = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdDate"));
//
//        Slice<Article> articles = articleRepository.findByOrderByCreatedDateDesc(createdDate);
//
//        List<Article> content = articles.getContent();
//
//        for (Article article : content) {
//            System.out.println("article.getCreatedDate() = " + article.getCreatedDate());
//            System.out.println("article.getContent() = " + article.getContent());
//        }
//
//
//        System.out.println(articles.getNumber());
//        System.out.println(articles.getNumberOfElements());
//        System.out.println(articles.hasNext());
//    }
//

    @Test
    public void 더미데이터테스트() throws Exception {



        List<CategoryNormalDto> categoryNormalDto = naCategoryRepository.getCategoryCount();

//        for (CategoryCountForRepository count : categoryCountForRepository) {
//
//            System.out.println("tier "+ count.getTier() + count.getTitle() +"(" + count.getCount()+")");
//
//        }
//

        CategoryForMainView category = CategoryForMainView.createCategory(categoryNormalDto);

        System.out.println("t1. " + category.getTitle() + "(" + category.getCount()+")");

        List<CategoryForMainView> categoryTCountList = category.getCategoryTCountList();
        for (CategoryForMainView categoryForMainView : categoryTCountList) {
            System.out.println("ㄴt2. " + categoryForMainView.getTitle() + "(" + categoryForMainView.getCount()+")");
            List<CategoryForMainView> categoryTCountList1 = categoryForMainView.getCategoryTCountList();
            for (CategoryForMainView countForView : categoryTCountList1) {
                System.out.println("  ㄴt3. " + countForView.getTitle() + "(" + countForView.getCount() +")");
            }

        }

    }

    @Test
    public void 카테고리테스트() throws Exception {
    // given

        articleRepository.save(Article.builder()
                .title(UUID.randomUUID().toString())
                .content(String.valueOf(166))
                .thumbnailUrl("https://picsum.photos/600/59" + (int) (Math.random() * 10))
                .category(categoryService.findCategory("child"))
                .member(memberRepository.findById(1L).get())
                .build());

        articleRepository.save(Article.builder()
                .title(UUID.randomUUID().toString())
                .content(String.valueOf(133))
                .thumbnailUrl("https://picsum.photos/600/59" + (int) (Math.random() * 10))
                .category(categoryService.findCategory("child"))
                .member(memberRepository.findById(1L).get())
                .build());

        // when

    // then
    }


}