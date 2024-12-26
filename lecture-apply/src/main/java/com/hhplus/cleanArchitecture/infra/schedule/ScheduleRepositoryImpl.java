package com.hhplus.cleanArchitecture.infra.schedule;

import com.hhplus.cleanArchitecture.domain.entity.Schedule;
import com.hhplus.cleanArchitecture.domain.repository.IScheduleRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.hhplus.cleanArchitecture.domain.entity.QSchedule.schedule;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements IScheduleRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<Schedule> findScheduleWithLockById(Long scheduleId) {
        Schedule result = queryFactory
                .selectFrom(schedule)
                .where(schedule.id.eq(scheduleId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne();

        return Optional.ofNullable(result);
    }

}
