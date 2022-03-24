package myblog.blog.category.domain;


import lombok.Builder;
import lombok.Getter;

import myblog.blog.shared.BasicEntity;
import myblog.blog.article.domain.Article;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/*
    - 카테고리 엔티티
*/
@Entity
@Getter
@SequenceGenerator(
        name = "CATEGORY_SEQ_GENERATOR",
        sequenceName = "CATEGORY_SEQ",
        initialValue = 0, allocationSize = 50)
public class Category extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORY_SEQ_GENERATOR")
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String title;

    @OneToMany(mappedBy = "category")
    private List<Article> articles = new ArrayList<>();

    @Column(nullable = false)
    private int tier;

    private int pSortNum;
    private int cSortNum;

    // 셀프조인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parents_id")
    private Category parents;

    @OneToMany(mappedBy = "parents")
    private List<Category> child = new ArrayList<>();

    @Builder
    public Category(String title, Category parents, int tier, int pSortNum, int cSortNum) {
        this.title = title;
        this.parents = parents;
        this.tier = tier;
        this.pSortNum = pSortNum;
        this.cSortNum = cSortNum;
    }

    protected Category() {
    }

    @Override
    public String toString() {
        return title;
    }

    // 도메인 비지니스 로직

    /*
        - 카테고리 더티체킹 업데이트
    */
    public void updateCategory(String title, int tier, int pSortNum, int cSortNum, Category parents){
        this.title = title;
        this.tier = tier;
        this.pSortNum = pSortNum;
        this.cSortNum = cSortNum;
        this.parents = parents;
    }

}
