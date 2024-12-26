package com.hhplus.cleanArchitecture.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRegistration is a Querydsl query type for Registration
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRegistration extends EntityPathBase<Registration> {

    private static final long serialVersionUID = 1139975859L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRegistration registration = new QRegistration("registration");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QLecture lecture;

    public final DatePath<java.time.LocalDate> registeredAt = createDate("registeredAt", java.time.LocalDate.class);

    public final QSchedule schedule;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QRegistration(String variable) {
        this(Registration.class, forVariable(variable), INITS);
    }

    public QRegistration(Path<? extends Registration> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRegistration(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRegistration(PathMetadata metadata, PathInits inits) {
        this(Registration.class, metadata, inits);
    }

    public QRegistration(Class<? extends Registration> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.lecture = inits.isInitialized("lecture") ? new QLecture(forProperty("lecture")) : null;
        this.schedule = inits.isInitialized("schedule") ? new QSchedule(forProperty("schedule"), inits.get("schedule")) : null;
    }

}

