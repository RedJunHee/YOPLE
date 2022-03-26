package com.map.mutual.side.auth.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSMSRequestLogEntity is a Querydsl query type for SMSRequestLogEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSMSRequestLogEntity extends EntityPathBase<SMSRequestLogEntity> {

    private static final long serialVersionUID = 2126908085L;

    public static final QSMSRequestLogEntity sMSRequestLogEntity = new QSMSRequestLogEntity("sMSRequestLogEntity");

    public final com.map.mutual.side.common.repository.config.QTimeEntity _super = new com.map.mutual.side.common.repository.config.QTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createTime = _super.createTime;

    public final StringPath duid = createString("duid");

    public final StringPath phone = createString("phone");

    public final StringPath requestAuthNum = createString("requestAuthNum");

    public final StringPath responseAuthNum = createString("responseAuthNum");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateTime = _super.updateTime;

    public QSMSRequestLogEntity(String variable) {
        super(SMSRequestLogEntity.class, forVariable(variable));
    }

    public QSMSRequestLogEntity(Path<? extends SMSRequestLogEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSMSRequestLogEntity(PathMetadata metadata) {
        super(SMSRequestLogEntity.class, metadata);
    }

}

