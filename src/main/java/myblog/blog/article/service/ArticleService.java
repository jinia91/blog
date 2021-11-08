package myblog.blog.article.service;

import lombok.RequiredArgsConstructor;
import myblog.blog.article.domain.Article;
import myblog.blog.article.dto.NewArticleDto;
import myblog.blog.article.repository.ArticleRepository;
import myblog.blog.member.doamin.Member;
import myblog.blog.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    public Long writeArticle(NewArticleDto articleDto) {

        Article newArticle = createNewArticleFrom(articleDto);
        articleRepository.save(newArticle);
        return newArticle.getId();

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
                .member(member)
                .build();
    }
}
