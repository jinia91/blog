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


/*
    - 아티클 Entity
        -  toc 추후 개발 예정
*/
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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "bigint default 0",nullable = false)
    private Long hit;

    // 추후 개발 예정
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
        this.thumbnailUrl = makeDefaultThumb(thumbnailUrl);
        this.category = category;
        this.hit = 0L;
    }

    // 비지니스 로직 //

    /*
        - 아티클 수정을 위한 로직
    */
    public void editArticle(ArticleForm articleForm, Category category){
        this.content = articleForm.getContent();
        this.title = articleForm.getTitle();
        this.toc = articleForm.getToc();
        this.category = category;

        if(articleForm.getThumbnailUrl() != null){
            this.thumbnailUrl = articleForm.getThumbnailUrl();
        }
    }
    /*
        - 아티클 조회수 증가
    */
    public void addHit(){
        this.hit++;
    }

    /*
        - 썸네일 기본 작성
    */
    private String makeDefaultThumb(String thumbnailUrl) {
        String defaultThumbUrl = "https://cdn.pixabay.com/photo/2020/11/08/13/28/tree-5723734_1280.jpg";

        if (thumbnailUrl == null || thumbnailUrl.equals("")) {
            thumbnailUrl = defaultThumbUrl;
        }
        return thumbnailUrl;
    }
}
