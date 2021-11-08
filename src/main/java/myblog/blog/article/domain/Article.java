package myblog.blog.article.domain;

import lombok.Builder;
import lombok.Getter;
import myblog.blog.base.domain.BasicEntity;
import myblog.blog.member.doamin.Member;

import javax.persistence.*;

@Entity
@Getter
@SequenceGenerator(
        name = "ARTICLE_SEQ_GENERATOR",
        sequenceName = "ARTICLE_SEQ",
        initialValue = 1, allocationSize = 50)
public class Article extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ARTICLE_SEQ_GENERATOR")
    @Column(name = "article_id")
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false, length = 10000)
    private String content;
    @Column(columnDefinition = "bigint default 0",nullable = false)
    private Long hit;
    private String toc;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    private String thumbnailUrl;

    protected Article() {
    }

    @Builder
    public Article(String title, String content, String toc, Member member) {
        this.title = title;
        this.content = content;
        this.toc = toc;
        this.member = member;
        this.hit = 0L;
    }
}
