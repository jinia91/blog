package myblog.blog.article.domain;

import lombok.Getter;
import myblog.blog.shared.BasicEntity;

import javax.persistence.*;

/*
            - 다 대 다 연관관계 해소 엔티티
        */
@Entity
@Getter
@SequenceGenerator(
        name = "ARTICLE_TAG_LIST_SEQ_GENERATOR",
        sequenceName = "ARTICLE_TAG_LIST_SEQ",
        initialValue = 1, allocationSize = 50)
public class ArticleTagList extends BasicEntity {

    @Id
    @Column(name = "article_tag_list_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ARTICLE_TAG_LIST_SEQ_GENERATOR")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "tags_id")
    private Tags tags;

    public ArticleTagList(Article article, Tags tags) {
        this.article = article;
        this.tags = tags;
    }

    protected ArticleTagList() {}
}
