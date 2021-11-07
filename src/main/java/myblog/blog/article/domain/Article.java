package myblog.blog.article.domain;

import lombok.Getter;
import myblog.blog.member.doamin.Member;

import javax.persistence.*;

@Entity
@Getter
@SequenceGenerator(
        name = "article_seq_generator",
        sequenceName = "article_seq",
        initialValue = 1, allocationSize = 50)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "article_generator")
    @Column(name = "article_id")
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    private Long hit;
    private String toc;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;



}
