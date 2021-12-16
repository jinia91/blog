package myblog.blog.member.doamin;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 863663972L;

    public static final QMember member = new QMember("member1");

    public final myblog.blog.base.domain.QBasicEntity _super = new myblog.blog.base.domain.QBasicEntity(this);

    public final ListPath<myblog.blog.article.domain.Article, myblog.blog.article.domain.QArticle> articles = this.<myblog.blog.article.domain.Article, myblog.blog.article.domain.QArticle>createList("articles", myblog.blog.article.domain.Article.class, myblog.blog.article.domain.QArticle.class, PathInits.DIRECT2);

    public final ListPath<myblog.blog.comment.domain.Comment, myblog.blog.comment.domain.QComment> commentList = this.<myblog.blog.comment.domain.Comment, myblog.blog.comment.domain.QComment>createList("commentList", myblog.blog.comment.domain.Comment.class, myblog.blog.comment.domain.QComment.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath picUrl = createString("picUrl");

    public final StringPath provider = createString("provider");

    public final StringPath providerId = createString("providerId");

    public final EnumPath<Role> role = createEnum("role", Role.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final StringPath userId = createString("userId");

    public final StringPath username = createString("username");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

