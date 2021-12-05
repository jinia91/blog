package myblog.blog.article.domain;

import lombok.Builder;
import lombok.Getter;
import myblog.blog.article.dto.ArticleForm;
import myblog.blog.base.domain.BasicEntity;
import myblog.blog.category.domain.Category;
import myblog.blog.comment.domain.Comment;
import myblog.blog.member.doamin.Member;
import myblog.blog.tags.domain.ArticleTagList;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false)
    private String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ArticleTagList> articleTagLists = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Comment> parentCommentList = new ArrayList<>();

    protected Article() {
    }

    @Builder
    public Article(String title, String content, String toc, Member member, String thumbnailUrl, Category category) {
        this.title = title;
        this.content = content;
        this.toc = toc;
        this.member = member;
        this.thumbnailUrl = thumbnailUrl;
        this.category = category;
        this.hit = 0L;
    }

    // 비지니스 로직 //

    public void addHit(){
        this.hit++;
    }

    public void editArticle(ArticleForm articleForm, Category category){
        this.content = articleForm.getContent();
        this.title = articleForm.getTitle();
        this.thumbnailUrl = articleForm.getThumbnailUrl();
        this.toc = articleForm.getToc();
        this.category = category;
    }

}
