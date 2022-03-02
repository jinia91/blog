package myblog.blog.article.domain;

import lombok.Builder;
import lombok.Getter;
import myblog.blog.article.dto.ArticleForm;
import myblog.blog.infra.BasicEntity;
import myblog.blog.category.domain.Category;
import myblog.blog.comment.domain.Comment;
import myblog.blog.member.doamin.Member;
import myblog.blog.tags.domain.ArticleTagList;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.*;


/*
    - 아티클 Entity
        -  toc 추후 개발 예정
        - 채번을 배치로 하게 하여 성능향상을 시켰고
          네트워크를 두번타는 identity 대신 table 방식으로 구현된 시퀸스 방식을 채택하여 배치 인서트의 확장성을 열어둠
*/
@Entity
@SequenceGenerator(
        name = "ARTICLE_SEQ_GENERATOR",
        sequenceName = "ARTICLE_SEQ",
        initialValue = 1, allocationSize = 50)
/*
    - fts 구현을 위한 인덱스 설정
*/
@Table(indexes = {
        @Index(name="i_article_title", columnList = "title"),
        @Index(name = "i_article_content", columnList = "content")
})
@Getter
public class Article extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ARTICLE_SEQ_GENERATOR")
    @Column(name = "article_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 10000)
    private String content;

    @Column(columnDefinition = "bigint default 0",nullable = false)
    private Long hit;

    // TODO: 추후 개발 예정
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
        this.thumbnailUrl = makeDefaultThumbOf(thumbnailUrl);
        this.category = category;
        this.hit = 0L;
    }

    /*
        - 아티클 수정을 위한 로직
    */
    public void isEditedFrom(ArticleForm articleForm, Category category){
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
    private String makeDefaultThumbOf(String thumbnailUrl) {
        String defaultThumbUrl = "https://cdn.pixabay.com/photo/2020/11/08/13/28/tree-5723734_1280.jpg";

        if (thumbnailUrl == null || thumbnailUrl.equals("")) {
            thumbnailUrl = defaultThumbUrl;
        }
        return thumbnailUrl;
    }
}
