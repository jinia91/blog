package myblog.blog.tags.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTags is a Querydsl query type for Tags
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTags extends EntityPathBase<Tags> {

    private static final long serialVersionUID = 1580147450L;

    public static final QTags tags = new QTags("tags");

    public final myblog.blog.base.domain.QBasicEntity _super = new myblog.blog.base.domain.QBasicEntity(this);

    public final ListPath<ArticleTagList, QArticleTagList> articleTagLists = this.<ArticleTagList, QArticleTagList>createList("articleTagLists", ArticleTagList.class, QArticleTagList.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QTags(String variable) {
        super(Tags.class, forVariable(variable));
    }

    public QTags(Path<? extends Tags> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTags(PathMetadata metadata) {
        super(Tags.class, metadata);
    }

}

