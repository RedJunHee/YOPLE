package com.map.mutual.side.review.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEmojiEntity is a Querydsl query type for EmojiEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmojiEntity extends EntityPathBase<EmojiEntity> {

    private static final long serialVersionUID = 1671664605L;

    public static final QEmojiEntity emojiEntity = new QEmojiEntity("emojiEntity");

    public final com.map.mutual.side.common.repository.config.QTimeEntity _super = new com.map.mutual.side.common.repository.config.QTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createTime = _super.createTime;

    public final NumberPath<Long> emojiId = createNumber("emojiId", Long.class);

    public final EnumPath<com.map.mutual.side.common.enumerate.BooleanType> emojiStatus = createEnum("emojiStatus", com.map.mutual.side.common.enumerate.BooleanType.class);

    public final StringPath emojiValue = createString("emojiValue");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateTime = _super.updateTime;

    public final StringPath userId = createString("userId");

    public QEmojiEntity(String variable) {
        super(EmojiEntity.class, forVariable(variable));
    }

    public QEmojiEntity(Path<? extends EmojiEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEmojiEntity(PathMetadata metadata) {
        super(EmojiEntity.class, metadata);
    }

}

