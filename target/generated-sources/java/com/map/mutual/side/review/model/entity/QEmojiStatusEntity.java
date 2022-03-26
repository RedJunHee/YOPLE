package com.map.mutual.side.review.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEmojiStatusEntity is a Querydsl query type for EmojiStatusEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmojiStatusEntity extends EntityPathBase<EmojiStatusEntity> {

    private static final long serialVersionUID = -1992708369L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEmojiStatusEntity emojiStatusEntity = new QEmojiStatusEntity("emojiStatusEntity");

    public final QEmojiEntity emojiEntity;

    public final NumberPath<Long> emojiId = createNumber("emojiId", Long.class);

    public final NumberPath<Long> reviewId = createNumber("reviewId", Long.class);

    public final StringPath userSuid = createString("userSuid");

    public final NumberPath<Long> worldId = createNumber("worldId", Long.class);

    public QEmojiStatusEntity(String variable) {
        this(EmojiStatusEntity.class, forVariable(variable), INITS);
    }

    public QEmojiStatusEntity(Path<? extends EmojiStatusEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEmojiStatusEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEmojiStatusEntity(PathMetadata metadata, PathInits inits) {
        this(EmojiStatusEntity.class, metadata, inits);
    }

    public QEmojiStatusEntity(Class<? extends EmojiStatusEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.emojiEntity = inits.isInitialized("emojiEntity") ? new QEmojiEntity(forProperty("emojiEntity")) : null;
    }

}

