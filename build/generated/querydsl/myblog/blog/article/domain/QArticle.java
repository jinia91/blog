package myblog.blog.article.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticle is a Querydsl query type for Article
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QArticle extends EntityPathBase<Article> {

    private static final long serialVersionUID = -1639243056L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticle article = new QArticle("article");

    public final myblog.blog.base.domain.QBasicEntity _super = new myblog.blog.base.domain.QBasicEntity(this);

    public final ListPath<myblog.blog.tags.domain.ArticleTagList, myblog.blog.tags.domain.QArticleTagList> articleTagLists = this.<myblog.blog.tags.domain.ArticleTagList, myblog.blog.tags.domain.QArticleTagList>createList("articleTagLists", myblog.blog.tags.domain.ArticleTagList.class, myblog.blog.tags.domain.QArticleTagList.class, PathInits.DIRECT2);

    public final myblog.blog.category.domain.QCategory category;

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> hit = createNumber("hit", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final myblog.blog.member.doamin.QMember member;

    public final ListPath<myblog.blog.comment.domain.Comment, myblog.blog.comment.domain.QComment> parentCommentList = this.<myblog.blog.comment.domain.Comment, myblog.blog.comment.domain.QComment>createList("parentCommentList", myblog.blog.comment.domain.Comment.class, myblog.blog.comment.domain.QComment.class, PathInits.DIRECT2);

    public final StringPath thumbnailUrl = createString("thumbnailUrl");

    public final StringPath title = createString("title");

    public final StringPath toc = createString("toc");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QArticle(String variable) {
        this(Article.class, forVariable(variable), INITS);
    }

    public QArticle(Path<? extends Article> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticle(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticle(PathMetadata metadata, PathInits inits) {
        this(Article.class, metadata, inits);
    }

    public QArticle(Class<? extends Article> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new myblog.blog.category.domain.QCategory(forProperty("category"), inits.get("category")) : null;
        this.member = inits.isInitialized("member") ? new myblog.blog.member.doamin.QMember(forProperty("member")) : null;
    }

}

