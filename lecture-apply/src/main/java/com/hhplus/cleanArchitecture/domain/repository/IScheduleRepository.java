package com.hhplus.cleanArchitecture.domain.repository;

import com.hhplus.cleanArchitecture.domain.entity.Schedule;

import java.util.Optional;

public interface IScheduleRepository {
    Optional<Schedule> findScheduleWithLockById(Long scheduleId);
}
