package myblog.blog.comment.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = 756614064L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QComment comment = new QComment("comment");

    public final myblog.blog.base.domain.QBasicEntity _super = new myblog.blog.base.domain.QBasicEntity(this);

    public final myblog.blog.article.domain.QArticle article;

    public final ListPath<Comment, QComment> child = this.<Comment, QComment>createList("child", Comment.class, QComment.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final NumberPath<Integer> cOrder = createNumber("cOrder", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final myblog.blog.member.doamin.QMember member;

    public final QComment parents;

    public final NumberPath<Integer> pOrder = createNumber("pOrder", Integer.class);

    public final BooleanPath secret = createBoolean("secret");

    public final NumberPath<Integer> tier = createNumber("tier", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QComment(String variable) {
        this(Comment.class, forVariable(variable), INITS);
    }

    public QComment(Path<? extends Comment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QComment(PathMetadata metadata, PathInits inits) {
        this(Comment.class, metadata, inits);
    }

    public QComment(Class<? extends Comment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new myblog.blog.article.domain.QArticle(forProperty("article"), inits.get("article")) : null;
        this.member = inits.isInitialized("member") ? new myblog.blog.member.doamin.QMember(forProperty("member")) : null;
        this.parents = inits.isInitialized("parents") ? new QComment(forProperty("parents"), inits.get("parents")) : null;
    }

}

