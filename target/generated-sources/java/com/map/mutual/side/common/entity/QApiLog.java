package com.map.mutual.side.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QApiLog is a Querydsl query type for ApiLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApiLog extends EntityPathBase<ApiLog> {

    private static final long serialVersionUID = 1262900750L;

    public static final QApiLog apiLog = new QApiLog("apiLog");

    public final com.map.mutual.side.common.repository.config.QCreateDtEntity _super = new com.map.mutual.side.common.repository.config.QCreateDtEntity(this);

    public final StringPath apiDesc = createString("apiDesc");

    public final StringPath apiName = createString("apiName");

    public final ComparablePath<Character> apiStatus = createComparable("apiStatus", Character.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createTime = _super.createTime;

    public final NumberPath<Float> processTime = createNumber("processTime", Float.class);

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public QApiLog(String variable) {
        super(ApiLog.class, forVariable(variable));
    }

    public QApiLog(Path<? extends ApiLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QApiLog(PathMetadata metadata) {
        super(ApiLog.class, metadata);
    }

}

