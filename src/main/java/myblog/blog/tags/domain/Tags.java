package myblog.blog.tags.domain;

import lombok.Builder;
import lombok.Getter;
import myblog.blog.base.domain.BasicEntity;

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

    @Id
    @Column(name = "tags_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAGS_SEQ_GENERATOR")
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @OneToMany(mappedBy = "tags")
    private List<ArticleTagList> articleTagLists = new ArrayList<>();

    @Builder
    public Tags(String name) {
        this.name = name;
    }

    protected Tags() {

    }
}
