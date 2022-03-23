package myblog.blog.article.domain;

import lombok.Getter;
import myblog.blog.shared.BasicEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(
        name = "TAGS_SEQ_GENERATOR",
        sequenceName = "TAGS_SEQ",
        initialValue = 1, allocationSize = 50)
@Getter
public class Tags extends BasicEntity {

    protected Tags() {}

    @Id
    @Column(name = "tags_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAGS_SEQ_GENERATOR")
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String name;

    @OneToMany(mappedBy = "tags")
    private List<ArticleTagList> articleTagLists = new ArrayList<>();

    public Tags(String name) {
        this.name = name;
    }
}