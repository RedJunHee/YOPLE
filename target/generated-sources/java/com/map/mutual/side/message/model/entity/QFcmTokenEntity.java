package com.map.mutual.side.message.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QFcmTokenEntity is a Querydsl query type for FcmTokenEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFcmTokenEntity extends EntityPathBase<FcmTokenEntity> {

    private static final long serialVersionUID = -1813569553L;

    public static final QFcmTokenEntity fcmTokenEntity = new QFcmTokenEntity("fcmTokenEntity");

    public final StringPath title = createString("title");

    public final StringPath userSuid = createString("userSuid");

    public QFcmTokenEntity(String variable) {
        super(FcmTokenEntity.class, forVariable(variable));
    }

    public QFcmTokenEntity(Path<? extends FcmTokenEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFcmTokenEntity(PathMetadata metadata) {
        super(FcmTokenEntity.class, metadata);
    }

}

