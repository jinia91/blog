package myblog.blog.article.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.article.dto.ArticleForMainView;
import myblog.blog.article.dto.NewArticleDto;
import myblog.blog.article.repository.ArticleRepository;
import myblog.blog.category.service.CategoryService;
import myblog.blog.member.doamin.Member;
import myblog.blog.member.repository.MemberRepository;
import myblog.blog.member.service.Oauth2MemberService;
import myblog.blog.tags.service.TagsService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
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
    private final Oauth2MemberService memberService;
    private final ModelMapper modelMapper;

    public Long writeArticle(NewArticleDto articleDto) {

        Article newArticle = createNewArticleFrom(articleDto);

        articleRepository.save(newArticle);
        tagsService.createNewTagsAndArticleTagList(articleDto.getTags(), newArticle);

        return newArticle.getId();

    }

    public List<ArticleForMainView> getPopularArticles() {
        List<Article> top6ByOrderByHitDesc = articleRepository.findTop6ByOrderByHitDesc();

        List<ArticleForMainView> articles = new ArrayList<>();

        for (Article article : top6ByOrderByHitDesc) {
            articles.add(modelMapper.map(article, ArticleForMainView.class));
        }


        return articles;

    }

    public Slice<ArticleForMainView> getArticles(int page) {

        Slice<ArticleForMainView> articles = articleRepository
                .findByOrderByIdDesc(PageRequest.of(page, 5))
                .map(article -> modelMapper
                        .map(article, ArticleForMainView.class));
        return articles;

    }

    public Page<ArticleForMainView> getArticles(String category, Integer tier, Integer page) {

        Page<Article> articles = null;

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
                .map(article, ArticleForMainView.class));
    }

    private int pageResolver(Integer rawPage) {
        if (rawPage == null || rawPage == 1) {
            return 0;
        } else return rawPage - 1;
    }

    private Article createNewArticleFrom(NewArticleDto articleDto) {
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
