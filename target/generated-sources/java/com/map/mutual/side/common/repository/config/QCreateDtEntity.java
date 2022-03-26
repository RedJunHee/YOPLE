package com.map.mutual.side.common.repository.config;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCreateDtEntity is a Querydsl query type for CreateDtEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QCreateDtEntity extends EntityPathBase<CreateDtEntity> {

    private static final long serialVersionUID = -1971073282L;

    public static final QCreateDtEntity createDtEntity = new QCreateDtEntity("createDtEntity");

    public final DateTimePath<java.time.LocalDateTime> createTime = createDateTime("createTime", java.time.LocalDateTime.class);

    public QCreateDtEntity(String variable) {
        super(CreateDtEntity.class, forVariable(variable));
    }

    public QCreateDtEntity(Path<? extends CreateDtEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCreateDtEntity(PathMetadata metadata) {
        super(CreateDtEntity.class, metadata);
    }

}

