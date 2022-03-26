package com.map.mutual.side.world.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QWorldEntity is a Querydsl query type for WorldEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorldEntity extends EntityPathBase<WorldEntity> {

    private static final long serialVersionUID = -953504601L;

    public static final QWorldEntity worldEntity = new QWorldEntity("worldEntity");

    public final com.map.mutual.side.common.repository.config.QTimeEntity _super = new com.map.mutual.side.common.repository.config.QTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createTime = _super.createTime;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateTime = _super.updateTime;

    public final StringPath worldDesc = createString("worldDesc");

    public final NumberPath<Long> worldId = createNumber("worldId", Long.class);

    public final StringPath worldName = createString("worldName");

    public final StringPath worldOwner = createString("worldOwner");

    public QWorldEntity(String variable) {
        super(WorldEntity.class, forVariable(variable));
    }

    public QWorldEntity(Path<? extends WorldEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWorldEntity(PathMetadata metadata) {
        super(WorldEntity.class, metadata);
    }

}

