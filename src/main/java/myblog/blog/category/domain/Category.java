package myblog.blog.category.domain;


import lombok.Builder;
import lombok.Getter;
import myblog.blog.article.domain.Article;
import myblog.blog.base.domain.BasicEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SequenceGenerator(
        name = "CATEGORY_SEQ_GENERATOR",
        sequenceName = "CATEGORY_SEQ",
        initialValue = 1, allocationSize = 50)
public class Category extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORY_SEQ_GENERATOR")
    @Column(name = "category_id")
    private Long id;

    private String title;

    @OneToMany(mappedBy = "category")
    private List<Article> articles = new ArrayList<>();

    @Column(nullable = false)
    private int tier;

    // 셀프조인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parents_id")
    private Category parents;

    @OneToMany(mappedBy = "parents")
    private List<Category> child = new ArrayList<>();

    @Builder
    public Category(String title, Category parents, int tier) {
        this.title = title;
        this.parents = parents;
        this.tier = tier;
    }

    protected Category() {
    }
}
