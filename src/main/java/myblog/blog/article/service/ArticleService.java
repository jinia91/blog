package myblog.blog.article.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.article.dto.ArticleDtoForMainView;
import myblog.blog.article.dto.NewArticleForm;
import myblog.blog.article.repository.ArticleRepository;
import myblog.blog.category.dto.CategoryForView;
import myblog.blog.category.service.CategoryService;
import myblog.blog.member.doamin.Member;
import myblog.blog.member.repository.MemberRepository;
import myblog.blog.tags.service.TagsService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository
            articleRepository;
    private final MemberRepository memberRepository;
    private final TagsService tagsService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    public Long writeArticle(NewArticleForm articleDto) {

        Article newArticle = createNewArticleFrom(articleDto);

        articleRepository.save(newArticle);
        tagsService.createNewTagsAndArticleTagList(articleDto.getTags(), newArticle);

        return newArticle.getId();

    }

    public List<ArticleDtoForMainView> getPopularArticles() {
        List<Article> top6ByOrderByHitDesc = articleRepository.findTop6ByOrderByHitDesc();

        List<ArticleDtoForMainView> articles = new ArrayList<>();

        for (Article article : top6ByOrderByHitDesc) {
            articles.add(modelMapper.map(article, ArticleDtoForMainView.class));
        }


        return articles;

    }

    public Slice<ArticleDtoForMainView> getArticles(int page) {

        Slice<ArticleDtoForMainView> articles = articleRepository
                .findByOrderByIdDesc(PageRequest.of(page, 5))
                .map(article -> modelMapper
                        .map(article, ArticleDtoForMainView.class));
        return articles;

    }

    public int getTotalArticleCntByCategory(String category, CategoryForView categorys) {

        if (categorys.getTitle().equals(category)) {
            return categorys.getCount();
        } else {
            for (CategoryForView categoryCnt :
                    categorys.getCategoryTCountList()) {
                if (categoryCnt.getTitle().equals(category))
                    return categoryCnt.getCount();
                for (CategoryForView categoryCntSub : categoryCnt.getCategoryTCountList()) {
                    if (categoryCntSub.getTitle().equals(category))
                        return categoryCntSub.getCount();
                }
            }
        }
        throw new IllegalArgumentException("카테고리별 아티클 수 에러");
    }

    public Slice<ArticleDtoForMainView> getArticles(String category, Integer tier, Integer page) {

        Slice<Article> articles = null;

        if (tier.intValue() == 0) {
            articles = articleRepository
                    .findAllByOrderByIdDesc(
                            PageRequest.of(pageResolver(page), 5));
        } else if (tier.intValue() == 1) {
            articles = articleRepository
                    .findByT1CategoryOrderByIdDesc(
                            PageRequest.of(pageResolver(page), 5), category);

        } else {
            articles = articleRepository
                    .findByCategoryOrderByIdDesc(
                            PageRequest.of(pageResolver(page), 5), category);
        }

        return articles.map(article -> modelMapper
                .map(article, ArticleDtoForMainView.class));
    }

    public Article findArticleById(Long id){
         return articleRepository.findArticleByIdFetchCategory(id);
    }


    private int pageResolver(Integer rawPage) {
        if (rawPage == null || rawPage == 1) {
            return 0;
        } else return rawPage - 1;
    }

    private Article createNewArticleFrom(NewArticleForm articleDto) {
        Member member =
                memberRepository.findById(articleDto.getMemberId()).orElseThrow(() -> {
                    throw new IllegalArgumentException("작성자를 확인할 수 없습니다");
                });

        return Article.builder()
                .title(articleDto.getTitle())
                .content(articleDto.getContent())
                .toc(articleDto.getToc())
                .thumbnailUrl(makeDefaultThumb(articleDto.getThumbnailUrl()))
                .category(categoryService.findCategory(articleDto.getCategory()))
                .member(member)
                .build();
    }

    private String makeDefaultThumb(String thumbnailUrl) {
        // 메시지로 올리기
        String defaultThumbUrl = "https://cdn.pixabay.com/photo/2020/11/08/13/28/tree-5723734_1280.jpg";

        if (thumbnailUrl == null || thumbnailUrl.equals("")) {
            thumbnailUrl = defaultThumbUrl;
        }
        return thumbnailUrl;
    }




}
