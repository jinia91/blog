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