package myblog.blog.article.domain;

import lombok.Getter;
import myblog.blog.base.BasicEntity;

import javax.persistence.*;

/*
    - 임시 아티클 저장 Entity
       - 임시 아티클은 한개만 유지할 예정
*/
@Entity
@Getter
public class TempArticle extends BasicEntity {

    @Id
    @Column(name = "temp_article_id")
    private Long id;

    @Column(nullable = false, length = 10000)
    private String content;

    public TempArticle() {
    }

    public TempArticle(String content) {
        this.id = 1L;
        this.content = content;
    }
}