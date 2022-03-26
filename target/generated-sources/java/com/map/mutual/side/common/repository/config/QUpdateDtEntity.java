package com.map.mutual.side.common.repository.config;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUpdateDtEntity is a Querydsl query type for UpdateDtEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QUpdateDtEntity extends EntityPathBase<UpdateDtEntity> {

    private static final long serialVersionUID = -2023470645L;

    public static final QUpdateDtEntity updateDtEntity = new QUpdateDtEntity("updateDtEntity");

    public final DateTimePath<java.time.LocalDateTime> updateTime = createDateTime("updateTime", java.time.LocalDateTime.class);

    public QUpdateDtEntity(String variable) {
        super(UpdateDtEntity.class, forVariable(variable));
    }

    public QUpdateDtEntity(Path<? extends UpdateDtEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUpdateDtEntity(PathMetadata metadata) {
        super(UpdateDtEntity.class, metadata);
    }

}

