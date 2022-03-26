package com.map.mutual.side.review.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewWorldMappingEntity is a Querydsl query type for ReviewWorldMappingEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewWorldMappingEntity extends EntityPathBase<ReviewWorldMappingEntity> {

    private static final long serialVersionUID = 467125699L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewWorldMappingEntity reviewWorldMappingEntity = new QReviewWorldMappingEntity("reviewWorldMappingEntity");

    public final com.map.mutual.side.common.repository.config.QCreateDtEntity _super = new com.map.mutual.side.common.repository.config.QCreateDtEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createTime = _super.createTime;

    public final QReviewEntity reviewEntity;

    public final NumberPath<Long> reviewId = createNumber("reviewId", Long.class);

    public final com.map.mutual.side.world.model.entity.QWorldEntity worldEntity;

    public final NumberPath<Long> worldId = createNumber("worldId", Long.class);

    public QReviewWorldMappingEntity(String variable) {
        this(ReviewWorldMappingEntity.class, forVariable(variable), INITS);
    }

    public QReviewWorldMappingEntity(Path<? extends ReviewWorldMappingEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewWorldMappingEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewWorldMappingEntity(PathMetadata metadata, PathInits inits) {
        this(ReviewWorldMappingEntity.class, metadata, inits);
    }

    public QReviewWorldMappingEntity(Class<? extends ReviewWorldMappingEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reviewEntity = inits.isInitialized("reviewEntity") ? new QReviewEntity(forProperty("reviewEntity"), inits.get("reviewEntity")) : null;
        this.worldEntity = inits.isInitialized("worldEntity") ? new com.map.mutual.side.world.model.entity.QWorldEntity(forProperty("worldEntity")) : null;
    }

}

