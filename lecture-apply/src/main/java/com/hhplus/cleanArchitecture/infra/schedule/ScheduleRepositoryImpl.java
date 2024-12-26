package com.hhplus.cleanArchitecture.infra.schedule;

import com.hhplus.cleanArchitecture.domain.entity.Schedule;
import com.hhplus.cleanArchitecture.domain.repository.IScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements IScheduleRepository {
    private final ScheduleJpaRepository scheduleJpaRepository;

    @Override
    public Optional<Schedule> findScheduleWithLockById(Long scheduleId) {
        return scheduleJpaRepository.findScheduleWithLockById(scheduleId);
    }

}
