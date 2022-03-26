package com.map.mutual.side.world.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorldUserMappingEntity is a Querydsl query type for WorldUserMappingEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorldUserMappingEntity extends EntityPathBase<WorldUserMappingEntity> {

    private static final long serialVersionUID = -1492750078L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorldUserMappingEntity worldUserMappingEntity = new QWorldUserMappingEntity("worldUserMappingEntity");

    public final com.map.mutual.side.auth.model.entity.QUserEntity userEntity;

    public final StringPath userSuid = createString("userSuid");

    public final QWorldEntity worldEntity;

    public final NumberPath<Long> worldId = createNumber("worldId", Long.class);

    public final StringPath worldUserCode = createString("worldUserCode");

    public QWorldUserMappingEntity(String variable) {
        this(WorldUserMappingEntity.class, forVariable(variable), INITS);
    }

    public QWorldUserMappingEntity(Path<? extends WorldUserMappingEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorldUserMappingEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorldUserMappingEntity(PathMetadata metadata, PathInits inits) {
        this(WorldUserMappingEntity.class, metadata, inits);
    }

    public QWorldUserMappingEntity(Class<? extends WorldUserMappingEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userEntity = inits.isInitialized("userEntity") ? new com.map.mutual.side.auth.model.entity.QUserEntity(forProperty("userEntity")) : null;
        this.worldEntity = inits.isInitialized("worldEntity") ? new QWorldEntity(forProperty("worldEntity")) : null;
    }

}

