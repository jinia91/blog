package myblog.blog.tags.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleTagList is a Querydsl query type for ArticleTagList
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QArticleTagList extends EntityPathBase<ArticleTagList> {

    private static final long serialVersionUID = 617904931L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticleTagList articleTagList = new QArticleTagList("articleTagList");

    public final myblog.blog.base.domain.QBasicEntity _super = new myblog.blog.base.domain.QBasicEntity(this);

    public final myblog.blog.article.domain.QArticle article;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QTags tags;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QArticleTagList(String variable) {
        this(ArticleTagList.class, forVariable(variable), INITS);
    }

    public QArticleTagList(Path<? extends ArticleTagList> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticleTagList(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticleTagList(PathMetadata metadata, PathInits inits) {
        this(ArticleTagList.class, metadata, inits);
    }

    public QArticleTagList(Class<? extends ArticleTagList> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new myblog.blog.article.domain.QArticle(forProperty("article"), inits.get("article")) : null;
        this.tags = inits.isInitialized("tags") ? new QTags(forProperty("tags")) : null;
    }

}

