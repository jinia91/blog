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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public Slice<ArticleForMainView> getRecentArticles(int page) {

        return articleRepository.findByOrderByCreatedDateDesc(PageRequest.of(page, 5))
                .map(article -> modelMapper.map(article, ArticleForMainView.class));

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
                .thumbnailUrl(articleDto.getThumbnailUrl())
                .category(categoryService.findCategory(articleDto.getCategory()))
                .member(member)
                .build();
    }

    /*--------------------------------------------------------------------------------------------*/


}
