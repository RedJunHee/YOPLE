package com.map.mutual.side.auth.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QJWTRefreshTokenLogEntity is a Querydsl query type for JWTRefreshTokenLogEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJWTRefreshTokenLogEntity extends EntityPathBase<JWTRefreshTokenLogEntity> {

    private static final long serialVersionUID = 1427936574L;

    public static final QJWTRefreshTokenLogEntity jWTRefreshTokenLogEntity = new QJWTRefreshTokenLogEntity("jWTRefreshTokenLogEntity");

    public final StringPath refreshToken = createString("refreshToken");

    public final StringPath userSuid = createString("userSuid");

    public QJWTRefreshTokenLogEntity(String variable) {
        super(JWTRefreshTokenLogEntity.class, forVariable(variable));
    }

    public QJWTRefreshTokenLogEntity(Path<? extends JWTRefreshTokenLogEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJWTRefreshTokenLogEntity(PathMetadata metadata) {
        super(JWTRefreshTokenLogEntity.class, metadata);
    }

}

